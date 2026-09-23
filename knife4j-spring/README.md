# Spring Boot + Knife4j 部署指南

> **双模式打包**：本项目既支持本地 `java -jar` 直接运行（推荐开发者），也支持一键打包成 WAR 部署到外部 Tomcat（推荐给队友）。
> 详细对比见 [打包模式说明](#-打包模式说明)。

## 📦 前置环境

- ✅ JDK 1.8+（推荐 17）
- ✅ Maven 3.6+
- ✅ 端口 8080 可用

## 🚀 快速启动

### 方式 A：本地开发（java -jar，推荐）

```bash
cd knife4j-spring

# 1. 编译打包（默认打包成可执行的 Fat WAR，含内嵌 Tomcat）
mvn clean package -DskipTests

# 2. 直接启动（与 java -jar xxx.jar 效果完全一致）
java -jar target/knife4j-spring-1.0.0.war

# 或指定端口启动
java -jar target/knife4j-spring-1.0.0.war --server.port=9090
```

启动成功后控制台会输出：

```
========================================
ROC-git Knife4j API 文档启动成功！
本地访问：http://localhost:8080/doc.html
========================================
```

### 方式 B：Maven 直接运行

```bash
mvn spring-boot:run
```

### 方式 C：IDE 运行

在 IntelliJ IDEA / Eclipse 中打开 `knife4j-spring/`，运行 `Application.java` 的 `main` 方法。

### 方式 D：部署到外部 Tomcat（发给队友）

```bash
# 1. 切到 war profile：打包成标准 WAR（不含内嵌 Tomcat）
mvn clean package -DskipTests -Pwar

# 2. 把 target/knife4j-spring-1.0.0.war 复制到 Tomcat 的 webapps/ 目录
copy target\knife4j-spring-1.0.0.war 你的Tomcat路径\webapps\

# 3. 启动 Tomcat
你的Tomcat路径\bin\startup.bat
```

访问：http://localhost:8080/knife4j-spring-1.0.0/doc.html

## 🌐 访问地址

启动成功后访问：

| 地址 | 说明 |
|------|------|
| http://localhost:8080/ | 首页（自动跳转 doc.html） |
| **http://localhost:8080/doc.html** | **Knife4j 文档主页（中文增强版 UI）** |
| http://localhost:8080/swagger-ui/index.html | 官方 Swagger UI |
| http://localhost:8080/v3/api-docs | OpenAPI 3.0 JSON 规范 |
| http://localhost:8080/v3/api-docs.yaml | OpenAPI 3.0 YAML 规范 |
| http://localhost:8080/api/v1/health | 健康检查接口 |
| http://localhost:8080/api/v1/user/profile | 用户信息（需 JWT） |

> 📌 **访问 Tomcat 部署版本**：路径会变成 `http://你的域名:端口/knife4j-spring-1.0.0/doc.html`

## 🎯 打包模式说明

本项目支持两种打包模式，通过 Maven Profile 切换：

### 📊 两种模式对比

| 对比项 | 默认模式（jar/war） | war 模式（`-Pwar`） |
|--------|-------------------|-------------------|
| **打包命令** | `mvn package` | `mvn package -Pwar` |
| **产物文件名** | `knife4j-spring-1.0.0.war` | `knife4j-spring-1.0.0.war` |
| **产物大小** | ~28.8 MB（含内嵌 Tomcat） | ~22.5 MB（不含内嵌 Tomcat） |
| **内嵌 Tomcat** | ✅ 包含 | ❌ 不包含（scope=provided） |
| **启动方式** | `java -jar xxx.war` 直接跑 | 必须丢到外部 Tomcat |
| **适合场景** | 👨‍💻 本地开发、自测 | 📦 发给队友部署到生产/测试服务器 |
| **启动速度** | 快（~10 秒） | 取决于 Tomcat 启动 |
| **打包耗时** | 较长（repackage） | 较快（标准 war） |

### 🔧 工作原理

```
┌─────────────────────────────────────────────────────────────────┐
│                       pom.xml 关键配置                            │
├─────────────────────────────────────────────────────────────────┤
│  <packaging>war</packaging>       ← 固定为 war                  │
│                                                                  │
│  <plugin>spring-boot-maven-plugin</plugin>                      │
│    <skip>      → 默认 false（repackage 启用）                   │
│              → war 模式 true（repackage 跳过）                  │
│                                                                  │
│  <dependency>spring-boot-starter-tomcat</dependency>            │
│    <scope>    → 默认 compile（打包进 WAR 内）                   │
│              → war 模式 provided（不打包）                      │
└─────────────────────────────────────────────────────────────────┘
```

**为什么 jar/war 都能用？**

Spring Boot 2.x 之后，**`war` 类型的 Fat WAR 同样支持 `java -jar` 启动**：
- 默认模式：`spring-boot-maven-plugin` 的 `repackage` 目标会把 war 重新打包成"可执行的 Fat WAR"（包含内嵌 Tomcat 和 `Main-Class` 清单）
- war 模式：跳过 `repackage`，生成标准 WAR，由外部 Tomcat 加载

### 💡 使用建议

```bash
# 👨‍💻 开发者本地（推荐：默认模式）
mvn clean package -DskipTests
java -jar target/knife4j-spring-1.0.0.war

# 📦 部署给队友（用 -Pwar 切到 WAR 模式）
mvn clean package -DskipTests -Pwar
# 把 target/knife4j-spring-1.0.0.war 发给队友
```

## 📁 项目结构

```
knife4j-spring/
├── pom.xml                                       ← Maven 依赖
├── README.md                                     ← 本说明
└── src/main/
    ├── java/com/rocgit/knife4j/
    │   ├── Application.java                      ← Spring Boot 启动类
    │   ├── config/
    │   │   └── Knife4jConfig.java                ← OpenAPI 配置（含 JWT）
    │   └── controller/
    │       ├── CommonController.java             ← 通用模块（无需鉴权）
    │       ├── UserController.java               ← 用户模块（@Operation 示例）
    │       └── GroupController.java              ← 小组模块
    └── resources/
        ├── application.yml                       ← Spring Boot + Knife4j 配置
        └── static/
            └── index.html                        ← 重定向到 doc.html
```

## 🔧 核心注解说明

本项目演示了以下 Knife4j/Swagger 注解：

### Controller 层
- `@Tag(name="用户模块")` — 接口分组（替代老版本 `@Api`）
- `@SecurityRequirement(name="Bearer")` — 整 Controller 需要鉴权
- `@RequestMapping("/api/v1/user")` — 路径前缀

### 方法层
- `@Operation(summary="...", description="...")` — 接口描述
- `@Parameter(description="...", example="...")` — 参数说明

### Knife4j 4.x 自动生成 doc.html
无需手动编写 UI 页面，Knife4j 4.x 自动注入 `/doc.html` 端点。

## 💡 Knife4j 高级特性（已配置）

查看 `application.yml`：

```yaml
knife4j:
  enable: true
  setting:
    language: zh_cn                          # 中文界面
    enable-version: true                     # 启用版本切换
    enable-swagger-models: true              # 显示实体类列表
    swagger-model-name: 实体类列表
    enable-document-manage: true             # 文档管理
  global:
    parameters:                              # 全局公共参数
      - name: token
        in: header
```

这些通过纯静态文档（方式二）**无法实现**，正是 Spring Boot 集成的价值所在。

## 🔄 与方式二（静态部署）的对比

| 特性 | 静态部署（方式二） | Spring Boot（方式一） |
|------|:-----------------:|:--------------------:|
| 部署难度 | ⭐ 极简单 | ⭐⭐⭐ 中等 |
| 后端依赖 | ❌ 不需要 | ✅ 需要 |
| 注解自动生成文档 | ❌ 不支持 | ✅ 支持 |
| 代码改动自动同步 | ❌ 手动维护 | ✅ 自动同步 |
| 全局参数配置 | ❌ 不支持 | ✅ 支持 |
| 在线调试真实 API | 部分（需 CORS） | ✅ 完整支持 |
| 离线部署 | ✅ 完全离线 | ⚠️ 需 Maven 网络 |

## ❓ 常见问题

**Q: Maven 下载依赖太慢？**
A: 配阿里云镜像（settings.xml）:
```xml
<mirror>
  <id>aliyun</id>
  <url>https://maven.aliyun.com/repository/public</url>
  <mirrorOf>central</mirrorOf>
</mirror>
```

**Q: 端口 8080 被占用？**
A: 修改 `application.yml`:
```yaml
server:
  port: 9080
```

**Q: 启动报错找不到 swagger 相关类？**
A: 确认 Maven 已下载 `knife4j-openapi3-spring-boot-starter` 依赖。
