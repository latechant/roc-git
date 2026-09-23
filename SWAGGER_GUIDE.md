# ROC-git Swagger/OpenAPI 接口文档

## 📁 文档说明

本目录包含 ROC-git 课程小组协作管理平台的完整 OpenAPI 3.0 接口规范文档。

## 📋 文件清单

| 文件名 | 说明 |
|--------|------|
| `openapi.yaml` | OpenAPI 3.0 规范文档（YAML 格式） |
| `openapi.json` | OpenAPI 3.0 规范文档（JSON 格式，可选） |
| `SWAGGER_GUIDE.md` | 使用与部署指南（本文件） |

## 🚀 快速开始

### 1. 在线查看（Swagger Editor）

访问 [Swagger Editor](https://editor.swagger.io/)，将 `openapi.yaml` 内容粘贴进去即可在线查看。

或者使用本地版：

```bash
# 启动 Swagger Editor（Docker）
docker run -p 8080:8080 -v $(pwd)/openapi.yaml:/app/openapi.yaml swaggerapi/swagger-editor
```

访问 `http://localhost:8080` 查看。

### 2. Swagger UI 部署

#### 方式一：使用 Swagger UI Docker

```bash
docker run -p 8081:8080 \
  -e SWAGGER_JSON=/api/openapi.json \
  -v $(pwd):/api \
  swaggerapi/swagger-ui
```

访问 `http://localhost:8081`。

> 💡 **推荐使用 Knife4j**（国内更友好、功能更强）：
> 详见 `KNIFE4J_DOC.md`，支持离线文档导出、增强搜索、在线调试等。

#### 方式二：NPM 安装 swagger-ui-dist

```bash
npm install swagger-ui-dist
```

将 `openapi.yaml` 放在 `public` 目录，然后在 `index.html` 中引入：

```html
<!DOCTYPE html>
<html>
<head>
  <title>ROC-git API Documentation</title>
  <link rel="stylesheet" href="node_modules/swagger-ui-dist/swagger-ui.css">
</head>
<body>
  <div id="swagger-ui"></div>
  <script src="node_modules/swagger-ui-dist/swagger-ui-bundle.js"></script>
  <script>
    window.onload = () => {
      SwaggerUIBundle({
        url: "/openapi.yaml",
        dom_id: '#swagger-ui',
        deepLinking: true,
        presets: [
          SwaggerUIBundle.presets.apis
        ]
      });
    }
  </script>
</body>
</html>
```

### 3. Spring Boot 集成（knife4j）

如果使用 Spring Boot，可以直接集成 knife4j：

#### Maven 依赖

```xml
<dependency>
    <groupId>com.github.xiaoymin</groupId>
    <artifactId>knife4j-spring-boot-starter</artifactId>
    <version>4.3.0</version>
</dependency>
```

#### application.yml 配置

```yaml
springdoc:
  swagger-ui:
    path: /swagger-ui.html
  api-docs:
    path: /v3/api-docs
knife4j:
  enable: true
  setting:
    language: zh_cn
```

### 4. 在线 HTML 预览

直接用浏览器打开 `openapi.yaml` 可能无法正常显示，建议使用以下方式：

#### 方式一：使用 VS Code 插件

- 安装 `OpenAPI (Swagger) Editor` 插件
- 直接打开 `openapi.yaml` 即可预览

#### 方式二：使用 Redoc CLI

```bash
# 安装 redoc-cli
npm install -g redoc-cli

# 生成静态 HTML
redoc-cli bundle openapi.yaml -o index.html
```

## 📊 接口统计

| 模块 | 接口数 | 主要功能 |
|------|:------:|---------|
| 通用模块 | 5 | 健康检查、配置、上传 |
| 用户模块 | 9 | 登录、个人信息、统计 |
| 小组模块 | 15 | CRUD、成员管理、统计 |
| 项目模块 | 17 | CRUD、阶段、模板 |
| 文件模块 | 28 | 上传、CRUD、审核、收藏 |
| 版本模块 | 3 | 列表、对比、回溯 |
| 任务模块 | 12 | CRUD、完成、转交、统计 |
| 协作模块 | 18 | 研讨、决策、成果、日志 |
| 消息模块 | 12 | 列表、已读、订阅 |
| 分享模块 | 27 | 生成、访问、访客端 |
| **合计** | **146** | **完整REST API** |

## 🔐 认证方式

所有需要鉴权的接口使用 JWT Token 认证：

```
Header: Authorization: Bearer <token>
```

在 Swagger UI 中，点击右上角 "Authorize" 按钮，输入 Token 即可全局认证。

## 📝 接口模块说明

### 通用模块（5个）
- `/health` - 健康检查
- `/config` - 服务端配置
- `/upload` - 通用文件上传
- `/upload/credentials` - 上传凭证

### 用户模块（9个）
- `/user/login` - 微信登录
- `/user/profile` - 个人信息 CRUD
- `/user/login-history` - 登录历史
- `/user/logout` - 退出登录
- `/user/refresh-token` - 刷新 Token
- `/user/contribution-stats` - 贡献统计
- `/user/bind-wechat` - 绑定微信
- `/feedback` - 意见反馈

### 小组模块（15个）
- 完整 CRUD + 成员管理 + 邀请码 + 统计

### 项目模块（17个）
- 完整 CRUD + 阶段管理 + 模板 + 进度

### 文件模块（28个）
- 上传 + 列表 + 预览 + 下载 + 审核 + 收藏 + 移动 + 重命名 + 批量操作 + 统计

### 版本模块（3个）
- 列表、对比、回溯

### 任务模块（12个）
- CRUD + 完成 + 放弃 + 转交 + 统计 + 到期

### 协作模块（18个）
- 研讨 CRUD + 决策 CRUD + 成果 CRUD + 日志

### 消息模块（12个）
- 列表 + 已读 + 删除 + 订阅 + 公告

### 分享模块（27个）
- 生成 + 管理 + 统计 + 访客端完整功能

## 🛠️ 开发工具

### Mock 数据生成

使用 prism（mock 服务器）：

```bash
# 安装 prism
npm install -g @stoplight/prism-cli

# 启动 mock 服务器
prism mock openapi.yaml
```

访问 `http://localhost:4010` 即可获得 mock 数据。

### 客户端代码生成

#### TypeScript / JavaScript

```bash
npx @openapitools/openapi-generator-cli generate \
  -i openapi.yaml \
  -g typescript-axios \
  -o src/api
```

#### Java

```bash
npx @openapitools/openapi-generator-cli generate \
  -i openapi.yaml \
  -g java \
  -o src/main/java
```

#### Python

```bash
npx @openapitools/openapi-generator-cli generate \
  -i openapi.yaml \
  -g python \
  -o ./client
```

### 接口测试

#### 使用 Postman

1. 打开 Postman
2. 点击 "Import" → "Link"
3. 输入文件 URL 或上传 `openapi.yaml`
4. 自动生成所有接口请求

#### 使用 curl

```bash
# 登录
curl -X POST https://api.rocgit.com/api/v1/user/login \
  -H "Content-Type: application/json" \
  -d '{
    "code": "wx_code_xxxxx",
    "nickname": "张三",
    "avatarUrl": "https://xxx.jpg"
  }'

# 获取个人信息
curl -X GET https://api.rocgit.com/api/v1/user/profile \
  -H "Authorization: Bearer <token>"
```

## 📐 响应格式规范

### 成功响应

```json
{
  "code": 0,
  "message": "success",
  "data": {
    // 业务数据
  },
  "timestamp": 1694697600000
}
```

### 失败响应

```json
{
  "code": 40001,
  "message": "参数错误",
  "data": null,
  "timestamp": 1694697600000
}
```

## ⚠️ 错误码说明

| 错误码 | 含义 | HTTP 状态码 |
|:------:|------|:----:|
| 0 | 成功 | 200 |
| 400 | 请求参数错误 | 400 |
| 401 | 未认证 | 401 |
| 403 | 无权限 | 403 |
| 404 | 资源不存在 | 404 |
| 409 | 资源冲突 | 409 |
| 429 | 请求频繁 | 429 |
| 500 | 服务器错误 | 500 |
| 10001-10099 | 业务错误 | 200 |

## 🔒 安全说明

- 所有接口必须使用 HTTPS
- JWT Token 有效期 7 天，refreshToken 30 天
- 接口限流：单用户 100 次/分钟
- 文件上传限制：50MB
- 文件类型白名单：docx/pdf/jpg/xlsx/pptx/png/mp4 等

## 📚 相关资源

- [OpenAPI 3.0 规范](https://swagger.io/specification/)
- [Swagger Editor](https://editor.swagger.io/)
- [knife4j 文档](https://doc.xiaominfo.com/)
- [Redoc](https://github.com/Redocly/redoc)

## 📮 联系方式

- 邮箱：support@rocgit.com
- 文档版本：v1.0
- 更新时间：2026-09-21
