"""Python 调度 Maven，对真实 Java 类执行单元用例并读取逐条 JUnit 证据。"""
import os
import shutil
import subprocess
import xml.etree.ElementTree as ET
from pathlib import Path
import pytest
from testing.support import case

ROOT = Path(__file__).parent
NAMES = [
    "空MultipartFile拒绝", "零字节文件拒绝", "恰好50MiB允许（虚拟Multipart大小）", "50MiB加1字节拒绝",
    "Unix路径剥离", "Windows路径剥离", "控制字符移除", "大写扩展名支持", "超长文件名保留后缀截断",
    "13种上传白名单", "双后缀exe拒绝", "无扩展名拒绝", "JWT签发解析有效性", "空和错误JWT拒绝",
    "JWT签名篡改拒绝", "JWT过期拒绝", "OPTIONS预检放行", "缺少Authorization返回401",
    "错误Authorization方案拒绝", "无效Bearer返回10003", "合法JWT写入userId",
    "任务进度0保持待处理", "任务进度1变为进行中", "任务进度99保持进行中", "任务进度100完成",
    "缺失任务抛业务异常且不写库", "无备注完成任务", "带备注完成任务", "无理由放弃任务",
    "带理由放弃任务", "转交任务发送通知", "删除任务调用Mapper", "部分更新保留其他字段",
    "DTO校验进度0到100及非法边界", "微信配置缺失快速失败", "无活动项目不查询任务",
    "自定义文件名仍校验白名单", "自定义文件名仍拒绝空文件", "自定义文件名剥离路径", "自定义文件名仍检查50MiB上限",
    "任务订阅关闭不写消息", "审核订阅关闭不写消息", "任务订阅开启写消息", "缺少订阅沿用默认开启",
    "只读分享拒绝下载", "只读分享拒绝预览", "分享拒绝跨项目文件", "分享拒绝未审核文件",
    "有下载权限的已审核文件可下载", "外组用户不能读取项目详情",
]


@pytest.fixture(scope="session")
def java_results(pytestconfig):
    if not pytestconfig.getoption("--java"):
        pytest.skip("未启用 --java，未执行Java白盒")
    maven = shutil.which("mvn.cmd") or shutil.which("mvn")
    if not maven:
        pytest.fail("需要Maven")
    env = os.environ.copy()
    java17 = Path(r"C:\Program Files\Java\jdk-17")
    if java17.exists():
        env["JAVA_HOME"] = str(java17)
        env["PATH"] = str(java17 / "bin") + os.pathsep + env["PATH"]
    from testing.java_bridge import prepare_pom
    pom = prepare_pom()
    command = [maven, "-B", "-f", str(pom),
               "-Dmaven.repo.local=" + str(ROOT / "artifacts/m2"), "clean", "test"]
    # 旧结果必须由 clean 清除，不能把前次XML误判成本次成功。
    result = subprocess.run(command, cwd=ROOT, env=env, capture_output=True, encoding="utf-8", errors="replace", timeout=600)
    out = ROOT / pytestconfig.getoption("--report-dir")
    out.mkdir(parents=True, exist_ok=True)
    (out / "maven.log").write_text(result.stdout + result.stderr, encoding="utf-8")
    reports = list((pom.parent / "target/surefire-reports").glob("TEST-*.xml"))
    assert reports, "未产生JUnit测试结果：" + result.stdout[-3500:] + result.stderr[-1000:]
    parsed = {test.attrib["name"]: test for file in reports for test in ET.parse(file).iter("testcase")}
    if result.returncode != 0 and not any(t.find("failure") is not None or t.find("error") is not None for t in parsed.values()):
        pytest.fail("Maven在测试之外的构建/报告步骤失败：" + result.stdout[-3500:] + result.stderr[-1000:])
    (out / "java-exitcode.txt").write_text(str(result.returncode), encoding="utf-8")
    for file in reports:
        shutil.copy2(file, out / file.name)
    coverage = pom.parent / "target/site/jacoco"
    if coverage.exists():
        shutil.copytree(coverage, out / "java-coverage", dirs_exist_ok=True)
    return parsed


@pytest.mark.whitebox
@pytest.mark.parametrize("name", [pytest.param(f"j{i:02d}", id=f"WB-J{i:02d}",
    marks=case(f"WB-J{i:02d}", "后端内部逻辑", title, "见CoreWhiteboxTest对应j方法", title + "；断言全部满足", "白盒", "边界与分支", "单元测试"))
    for i, title in enumerate(NAMES, 1)])
def test_java_branch(request, java_results, name):
    assert name in java_results, "本次未执行该Java测试：" + name
    result = java_results[name]
    errors = list(result.findall("failure")) + list(result.findall("error"))
    request.node.observed = [{"java_test": name, "time": result.get("time"), "result": "成功" if not errors else [e.text for e in errors]}]
    assert result.find("skipped") is None, "Java用例被跳过"
    assert not errors, "\n".join(e.text or e.get("message", "") for e in errors)
