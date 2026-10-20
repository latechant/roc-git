"""仅通过 SSH 建立私有测试实例；不连接生产业务库，不重启生产服务。"""
import json
import re
import secrets
import shlex
import socketserver
import threading
import time
from pathlib import Path
import paramiko
import requests

ROOT = Path(__file__).resolve().parents[1]


class RemoteLab:
    def __init__(self, host, user, key):
        self.host, self.user, self.key = host, user, key
        self.tag = secrets.token_hex(6)
        self.database = "roc_git_test_" + self.tag
        self.dbuser = "rgt_" + self.tag
        self.password = secrets.token_urlsafe(24)
        self.remote = "/tmp/rocgit-test-" + self.tag
        self.ssh = None
        self.server = None
        self.pid = None
        self.evidence = {}

    def command(self, command, stdin=None, check=True):
        _, out, err = self.ssh.exec_command(command, timeout=90)
        if stdin is not None:
            _.write(stdin)
            _.flush()
            _.channel.shutdown_write()
        text = out.read().decode("utf-8", "replace")
        error = err.read().decode("utf-8", "replace")
        code = out.channel.recv_exit_status()
        if check and code:
            raise RuntimeError(f"远程命令失败({code}): {error[:1500]}")
        return text

    def sql(self, sql):
        assert re.fullmatch(r"roc_git_test_[0-9a-f]{12}", self.database)
        return self.command("mysql --default-character-set=utf8mb4 -N -B -r " + self.database, sql).strip()

    def scalar(self, sql):
        return self.sql(sql).splitlines()[0]

    def start(self):
        war = ROOT / "knife4j-spring/target/knife4j-spring-1.0.0.war"
        if not war.exists():
            raise RuntimeError("请先 mvn -f knife4j-spring/pom.xml -DskipTests package")
        inputs = [ROOT / "knife4j-spring/pom.xml"]
        inputs += list((ROOT / "knife4j-spring/src/main").rglob("*.java"))
        inputs += [p for p in (ROOT / "knife4j-spring/src/main/resources").rglob("*") if p.is_file()]
        if any(p.stat().st_mtime > war.stat().st_mtime for p in inputs):
            raise RuntimeError("后端源码或配置比WAR更新，请先重新mvn package，避免测试旧代码")
        self.ssh = paramiko.SSHClient()
        self.ssh.load_system_host_keys()
        self.ssh.set_missing_host_key_policy(paramiko.RejectPolicy())
        self.ssh.connect(self.host, username=self.user, key_filename=self.key,
                         timeout=20, look_for_keys=False, allow_agent=False)
        production_before = self.command("systemctl is-active rocgit").strip()
        self.command("mkdir -m 700 " + self.remote)
        schema = (ROOT / "database/schema.sql").read_text(encoding="utf-8")
        # 只替换严格匹配的库名，不允许原脚本的 DROP DATABASE roc_git 被提交执行。
        schema = re.sub(r"\broc_git\b", self.database, schema)
        assert "DROP DATABASE IF EXISTS roc_git;" not in schema
        seed = re.sub(r"\broc_git\b", self.database, (ROOT / "database/seed.sql").read_text(encoding="utf-8"))
        self.command("mysql --default-character-set=utf8mb4", schema + "\n" + seed +
            f"\nCREATE USER '{self.dbuser}'@'127.0.0.1' IDENTIFIED BY '{self.password}';"
            f"\nGRANT ALL ON `{self.database}`.* TO '{self.dbuser}'@'127.0.0.1';")
        port = int(self.command("python3 -c 'import socket; s=socket.socket(); s.bind((\"127.0.0.1\",0)); print(s.getsockname()[1]); s.close()'"))
        config = {"server": {"address": "127.0.0.1", "port": port}, "spring": {"datasource": {
            "url": f"jdbc:mysql://127.0.0.1:3306/{self.database}?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true",
            "username": self.dbuser, "password": self.password}},
            "rocgit": {"file": {"storage-dir": self.remote + "/files"}},
            "logging": {"level": {"root": "WARN", "com.rocgit": "WARN"}},
            "mybatis-plus": {"configuration": {"log-impl": "org.apache.ibatis.logging.nologging.NoLoggingImpl"}}}
        with self.ssh.open_sftp() as sftp:
            sftp.put(str(war), self.remote + "/app.war")
            with sftp.file(self.remote + "/application-test.yml", "w") as f:
                f.write(json.dumps(config))
            sftp.chmod(self.remote + "/application-test.yml", 0o600)
        self.pid = int(self.command(f"nohup java -Xms96m -Xmx320m -jar {self.remote}/app.war "
            f"--spring.config.additional-location=file:{self.remote}/application-test.yml "
            f">{self.remote}/app.log 2>&1 </dev/null & echo $!"))
        transport = self.ssh.get_transport()

        class Forward(socketserver.BaseRequestHandler):
            def handle(inner):
                import select
                channel = transport.open_channel("direct-tcpip", ("127.0.0.1", port), inner.request.getpeername())
                try:
                    while True:
                        readable, _, _ = select.select([channel, inner.request], [], [], 20)
                        if not readable:
                            return
                        for source in readable:
                            data = source.recv(65536)
                            if not data:
                                return
                            (inner.request if source is channel else channel).sendall(data)
                finally:
                    channel.close()

        class Server(socketserver.ThreadingTCPServer):
            allow_reuse_address = True
            daemon_threads = True

        self.server = Server(("127.0.0.1", 0), Forward)
        threading.Thread(target=self.server.serve_forever, daemon=True).start()
        self.base = f"http://127.0.0.1:{self.server.server_address[1]}"
        session = requests.Session()
        session.trust_env = False
        for _ in range(90):
            try:
                r = session.get(self.base + "/api/v1/health", timeout=2)
                if r.status_code == 200 and r.json().get("code") == 0:
                    break
            except requests.RequestException:
                pass
            time.sleep(1)
        else:
            raise RuntimeError("隔离实例未就绪：" + self.command("tail -60 " + self.remote + "/app.log"))
        import hashlib
        import subprocess
        source_paths = ["src/utils/api.js", "knife4j-spring/pom.xml", "database/schema.sql", "database/seed.sql"]
        source_paths += [str(p.relative_to(ROOT)).replace("\\", "/") for p in (ROOT / "knife4j-spring/src/main/java").rglob("*.java")]
        self.evidence = {"host": self.host, "database": self.database, "storage": self.remote + "/files",
            "remote_bind": f"127.0.0.1:{port}", "war_sha256": hashlib.sha256(war.read_bytes()).hexdigest(),
            "schema_tables": int(self.scalar("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema=DATABASE()")),
            "production_before": production_before, "source": "本地仓库编译 WAR；仓库 schema+seed；实际 MySQL 8"}
        self.evidence["git_head"] = subprocess.check_output(["git", "rev-parse", "HEAD"], cwd=ROOT, text=True).strip()
        self.evidence["source_sha256"] = {p: hashlib.sha256((ROOT / p).read_bytes()).hexdigest() for p in source_paths}

    def close(self, output):
        if not self.ssh:
            return
        errors = []
        try:
            if self.pid:
                # 校验 PID 命令行的精确测试目录，防止杀到其他进程。
                cmd = self.command(f"cat /proc/{self.pid}/cmdline", check=False)
                if self.remote + "/app.war" in cmd:
                    self.command(f"kill {self.pid}")
                    for _ in range(30):
                        state = self.command(f"ps -p {self.pid} -o stat=", check=False).strip()
                        if not state or state.startswith("Z"):
                            break
                        time.sleep(0.3)
                    else:
                        raise RuntimeError("测试进程退出超时，保留目录供检查")
            log = self.command("cat " + self.remote + "/app.log", check=False)
            output.mkdir(parents=True, exist_ok=True)
            (output / "server.log").write_text(log.replace(self.password, "<已脱敏>"), encoding="utf-8")
            assert re.fullmatch(r"roc_git_test_[0-9a-f]{12}", self.database)
            assert re.fullmatch(r"rgt_[0-9a-f]{12}", self.dbuser)
            self.command("mysql", f"DROP DATABASE IF EXISTS `{self.database}`; DROP USER IF EXISTS '{self.dbuser}'@'127.0.0.1';")
            assert re.fullmatch(r"/tmp/rocgit-test-[0-9a-f]{12}", self.remote)
            self.command("python3 -c " + shlex.quote("import shutil; shutil.rmtree(" + repr(self.remote) + ")"))
            self.evidence["cleanup"] = "测试进程停止；测试库、专用账号、测试文件已删除"
            self.evidence["production_after"] = self.command("systemctl is-active rocgit").strip()
        except Exception as exc:
            errors.append(str(exc))
            self.evidence["cleanup_error"] = errors
        finally:
            if self.server:
                self.server.shutdown()
                self.server.server_close()
            self.ssh.close()
            output.mkdir(parents=True, exist_ok=True)
            (output / "environment.json").write_text(json.dumps(self.evidence, ensure_ascii=False, indent=2), encoding="utf-8")
        if errors:
            raise RuntimeError("测试环境清理失败：" + "; ".join(errors))


class Api:
    def __init__(self, lab, node):
        self.lab, self.node = lab, node
        self.token = None
        self.session = requests.Session()
        self.session.trust_env = False

    def call(self, method, path, token=None, **kwargs):
        from testing.support import compact
        headers = kwargs.pop("headers", {})
        auth = self.token if token is None else token
        if auth:
            headers["Authorization"] = "Bearer " + auth
        response = self.session.request(method, self.lab.base + "/api/v1" + path,
            headers=headers, timeout=30, **kwargs)
        try:
            body = response.json()
        except ValueError:
            body = {"http": response.status_code, "body": response.text[:300]}
        if not hasattr(self.node, "observed"):
            self.node.observed = []
        self.node.observed.append({"method": method, "path": path, "http": response.status_code,
                                   "body": compact(body)})
        return body

    def ok(self, method, path, **kwargs):
        body = self.call(method, path, **kwargs)
        assert body.get("code") == 0, body
        return body.get("data")

    def upload(self, world, name="report.txt", content=b"version one\n", **fields):
        return self.ok("POST", "/file/upload", data={"projectId": world["pid"], **fields},
                       files={"file": (name, content, "application/octet-stream")})

    def create_task(self, world, **fields):
        from datetime import datetime, timedelta
        return self.ok("POST", "/task/create", json={"projectId": world["pid"], "name": "测试任务",
            "assigneeId": world["leader"]["userId"],
            "deadline": (datetime.now() + timedelta(days=2)).strftime("%Y-%m-%d %H:%M:%S"), **fields})
