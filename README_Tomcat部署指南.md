# Tomcat 部署指南（WAR 包方式）

> 📦 **部署包**：`knife4j-spring-1.0.0.war`（约 22.7MB）
> 🎯 **目标**：让队友把 WAR 包丢到 Tomcat 上就能访问 Knife4j API 文档

---

## 📋 部署文件清单

收到这个包时，应该包含：

```
📦 部署包
├── knife4j-spring-1.0.0.war        ← 部署包主体（22.7MB）
├── API_团队使用指南.md              ← 接口使用指南
├── API接口使用文档.md                ← 完整接口文档
└── README（本文档）                 ← 部署指南
```

---

## 🚀 快速部署（5 分钟）

### 步骤 1：确认环境

| 必备项 | 版本要求 | 检查命令 |
|--------|----------|----------|
| **JDK** | 1.8+ | `java -version` |
| **Tomcat** | 8.5+ / 9.x / 10.x | `$TOMCAT_HOME/bin/version.sh` (Linux)<br>`$TOMCAT_HOME\bin\version.bat` (Windows) |

> ⚠️ **JDK 17 + Tomcat 10** 时，servlet API 是 `jakarta.*` 包，需调整源码。本项目使用 **javax.***，推荐 **JDK 8/11 + Tomcat 9**。

### 步骤 2：把 WAR 包丢到 Tomcat

#### Linux / macOS：
```bash
cp knife4j-spring-1.0.0.war $TOMCAT_HOME/webapps/
$TOMCAT_HOME/bin/startup.sh
```

#### Windows：
```cmd
copy knife4j-spring-1.0.0.war %TOMCAT_HOME%\webapps\
%TOMCAT_HOME%\bin\startup.bat
```

### 步骤 3：等待自动部署

Tomcat 启动时会自动解压 WAR 包并部署。看到这种日志表示成功：

```
INFO: Deployment of web application archive [knife4j-spring-1.0.0.war] has finished in [3,XXX] ms
INFO: Starting Coyote HTTP/1.1 on http-8080
INFO: Starting Servlet Engine: Apache Tomcat/9.0.x
INFO: Starting ProtocolHandler ["http-bio-8080"]
INFO: Tomcat started on port(s): 8080 (http)
```

### 步骤 4：访问 Knife4j

打开浏览器：

| URL | 说明 |
|-----|------|
| **http://localhost:8080/knife4j-spring-1.0.0/doc.html** | ⭐ 完整 Knife4j 文档（推荐） |
| http://localhost:8080/knife4j-spring-1.0.0/swagger-ui/index.html | Swagger UI |
| http://localhost:8080/knife4j-spring-1.0.0/v3/api-docs | OpenAPI JSON |
| http://localhost:8080/knife4j-spring-1.0.0/api/v1/health | 健康检查 |

---

## ⚙️ 高级配置

### 🎯 方案 A：去掉 URL 里的版本号路径（推荐）

默认访问路径是 `/knife4j-spring-1.0.0/doc.html`，如果你想去掉版本号路径（直接 `/doc.html`）：

#### 方式 1：把 WAR 重命名为 ROOT.war
```bash
# Linux/macOS
mv knife4j-spring-1.0.0.war $TOMCAT_HOME/webapps/ROOT.war
$TOMCAT_HOME/bin/startup.sh
```

```cmd
:: Windows
rename knife4j-spring-1.0.0.war ROOT.war
move ROOT.war %TOMCAT_HOME%\webapps\
%TOMCAT_HOME%\bin\startup.bat
```

> 重命名为 `ROOT.war` 后，Tomcat 会把它当作根应用，访问地址变为 `http://localhost:8080/doc.html`。

#### 方式 2：修改 server.xml
在 `$TOMCAT_HOME/conf/server.xml` 的 `<Host>` 节点添加：

```xml
<Context path="" docBase="knife4j-spring-1.0.0" reloadable="false" />
```

### 🔧 方案 B：修改 Tomcat 端口

默认 Tomcat 端口是 8080，如果被占用：

1. 编辑 `$TOMCAT_HOME/conf/server.xml`
2. 找到 `<Connector port="8080">` 改成 `8081`、`8888` 等
3. 重启 Tomcat

### 🌐 方案 C：绑定域名

在 `server.xml` 的 `<Engine>` 下添加：

```xml
<Host name="api.rocgit.com"  appBase="webapps"
      unpackWARs="true" autoDeploy="true">
  <Alias>api.rocgit.local</Alias>
  <Context path="" docBase="knife4j-spring-1.0.0" />
</Host>
```

然后访问 `http://api.rocgit.com/doc.html`。

---

## 🔐 生产环境建议

### 1. 修改 JWT Secret

默认的 JWT 密钥是开发用的，生产环境**必须修改**：

编辑源码中的 [`JwtUtil.java`](./knife4j-spring/src/main/java/com/rocgit/knife4j/config/JwtUtil.java) 或在外部配置文件覆盖：

```yaml
# 在 Tomcat 中通过环境变量传入
JAVA_OPTS="-Djwt.secret=your-real-secret-key-with-at-least-32-chars"
```

### 2. 关闭 Knife4j 的生产特性

Spring Boot 生产环境（`spring.profiles.active=prod`）下，**强烈建议**关闭 Knife4j 文档，避免泄露 API：

```yaml
spring:
  profiles:
    active: prod
knife4j:
  enable: false
springdoc:
  api-docs:
    enabled: false
  swagger-ui:
    enabled: false
```

### 3. 配置 HTTPS

1. 准备 SSL 证书（`.pfx` 或 `.jks`）
2. 编辑 `$TOMCAT_HOME/conf/server.xml`：

```xml
<Connector port="443" protocol="org.apache.coyote.http11.Http11NioProtocol"
           maxThreads="200" SSLEnabled="true" scheme="https" secure="true"
           keystoreFile="conf/ssl/your-cert.pfx"
           keystorePass="your-password"
           clientAuth="false" sslProtocol="TLS" />
```

3. 添加 80 → 443 重定向：

```xml
<Connector port="80" protocol="HTTP/1.1" redirectPort="443" />
```

### 4. 配置 JVM 参数

在 `$TOMCAT_HOME/bin/catalina.sh`（或 `catalina.bat`）开头添加：

```bash
JAVA_OPTS="$JAVA_OPTS -Xms512m -Xmx2g -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=512m"
JAVA_OPTS="$JAVA_OPTS -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
JAVA_OPTS="$JAVA_OPTS -Dfile.encoding=UTF-8 -Duser.timezone=GMT+8"
```

### 5. 设置开机自启动（Linux）

```bash
# 在 /etc/systemd/system/ 创建 tomcat.service
[Unit]
Description=Apache Tomcat Web Application Container
After=network.target

[Service]
Type=forking
Environment=JAVA_HOME=/usr/lib/jvm/java-8-openjdk
Environment=CATALINA_PID=/opt/tomcat/temp/tomcat.pid
Environment=CATALINA_HOME=/opt/tomcat
Environment=CATALINA_BASE=/opt/tomcat
ExecStart=/opt/tomcat/bin/startup.sh
ExecStop=/opt/tomcat/bin/shutdown.sh
User=tomcat
Group=tomcat
Restart=on-failure

[Install]
WantedBy=multi-user.target
```

```bash
systemctl daemon-reload
systemctl enable tomcat
systemctl start tomcat
```

---

## 🔍 部署验证清单

部署完成后，按顺序检查：

- [ ] 1. Tomcat 启动无报错（`logs/catalina.out` 无 `Exception`）
- [ ] 2. WAR 包自动解压到 `webapps/knife4j-spring-1.0.0/` 目录
- [ ] 3. 访问 `http://localhost:8080/knife4j-spring-1.0.0/api/v1/health` 返回 200
- [ ] 4. 访问 `http://localhost:8080/knife4j-spring-1.0.0/doc.html` 能看到 Knife4j UI
- [ ] 5. Knife4j 中能展开至少 3 个模块（用户、小组、项目）
- [ ] 6. 测试调用 `/api/v1/user/login` 能成功返回 token

---

## ❓ 常见问题

### Q1: 部署后 404？

**原因**：访问路径不对。

**解决**：
- 默认路径：`http://localhost:8080/knife4j-spring-1.0.0/doc.html`
- 如果重命名为 ROOT.war：`http://localhost:8080/doc.html`

### Q2: 启动报错 "javax.servlet" 找不到？

**原因**：Tomcat 版本不对（用的是 Tomcat 10+）。

**解决**：
- Tomcat 10+ 使用 `jakarta.*` 包，本项目使用 `javax.*`
- 改用 Tomcat 9 或 8.5

### Q3: 中文乱码？

**解决**：
1. 确认 `application.yml` 中 `spring.jackson.date-format` 是 `yyyy-MM-dd HH:mm:ss`
2. Tomcat 启动参数加上 `-Dfile.encoding=UTF-8`
3. 连接 MySQL 时 URL 加 `useUnicode=true&characterEncoding=UTF-8`

### Q4: 文件上传大小限制？

编辑 `$TOMCAT_HOME/conf/server.xml` 的 Connector，添加：

```xml
<Connector port="8080" protocol="HTTP/1.1"
           maxPostSize="-1"        <!-- 不限制 POST 大小 -->
           maxSavePostSize="-1" />
```

或在 `application.yml` 中：

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 100MB
      max-request-size: 200MB
```

### Q5: 上传文件后中文文件名乱码？

**Windows Tomcat 部署时常见**。在 Connector 上加 `URIEncoding="UTF-8"`：

```xml
<Connector port="8080" URIEncoding="UTF-8" />
```

### Q6: Knife4j 页面空白？

**可能原因**：
1. Knife4j 静态资源被拦截（404 `/webjars/**`）
2. 自定义拦截器把 Knife4j 路径拦截了

**解决**：在 `WebMvcConfig.java` 中放行 Knife4j 路径：

```java
registry.addPathPatterns("/**")
        .excludePathPatterns(
            "/doc.html", "/webjars/**", "/swagger-resources/**",
            "/v3/api-docs", "/v2/api-docs", "/error"
        );
```

### Q7: 部署到 Windows 上的 Tomcat，日志乱码？

修改 `$TOMCAT_HOME/conf/logging.properties`：

```properties
java.util.logging.ConsoleHandler.encoding = GBK
```

---

## 📦 离线部署方案

如果服务器**无法联网**（Maven 依赖无法下载）：

### 方案 1：使用本地 Maven 仓库
```bash
# 在能联网的机器上
mvn dependency:go-offline -Dmaven.repo.local=/path/to/offline-repo

# 把 offline-repo 整个拷贝到服务器
# 服务器启动 Maven 时指定
mvn -o clean package -Dmaven.repo.local=/path/to/offline-repo
```

### 方案 2：使用 mvn dependency:copy-dependencies
```bash
# 把所有依赖复制到 target/lib
mvn dependency:copy-dependencies -DoutputDirectory=target/lib

# 部署时把 lib/ 下的 JAR 都复制到 $TOMCAT_HOME/lib/
```

### 方案 3：使用 Docker（如果服务器有 Docker）
```bash
docker pull tomcat:9.0
docker run -d --name rocgit-tomcat \
  -p 8080:8080 \
  -v /path/to/knife4j-spring-1.0.0.war:/usr/local/tomcat/webapps/ROOT.war \
  tomcat:9.0
```

---

## 📊 WAR 包目录结构

部署后 WAR 会被自动解压成：

```
$TOMCAT_HOME/webapps/knife4j-spring-1.0.0/
├── WEB-INF/
│   ├── classes/                    ← class 文件
│   │   ├── com/rocgit/knife4j/...
│   │   └── application.yml
│   └── lib/                        ← 依赖 JAR
│       ├── spring-boot-2.7.18.jar
│       ├── knife4j-openapi3-spring-boot-starter-4.4.0.jar
│       ├── jjwt-*.jar
│       └── ... (约 80 个 JAR)
├── META-INF/
├── static/                         ← 静态资源
│   └── index.html
└── doc.html, swagger-ui/...        ← Knife4j 资源
```

---

## 🎁 一键部署脚本

### Windows 部署脚本 `deploy.bat`

```batch
@echo off
chcp 65001
setlocal

:: ===== 配置区域 =====
set TOMCAT_HOME=D:\apache-tomcat-9.0.85
set WAR_NAME=knife4j-spring-1.0.0.war
set APP_NAME=knife4j-spring-1.0.0

echo === 部署 ROC-git Knife4j 文档 ===
echo.

:: 1. 检查 Tomcat 是否安装
if not exist "%TOMCAT_HOME%\bin\startup.bat" (
    echo [错误] Tomcat 未找到：%TOMCAT_HOME%
    pause
    exit /b 1
)

:: 2. 停止 Tomcat
echo [1/5] 停止 Tomcat...
call "%TOMCAT_HOME%\bin\shutdown.bat" > nul 2>&1
timeout /t 5 /nobreak > nul

:: 3. 删除旧的部署
echo [2/5] 清理旧部署...
if exist "%TOMCAT_HOME%\webapps\%APP_NAME%" (
    rd /s /q "%TOMCAT_HOME%\webapps\%APP_NAME%"
)
if exist "%TOMCAT_HOME%\webapps\%WAR_NAME%" (
    del /q "%TOMCAT_HOME%\webapps\%WAR_NAME%"
)
if exist "%TOMCAT_HOME%\work\Catalina\localhost\%APP_NAME%" (
    rd /s /q "%TOMCAT_HOME%\work\Catalina\localhost\%APP_NAME%"
)

:: 4. 拷贝新 WAR
echo [3/5] 拷贝 WAR 包...
copy /y "%~dp0%WAR_NAME%" "%TOMCAT_HOME%\webapps\"

:: 5. 启动 Tomcat
echo [4/5] 启动 Tomcat...
call "%TOMCAT_HOME%\bin\startup.bat"

:: 6. 等待部署完成
echo [5/5] 等待部署完成...
timeout /t 15 /nobreak > nul

echo.
echo === 部署完成 ===
echo 访问地址：
echo   Knife4j:   http://localhost:8080/%APP_NAME%/doc.html
echo   Swagger:   http://localhost:8080/%APP_NAME%/swagger-ui/index.html
echo.
pause
```

### Linux/macOS 部署脚本 `deploy.sh`

```bash
#!/bin/bash

# ===== 配置区域 =====
TOMCAT_HOME=/opt/apache-tomcat-9.0.85
WAR_NAME=knife4j-spring-1.0.0.war
APP_NAME=knife4j-spring-1.0.0

echo "=== 部署 ROC-git Knife4j 文档 ==="

# 1. 检查 Tomcat
if [ ! -d "$TOMCAT_HOME" ]; then
    echo "[错误] Tomcat 未找到：$TOMCAT_HOME"
    exit 1
fi

# 2. 停止 Tomcat
echo "[1/5] 停止 Tomcat..."
$TOMCAT_HOME/bin/shutdown.sh > /dev/null 2>&1
sleep 5

# 3. 清理旧部署
echo "[2/5] 清理旧部署..."
rm -rf $TOMCAT_HOME/webapps/$APP_NAME
rm -f  $TOMCAT_HOME/webapps/$WAR_NAME
rm -rf $TOMCAT_HOME/work/Catalina/localhost/$APP_NAME

# 4. 拷贝新 WAR
echo "[3/5] 拷贝 WAR 包..."
cp $WAR_NAME $TOMCAT_HOME/webapps/

# 5. 启动 Tomcat
echo "[4/5] 启动 Tomcat..."
$TOMCAT_HOME/bin/startup.sh

# 6. 等待部署
echo "[5/5] 等待部署完成..."
sleep 15

echo ""
echo "=== 部署完成 ==="
echo "访问地址："
echo "  Knife4j:   http://localhost:8080/$APP_NAME/doc.html"
echo "  Swagger:   http://localhost:8080/$APP_NAME/swagger-ui/index.html"
```

---

## 📋 部署完成后联系

如果遇到问题，截图以下信息发给 API 维护者：

1. **Tomcat 版本**：`$TOMCAT_HOME/bin/version.sh` 输出
2. **JDK 版本**：`java -version` 输出
3. **错误日志**：`$TOMCAT_HOME/logs/catalina.out` 最后 50 行
4. **WAR 包**：`knife4j-spring-1.0.0.war`（22.7MB）
5. **访问 URL**：你输入的完整 URL

---

## 🎯 完整部署检查单

部署后按顺序勾选：

- [ ] JDK 8/11 已安装
- [ ] Tomcat 9 已安装
- [ ] WAR 包拷贝到 `webapps/`
- [ ] Tomcat 启动无 ERROR 日志
- [ ] `webapps/knife4j-spring-1.0.0/` 目录已生成
- [ ] `http://localhost:8080/knife4j-spring-1.0.0/api/v1/health` 返回 200
- [ ] Knife4j UI 正常显示（中文、14 个模块）
- [ ] 登录接口能成功返回 token
- [ ] 把 4.4 节「高级配置」按需配置（端口、域名、HTTPS等）

---

**文档版本**：v1.0.0
**适用版本**：knife4j-spring-1.0.0.war
**最后更新**：2026-09-22
