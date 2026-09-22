# ROC-git API 接口使用文档

> **版本**：v1.0.0
> **更新时间**：2026-09-22
> **Base URL**：`http://localhost:8080`（开发环境）
>
> **Knife4j 在线文档**：`http://localhost:8080/doc.html`（推荐）
> **Swagger UI**：`http://localhost:8080/swagger-ui/index.html`

---

## 📌 快速开始

### 1. 环境说明

| 环境 | Base URL | 说明 |
|------|----------|------|
| 开发环境 | `http://localhost:8080` | 本地启动后端服务 |
| 测试环境 | `http://api-test.rocgit.com` | （待配置） |
| 生产环境 | `https://api.rocgit.com` | （待配置） |

### 2. 认证方式

本系统使用 **JWT Bearer Token** 认证。

**登录获取 Token**：

```http
POST /api/v1/user/login
Content-Type: application/json

{
  "code": "微信登录code",
  "nickname": "张三",
  "avatarUrl": "https://thirdwx.qlogo.cn/xxx"
}
```

**响应**：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": 10001,
    "openid": "oUpF8uMuAJ...",
    "nickname": "张三",
    "avatarUrl": "https://thirdwx.qlogo.cn/xxx",
    "isNewUser": true
  }
}
```

**后续请求携带 Token**：

```http
GET /api/v1/user/profile
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

## 📋 通用规范

### 统一响应格式

所有接口均返回以下 JSON 结构：

```json
{
  "code": 200,
  "msg": "success",
  "data": { ... }
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | integer | 状态码，200=成功，其他=错误 |
| `msg` | string | 状态信息 |
| `data` | object/array | 响应数据（无数据时为 `null`） |

### 分页响应格式

列表类接口返回分页数据：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "total": 100,
    "list": [ ... ]
  }
}
```

分页参数通过 Query 参数传递：

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `pageNum` | integer | 1 | 页码 |
| `pageSize` | integer | 20 | 每页条数（最大 100） |

### 错误码说明

| code | 说明 | 处理建议 |
|------|------|----------|
| 200 | 成功 | - |
| 400 | 参数错误 | 检查请求参数格式 |
| 401 | 未登录 / Token 失效 | 重新登录获取 Token |
| 403 | 无权限 | 检查用户角色权限 |
| 404 | 资源不存在 | 检查 ID 是否正确 |
| 409 | 业务冲突 | 如邀请码已失效、文件已存在 |
| 500 | 服务器错误 | 联系后端开发人员 |

### 请求头规范

| Header | 必填 | 说明 |
|--------|------|------|
| `Content-Type` | POST/PUT 必填 | `application/json` |
| `Authorization` | 需认证接口必填 | `Bearer {token}` |

---

## 🔐 模块一：通用接口（01）

### 健康检查

```
GET /api/v1/health
```

无需认证。用于检查服务是否存活。

**响应示例**：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "status": "UP",
    "version": "1.0.0",
    "timestamp": 1726983600000
  }
}
```

---

### 获取服务端配置

```
GET /api/v1/config
```

无需认证。获取小程序版本信息等配置。

**响应示例**：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "minVersion": "1.0.0",
    "latestVersion": "1.0.0",
    "maintenanceMode": false,
    "announcement": "欢迎使用 ROC-git 协作平台"
  }
}
```

---

### 文件上传（通用）

```
POST /api/v1/upload
Content-Type: multipart/form-data
```

上传头像、海报等通用文件。

**请求参数**：

| 参数 | 位置 | 类型 | 必填 | 说明 |
|------|------|------|------|------|
| `file` | body | binary | ✅ | 文件二进制内容 |
| `type` | body | string | 否 | 上传类型：`file`/`avatar`/`poster` |
| `projectId` | body | long | 否 | 所属项目ID |

**响应示例**：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "fileId": 1726983600001,
    "url": "https://cdn.rocgit.com/files/abc123.png"
  }
}
```

---

### 获取上传凭证（STS）

```
POST /api/v1/upload/credentials
Authorization: Bearer {token}
```

获取阿里云 OSS / 腾讯云 COS 的临时上传凭证，适合大文件直传场景。

**响应示例**：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "accessKeyId": "STS.xxxxxx",
    "accessKeySecret": "xxxxxx",
    "securityToken": "xxxxxx",
    "expiration": 1726987200000
  }
}
```

---

### 提交反馈

```
POST /api/v1/feedback
Content-Type: application/json
```

无需认证。提交问题反馈或建议。

**请求体**：

```json
{
  "type": "bug",
  "content": "登录页面偶尔会闪退，已录制视频",
  "images": [
    "https://cdn.rocgit.com/feedback/1.png",
    "https://cdn.rocgit.com/feedback/2.png"
  ],
  "contact": "QQ: 123456"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `type` | string | ✅ | 反馈类型：`bug`/`suggestion`/`complaint` |
| `content` | string | ✅ | 反馈内容，5-500 字 |
| `images` | string[] | 否 | 截图 URL，最多 3 张 |
| `contact` | string | 否 | 联系方式 |

---

## 👤 模块二：用户接口（02）

### 微信登录

```
POST /api/v1/user/login
Content-Type: application/json
```

通过微信授权 code 完成登录，首次登录自动注册。

**请求体**：

```json
{
  "code": "081C4t0000xxxxx",
  "nickname": "张三",
  "avatarUrl": "https://thirdwx.qlogo.cn/xxx"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `code` | string | ✅ | 微信登录 code（wx.login 获取） |
| `nickname` | string | ✅ | 用户昵称 |
| `avatarUrl` | string | ✅ | 头像 URL |

**响应**：`Result<LoginVO>`（见上方「认证方式」）

---

### 获取个人信息

```
GET /api/v1/user/profile
Authorization: Bearer {token}
```

获取当前登录用户详细信息。

**响应示例**：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "userId": 10001,
    "openid": "oUpF8uMuAJ...",
    "nickname": "张三",
    "avatarUrl": "https://cdn.rocgit.com/avatar/1.jpg",
    "createdAt": "2026-09-01 10:00:00",
    "groupCount": 3,
    "projectCount": 5
  }
}
```

---

### 更新个人信息

```
PUT /api/v1/user/profile
Authorization: Bearer {token}
Content-Type: application/json
```

**请求体**：

```json
{
  "nickname": "新昵称",
  "avatarUrl": "https://cdn.rocgit.com/avatar/new.jpg"
}
```

所有字段均为可选，只传需要修改的字段。

---

### 退出登录

```
POST /api/v1/user/logout
Authorization: Bearer {token}
```

清除当前会话。

---

### 刷新 Token

```
POST /api/v1/user/refresh-token
Authorization: Bearer {token}
```

Token 即将过期时换取新 Token。

**响应**：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9.newtoken",
    "expiresIn": 7200
  }
}
```

---

### 登录历史

```
GET /api/v1/user/login-history
Authorization: Bearer {token}
```

查询近 30 天登录记录。

**响应**：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "total": 15,
    "list": [
      {
        "loginTime": "2026-09-22 10:30:00",
        "device": "iPhone 15 Pro",
        "ip": "192.168.1.1"
      }
    ]
  }
}
```

---

### 贡献统计

```
GET /api/v1/user/contribution-stats
Authorization: Bearer {token}
```

获取当前用户的贡献数据（上传文件数、完成任务数等）。

**响应示例**：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "uploadedFiles": 28,
    "approvedFiles": 25,
    "completedTasks": 12,
    "discussions": 8,
    "joinDays": 365,
    "lastActiveTime": "2026-09-22 10:30:00"
  }
}
```

---

### 绑定微信

```
POST /api/v1/user/bind-wechat
Authorization: Bearer {token}
Content-Type: application/json
```

将微信 openId 绑定到已有账号（用于非首次登录用户绑定微信）。

**请求体**：

```json
{
  "code": "微信code",
  "nickname": "张三",
  "avatarUrl": "https://thirdwx.qlogo.cn/xxx"
}
```

---

## 👥 模块三：小组接口（03）

### 创建小组

```
POST /api/v1/group/create
Authorization: Bearer {token}
Content-Type: application/json
```

创建新的课程小组（创建者自动成为组长）。

**请求体**：

```json
{
  "name": "课程设计第十二组",
  "description": "负责软件设计实践课程项目"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `name` | string | ✅ | 小组名称，2-20 字 |
| `description` | string | 否 | 小组描述 |

**响应**：`Result<GroupVO>`

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "groupId": 1,
    "name": "课程设计第十二组",
    "description": "负责软件设计实践课程项目",
    "leaderId": 10001,
    "leaderName": "张三",
    "inviteCode": "ABC123",
    "qrCodeUrl": "https://cdn.rocgit.com/qr/abc123.png",
    "createdAt": "2026-09-01 10:00:00"
  }
}
```

---

### 加入小组

```
POST /api/v1/group/join
Authorization: Bearer {token}
Content-Type: application/json
```

通过邀请码加入小组。

**请求体**：

```json
{
  "inviteCode": "ABC123"
}
```

---

### 校验邀请码

```
POST /api/v1/group/validate-invite-code
Authorization: Bearer {token}
Content-Type: application/json
```

在加入前预览小组信息。

**请求体**：

```json
{
  "inviteCode": "ABC123"
}
```

**响应**：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "valid": true,
    "groupName": "课程设计第十二组",
    "memberCount": 5,
    "leaderName": "张三"
  }
}
```

---

### 我的小组列表

```
GET /api/v1/group/my-list
Authorization: Bearer {token}
```

获取当前用户加入的所有小组。

**响应**：`Result<List<GroupVO>>`

---

### 小组详情

```
GET /api/v1/group/{groupId}
Authorization: Bearer {token}
```

获取指定小组详细信息。

**路径参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `groupId` | long | ✅ | 小组 ID |

---

### 更新小组信息

```
PUT /api/v1/group/{groupId}
Authorization: Bearer {token}
Content-Type: application/json
```

仅组长可用。

**请求体**：

```json
{
  "name": "新课程设计组",
  "description": "新的描述"
}
```

---

### 退出小组

```
POST /api/v1/group/{groupId}/leave
Authorization: Bearer {token}
```

退出小组（组长不能退出，需先转让组长）。

---

### 小组成员列表

```
GET /api/v1/group/{groupId}/members
Authorization: Bearer {token}
```

**Query 参数**：

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| `pageNum` | int | 否 | 1 | 页码 |
| `pageSize` | int | 否 | 20 | 每页数量 |

**响应**：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "total": 5,
    "members": [
      {
        "userId": 10001,
        "nickname": "张三",
        "avatarUrl": "https://cdn.rocgit.com/avatar/1.jpg",
        "role": "leader",
        "joinedAt": "2026-09-01 10:00:00",
        "status": "active",
        "remark": "组长"
      }
    ]
  }
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| `role` | string | `leader`=组长，`member`=成员 |
| `status` | string | `active`=活跃，`inactive`=不活跃 |
| `remark` | string | 组长设置的备注名 |

---

### 修改成员备注

```
PUT /api/v1/group/{groupId}/member/{userId}/remark
Authorization: Bearer {token}
Content-Type: application/json
```

组长修改成员的备注名。

**请求体**：

```json
{
  "remark": "前端大佬"
}
```

---

### 转让组长

```
POST /api/v1/group/{groupId}/transfer-leader
Authorization: Bearer {token}
Content-Type: application/json
```

组长将组长权限转让给其他成员。

**请求体**：

```json
{
  "newLeaderId": 10002
}
```

---

### 重新生成邀请码

```
POST /api/v1/group/{groupId}/invite-code
Authorization: Bearer {token}
```

重新生成邀请码，旧码立即失效。

**响应**：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "inviteCode": "XYZ789",
    "qrCodeUrl": "https://cdn.rocgit.com/qr/xyz789.png",
    "expiresAt": "2026-09-29 10:00:00"
  }
}
```

---

### 小组统计

```
GET /api/v1/group/{groupId}/stats
Authorization: Bearer {token}
```

**响应**：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "memberCount": 5,
    "projectCount": 3,
    "activeProjectCount": 2,
    "completedProjectCount": 1,
    "totalFileCount": 28,
    "totalTaskCount": 20
  }
}
```

---

### 成员贡献排行

```
GET /api/v1/group/{groupId}/member-stats
Authorization: Bearer {token}
```

查看小组成员的贡献排行榜。

**响应**：

```json
{
  "code": 200,
  "msg": "success",
  "data": [
    {
      "userId": 10001,
      "nickname": "张三",
      "avatarUrl": "https://cdn.rocgit.com/avatar/1.jpg",
      "contributions": 567,
      "fileCount": 15,
      "taskCount": 10,
      "rank": 1
    }
  ]
}
```

---

## 📁 模块四：项目接口（04）

### 创建项目

```
POST /api/v1/project/create
Authorization: Bearer {token}
Content-Type: application/json
```

在小组下创建新项目。

**请求体**：

```json
{
  "groupId": 1,
  "name": "课程设计项目",
  "description": "基于 Spring Boot 的协作管理",
  "templateId": 1,
  "stages": [
    { "name": "需求分析", "orderNum": 1 },
    { "name": "概要设计", "orderNum": 2 },
    { "name": "详细设计", "orderNum": 3 }
  ],
  "auditRule": {
    "requireAudit": true,
    "auditorRole": "leader"
  },
  "reminderRule": {
    "taskReminderDays": [3, 1]
  }
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `groupId` | long | ✅ | 所属小组 ID |
| `name` | string | ✅ | 项目名称 |
| `description` | string | 否 | 项目描述 |
| `templateId` | long | 否 | 使用模板 ID |
| `stages` | array | 否 | 阶段配置 |
| `auditRule.requireAudit` | boolean | 否 | 是否需要审核 |
| `reminderRule.taskReminderDays` | int[] | 否 | 任务到期提醒天数 |

**响应**：`Result<ProjectDetailVO>`

---

### 项目列表

```
GET /api/v1/project/list
Authorization: Bearer {token}
```

**Query 参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `groupId` | long | 否 | 筛选小组 |
| `status` | string | 否 | `active`/`completed`/`archived` |
| `pageNum` | int | 否 | 默认 1 |
| `pageSize` | int | 否 | 默认 20 |

---

### 项目详情

```
GET /api/v1/project/{projectId}
Authorization: Bearer {token}
```

获取项目详细信息（含阶段、进度、统计数据）。

**响应示例**：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "projectId": 1,
    "groupId": 1,
    "name": "课程设计项目",
    "description": "基于 Spring Boot 的协作管理",
    "currentStage": "实现阶段",
    "progress": 65,
    "status": "active",
    "stages": [
      {
        "stageId": 1,
        "name": "需求分析",
        "orderNum": 1,
        "status": "completed",
        "fileCount": 5,
        "taskCount": 4
      },
      {
        "stageId": 2,
        "name": "概要设计",
        "orderNum": 2,
        "status": "in_progress",
        "fileCount": 3,
        "taskCount": 3
      }
    ],
    "memberCount": 5,
    "fileCount": 28,
    "taskCount": 20,
    "completedTaskCount": 12,
    "createdAt": "2026-09-01 10:00:00"
  }
}
```

---

### 更新项目

```
PUT /api/v1/project/{projectId}
Authorization: Bearer {token}
Content-Type: application/json
```

---

### 删除项目

```
DELETE /api/v1/project/{projectId}
Authorization: Bearer {token}
```

软删除项目（可恢复）。

---

### 复制项目

```
POST /api/v1/project/{projectId}/copy
Authorization: Bearer {token}
Content-Type: application/json
```

以现有项目为模板创建新项目。

**请求体**：

```json
{
  "name": "课程设计项目 - 副本",
  "includeFiles": false,
  "includeTasks": false
}
```

---

### 项目结项

```
POST /api/v1/project/{projectId}/complete
Authorization: Bearer {token}
```

完成项目结项流程（组长操作）。

---

### 项目进度

```
GET /api/v1/project/{projectId}/progress
Authorization: Bearer {token}
```

获取项目整体进度统计。

**响应示例**：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "overall": 65,
    "stages": [
      { "name": "需求分析", "progress": 100, "status": "completed" },
      { "name": "概要设计", "progress": 100, "status": "completed" },
      { "name": "实现阶段", "progress": 50, "status": "in_progress" }
    ],
    "fileStats": { "total": 28, "approved": 25 },
    "taskStats": { "total": 20, "completed": 12 }
  }
}
```

---

### 项目动态

```
GET /api/v1/project/{projectId}/activities
Authorization: Bearer {token}
```

获取项目最近的操作记录（文件上传、任务完成等）。

**Query 参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `type` | string | 否 | 筛选类型：`file`/`task`/`stage`/`member` |

---

### 任务统计

```
GET /api/v1/project/{projectId}/task-stats
Authorization: Bearer {token}
```

---

### 新增阶段

```
POST /api/v1/project/{projectId}/stage
Authorization: Bearer {token}
Content-Type: application/json
```

**请求体**：

```json
{
  "name": "测试阶段",
  "orderNum": 5,
  "startDate": "2026-10-01",
  "endDate": "2026-10-15"
}
```

---

### 阶段列表

```
GET /api/v1/project/{projectId}/stage
Authorization: Bearer {token}
```

---

### 阶段详情

```
GET /api/v1/project/{projectId}/stage/{stageId}
Authorization: Bearer {token}
```

---

### 更新阶段

```
PUT /api/v1/project/{projectId}/stage/{stageId}
Authorization: Bearer {token}
Content-Type: application/json
```

---

### 删除阶段

```
DELETE /api/v1/project/{projectId}/stage/{stageId}
Authorization: Bearer {token}
```

---

### 项目模板列表

```
GET /api/v1/project/template/list
Authorization: Bearer {token}
```

---

### 模板详情

```
GET /api/v1/project/template/{templateId}
Authorization: Bearer {token}
```

---

### 创建项目模板

```
POST /api/v1/project/template
Authorization: Bearer {token}
Content-Type: application/json
```

---

## 📄 模块五：文件接口（05）

### 上传文件

```
POST /api/v1/file/upload
Authorization: Bearer {token}
Content-Type: multipart/form-data
```

上传文件到项目指定阶段。

**请求参数**：

| 参数 | 位置 | 类型 | 必填 | 说明 |
|------|------|------|------|------|
| `file` | body | binary | ✅ | 文件内容 |
| `projectId` | body | long | ✅ | 所属项目 ID |
| `stageId` | body | long | 否 | 目标阶段 ID |
| `remark` | body | string | 否 | 上传说明 |

**响应**：`Result<FileVO>`

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "fileId": 101,
    "name": "需求文档.docx",
    "url": "https://cdn.rocgit.com/files/101.docx",
    "size": 1024000,
    "type": "docx",
    "status": "pending",
    "version": 1,
    "projectId": 1,
    "stageId": 2,
    "uploader": {
      "userId": 10001,
      "nickname": "张三"
    },
    "uploadedAt": "2026-09-22 10:00:00"
  }
}
```

| 字段 | 说明 |
|------|------|
| `status` | `pending`=待审核，`approved`=已通过，`rejected`=已驳回 |
| `version` | 文件版本号（每次上传递增） |

---

### 文件列表

```
GET /api/v1/file/list
Authorization: Bearer {token}
```

分页查询项目下的文件，支持多条件筛选。

**Query 参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `projectId` | long | ✅ | 项目 ID |
| `stageId` | long | 否 | 按阶段筛选 |
| `type` | string | 否 | 按文件类型（如 `docx`） |
| `uploaderId` | long | 否 | 按上传者筛选 |
| `keyword` | string | 否 | 文件名关键词 |
| `status` | string | 否 | `pending`/`approved`/`rejected` |
| `startDate` | string | 否 | 开始日期 `yyyy-MM-dd` |
| `endDate` | string | 否 | 结束日期 `yyyy-MM-dd` |
| `pageNum` | int | 否 | 默认 1 |
| `pageSize` | int | 否 | 默认 20 |

---

### 文件详情

```
GET /api/v1/file/{fileId}
Authorization: Bearer {token}
```

---

### 删除文件

```
DELETE /api/v1/file/{fileId}
Authorization: Bearer {token}
```

---

### 文件预览

```
GET /api/v1/file/{fileId}/preview
Authorization: Bearer {token}
```

获取文件预览地址（带水印）。

**响应**：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "url": "https://view.rocgit.com/preview/101",
    "type": "pdf",
    "watermark": true
  }
}
```

---

### 文件下载

```
GET /api/v1/file/{fileId}/download
Authorization: Bearer {token}
```

获取文件下载链接（临时有效期 1 小时）。

**响应**：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "downloadUrl": "https://cdn.rocgit.com/download/101",
    "expiresAt": 1726987200000,
    "filename": "需求文档.docx"
  }
}
```

---

### 移动文件

```
PUT /api/v1/file/{fileId}/move
Authorization: Bearer {token}
Content-Type: application/json
```

将文件移到目标阶段。

**请求体**：

```json
{
  "targetStageId": 3
}
```

---

### 重命名文件

```
PUT /api/v1/file/{fileId}/rename
Authorization: Bearer {token}
Content-Type: application/json
```

**请求体**：

```json
{
  "name": "需求文档v2.docx"
}
```

---

### 收藏文件

```
POST /api/v1/file/{fileId}/favorite
Authorization: Bearer {token}
```

切换收藏状态（已收藏则取消，未收藏则添加）。

---

### 审核通过

```
POST /api/v1/file/{fileId}/approve
Authorization: Bearer {token}
```

组长或审核人员审核通过文件。

---

### 审核驳回

```
POST /api/v1/file/{fileId}/reject
Authorization: Bearer {token}
Content-Type: application/json
```

**请求体**：

```json
{
  "reason": "缺少第三章节的内容，请补充后重新上传"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `reason` | string | ✅ | 驳回原因，5-200 字 |

---

### 重新提交

```
POST /api/v1/file/{fileId}/resubmit
Authorization: Bearer {token}
Content-Type: multipart/form-data
```

被驳回后重新上传新版本。

**请求参数**：

| 参数 | 必填 | 说明 |
|------|------|------|
| `file` | ✅ | 新文件内容 |
| `remark` | 否 | 重新提交说明 |

---

### 审核日志

```
GET /api/v1/file/{fileId}/audit-logs
Authorization: Bearer {token}
```

查看文件的审核历史记录。

---

### 下载记录

```
GET /api/v1/file/{fileId}/download-records
Authorization: Bearer {token}
```

---

### 待审核列表

```
GET /api/v1/file/pending-audit
Authorization: Bearer {token}
```

获取当前用户（审核权限）的待审核文件列表。

---

### 批量审核

```
POST /api/v1/file/batch-audit
Authorization: Bearer {token}
Content-Type: application/json
```

批量通过或驳回多个文件。

**请求体**：

```json
{
  "fileIds": [101, 102, 103],
  "action": "approve",
  "reason": "",
  "remark": "统一审核通过"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `fileIds` | long[] | ✅ | 文件 ID 列表，最多 20 个 |
| `action` | string | ✅ | `approve`=通过，`reject`=驳回 |
| `reason` | string | reject 时必填 | 驳回原因 |
| `remark` | string | 否 | 审核备注 |

---

### 批量移动

```
PUT /api/v1/file/batch-move
Authorization: Bearer {token}
Content-Type: application/json
```

批量移动文件到目标阶段。

**请求体**：

```json
{
  "fileIds": [101, 102],
  "targetStageId": 3
}
```

最多 50 个文件/次。

---

### 批量删除

```
DELETE /api/v1/file/batch-delete
Authorization: Bearer {token}
Content-Type: application/json
```

**请求体**：

```json
{
  "fileIds": [101, 102],
  "reason": "已更新新版本"
}
```

最多 20 个文件/次。

---

### 我的收藏

```
GET /api/v1/file/favorites
Authorization: Bearer {token}
```

---

### 我的上传

```
GET /api/v1/file/my-uploads
Authorization: Bearer {token}
```

---

### 类型统计

```
GET /api/v1/file/{projectId}/type-stats
Authorization: Bearer {token}
```

---

### 导出文件清单

```
POST /api/v1/file/{projectId}/export
Authorization: Bearer {token}
Content-Type: application/json
```

导出项目所有文件清单为 Excel 或 CSV。

**请求体**：

```json
{
  "format": "xlsx",
  "stageId": 2,
  "includeVersions": true
}
```

| 字段 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `format` | string | `xlsx` | 导出格式：`xlsx` 或 `csv` |
| `stageId` | long | 全部阶段 | 限定阶段 |
| `includeVersions` | boolean | false | 是否包含版本历史 |

---

## 🔄 模块六：版本接口（06）

### 版本列表

```
GET /api/v1/version/list
Authorization: Bearer {token}
```

获取文件的所有历史版本。

**Query 参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `fileId` | long | ✅ | 文件 ID |

---

### 版本对比

```
GET /api/v1/version/compare
Authorization: Bearer {token}
```

对比两个版本的差异。

**Query 参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `fileId` | long | ✅ | 文件 ID |
| `fromVersion` | int | ✅ | 源版本号 |
| `toVersion` | int | ✅ | 目标版本号 |

---

### 版本回滚

```
POST /api/v1/version/{fileId}/rollback
Authorization: Bearer {token}
Content-Type: application/json
```

回滚文件到指定版本。

**请求体**：

```json
{
  "targetVersion": 3,
  "remark": "新版本有 bug，回退到 v3"
}
```

---

## ✅ 模块七：任务接口（07）

### 创建任务

```
POST /api/v1/task/create
Authorization: Bearer {token}
Content-Type: application/json
```

在项目中创建任务并分配给成员。

**请求体**：

```json
{
  "projectId": 1,
  "name": "完成登录模块",
  "description": "实现微信扫码登录功能",
  "assigneeId": 10002,
  "deadline": "2026-10-01T23:59:59",
  "priority": "high",
  "relatedFileIds": [101, 102]
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `projectId` | long | ✅ | 所属项目 ID |
| `name` | string | ✅ | 任务名称 |
| `description` | string | 否 | 任务详细描述 |
| `assigneeId` | long | ✅ | 负责人用户 ID |
| `deadline` | datetime | ✅ | 截止时间 |
| `priority` | string | 否 | `high`/`normal`/`low` |
| `relatedFileIds` | long[] | 否 | 关联文件 ID 列表 |

**响应**：`Result<TaskVO>`

---

### 任务列表

```
GET /api/v1/task/list
Authorization: Bearer {token}
```

**Query 参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `projectId` | long | ✅ | 项目 ID |
| `assigneeId` | long | 否 | 按负责人筛选 |
| `status` | string | 否 | `pending`/`in_progress`/`completed` |

---

### 我的任务

```
GET /api/v1/task/my-tasks
Authorization: Bearer {token}
```

获取当前用户负责的所有任务。

**Query 参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `status` | string | 否 | 筛选状态 |
| `overdue` | boolean | 否 | 只看已过期任务 |
| `pageNum` | int | 否 | 默认 1 |
| `pageSize` | int | 否 | 默认 20 |

---

### 即将到期

```
GET /api/v1/task/due-soon
Authorization: Bearer {token}
```

获取截止日期临近的任务（默认未来 7 天内）。

**Query 参数**：

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `days` | int | 7 | 天数 |

---

### 任务详情

```
GET /api/v1/task/{taskId}
Authorization: Bearer {token}
```

**响应示例**：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "taskId": 1,
    "projectId": 1,
    "projectName": "课程设计项目",
    "name": "完成登录模块",
    "description": "实现微信扫码登录功能",
    "assigneeId": 10001,
    "assigneeName": "张三",
    "deadline": "2026-10-01T23:59:59",
    "priority": "high",
    "status": "in_progress",
    "progress": 50,
    "relatedFiles": [
      { "fileId": 101, "name": "登录模块设计.md" }
    ],
    "completedAt": null,
    "completionRemark": null,
    "createdAt": "2026-09-15 10:00:00"
  }
}
```

---

### 更新任务

```
PUT /api/v1/task/{taskId}
Authorization: Bearer {token}
Content-Type: application/json
```

修改任务信息（仅负责人或组长可操作）。

**请求体**（所有字段可选）：

```json
{
  "name": "新任务名",
  "description": "新的描述",
  "assigneeId": 10003,
  "deadline": "2026-10-10T23:59:59",
  "priority": "normal"
}
```

---

### 删除任务

```
DELETE /api/v1/task/{taskId}
Authorization: Bearer {token}
```

---

### 完成任务

```
POST /api/v1/task/{taskId}/complete
Authorization: Bearer {token}
Content-Type: application/json
```

将任务标记为已完成，可上传完成凭证文件。

**请求体**（可选）：

```json
{
  "remark": "已实现微信登录，单元测试通过",
  "proofFiles": [105, 106]
}
```

---

### 放弃任务

```
POST /api/v1/task/{taskId}/abandon
Authorization: Bearer {token}
Content-Type: application/json
```

**请求体**：

```json
{
  "reason": "工作量超出预期，需要重新评估"
}
```

---

### 转交任务

```
POST /api/v1/task/{taskId}/transfer
Authorization: Bearer {token}
Content-Type: application/json
```

把任务转交给其他成员。

**请求体**：

```json
{
  "newAssigneeId": 10003,
  "remark": "因工作调整，转交给李四负责"
}
```

---

### 更新进度

```
POST /api/v1/task/{taskId}/progress
Authorization: Bearer {token}
Content-Type: application/json
```

更新任务完成进度（0-100）。

**请求体**：

```json
{
  "progress": 75,
  "remark": "已完成登录界面，前端对接中"
}
```

---

## 💬 模块八：研讨接口（08）

### 创建研讨记录

```
POST /api/v1/discussion/create
Authorization: Bearer {token}
Content-Type: application/json
```

记录小组会议讨论内容。

**请求体**：

```json
{
  "projectId": 1,
  "topic": "需求评审会议",
  "content": "讨论了需求的边界范围，确认为 MVP 功能集...",
  "meetingTime": "2026-09-25T14:00:00",
  "attendees": [10001, 10002, 10003]
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `projectId` | long | ✅ | 所属项目 ID |
| `topic` | string | ✅ | 研讨主题 |
| `content` | string | ✅ | 详细内容 |
| `meetingTime` | datetime | ✅ | 会议时间 |
| `attendees` | long[] | 否 | 参会人员 ID 列表 |

---

### 研讨列表

```
GET /api/v1/discussion/list
Authorization: Bearer {token}
```

**Query 参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `projectId` | long | ✅ | 项目 ID |
| `pageNum` | int | 否 | 默认 1 |
| `pageSize` | int | 否 | 默认 20 |

---

### 研讨详情

```
GET /api/v1/discussion/{discussionId}
Authorization: Bearer {token}
```

---

### 更新研讨

```
PUT /api/v1/discussion/{discussionId}
Authorization: Bearer {token}
Content-Type: application/json
```

---

### 删除研讨

```
DELETE /api/v1/discussion/{discussionId}
Authorization: Bearer {token}
```

---

## ⚖️ 模块九：决策接口（09）

### 创建决策

```
POST /api/v1/decision/create
Authorization: Bearer {token}
Content-Type: application/json
```

记录小组的关键决策。

**请求体**：

```json
{
  "projectId": 1,
  "title": "采用 Spring Boot 框架",
  "content": "经过技术选型讨论，决定后端使用 Spring Boot + MyBatis...",
  "discussionId": 101
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `projectId` | long | ✅ | 所属项目 ID |
| `title` | string | ✅ | 决策标题 |
| `content` | string | ✅ | 决策详细内容 |
| `discussionId` | long | 否 | 来源研讨记录 ID |

---

### 决策列表

```
GET /api/v1/decision/list
Authorization: Bearer {token}
```

---

### 决策详情

```
GET /api/v1/decision/{decisionId}
Authorization: Bearer {token}
```

---

### 更新决策

```
PUT /api/v1/decision/{decisionId}
Authorization: Bearer {token}
Content-Type: application/json
```

---

### 删除决策

```
DELETE /api/v1/decision/{decisionId}
Authorization: Bearer {token}
```

---

## 🏆 模块十：成果接口（10）

### 创建成果

```
POST /api/v1/achievement/create
Authorization: Bearer {token}
Content-Type: application/json
```

登记项目阶段性成果。

**请求体**：

```json
{
  "projectId": 1,
  "stageId": 1,
  "title": "完成需求分析",
  "description": "产出完整的需求规格说明书，包含用例图、ER图等",
  "relatedFileIds": [101, 102]
}
```

---

### 成果列表

```
GET /api/v1/achievement/list
Authorization: Bearer {token}
```

**Query 参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `projectId` | long | ✅ | 项目 ID |

---

### 成果详情

```
GET /api/v1/achievement/{achievementId}
Authorization: Bearer {token}
```

---

### 更新成果

```
PUT /api/v1/achievement/{achievementId}
Authorization: Bearer {token}
Content-Type: application/json
```

---

### 删除成果

```
DELETE /api/v1/achievement/{achievementId}
Authorization: Bearer {token}
```

---

## 📊 模块十一：日志接口（11）

### 操作日志列表

```
GET /api/v1/log/list
Authorization: Bearer {token}
```

**Query 参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `projectId` | long | ✅ | 项目 ID |
| `type` | string | 否 | 日志类型：`file`/`task`/`stage`/`member` |
| `pageNum` | int | 否 | 默认 1 |
| `pageSize` | int | 否 | 默认 20 |

---

### 日志详情

```
GET /api/v1/log/{logId}
Authorization: Bearer {token}
```

---

### 日志统计

```
GET /api/v1/log/project/{projectId}/log-stats
Authorization: Bearer {token}
```

---

## 🔔 模块十二：消息接口（12）

### 消息列表

```
GET /api/v1/message/list
Authorization: Bearer {token}
```

**Query 参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `type` | string | 否 | 消息类型：`system`/`task`/`mention`/`audit` |
| `pageNum` | int | 否 | 默认 1 |
| `pageSize` | int | 否 | 默认 20 |

---

### 未读消息数

```
GET /api/v1/message/unread-count
Authorization: Bearer {token}
```

获取未读消息数量（分类统计）。

**响应**：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "total": 5,
    "system": 1,
    "task": 2,
    "mention": 2,
    "audit": 0
  }
}
```

---

### 消息详情

```
GET /api/v1/message/{messageId}
Authorization: Bearer {token}
```

---

### 标记已读

```
POST /api/v1/message/{messageId}/read
Authorization: Bearer {token}
```

---

### 全部已读

```
POST /api/v1/message/read-all
Authorization: Bearer {token}
Content-Type: application/json
```

**请求体**：

```json
{
  "type": "all"
}
```

| `type` 值 | 说明 |
|-----------|------|
| `all` | 所有消息 |
| `system` | 系统消息 |
| `task` | 任务消息 |
| `mention` | @提及消息 |
| `audit` | 审核消息 |

---

### 订阅设置

```
POST /api/v1/message/subscribe
Authorization: Bearer {token}
Content-Type: application/json
```

订阅/取消订阅某种类型的消息通知。

**请求体**：

```json
{
  "type": "task",
  "enabled": true,
  "channel": "wechat"
}
```

---

### 我的订阅

```
GET /api/v1/message/subscription
Authorization: Bearer {token}
```

---

### 更新订阅

```
PUT /api/v1/message/subscription
Authorization: Bearer {token}
Content-Type: application/json
```

**请求体**：

```json
{
  "auditNotify": true,
  "taskRemind": true,
  "memberNotify": false,
  "stageNotify": true,
  "mentionNotify": true
}
```

---

## 📢 模块十三：公告接口（13）

### 公告列表

```
GET /api/v1/announcement/list
Authorization: Bearer {token}
```

---

### 标记已读

```
POST /api/v1/announcement/{announcementId}/read
Authorization: Bearer {token}
```

---

## 🔗 模块十四：分享接口（14）

### 创建分享链接

```
POST /api/v1/share/create
Authorization: Bearer {token}
Content-Type: application/json
```

为项目结项创建对外分享链接。

**请求体**：

```json
{
  "projectId": 1,
  "permissionLevel": "flow_preview",
  "expiresAt": "2027-09-22T23:59:59",
  "needPassword": false
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `projectId` | long | ✅ | 项目 ID |
| `permissionLevel` | string | ✅ | 权限级别（见下方） |
| `expiresAt` | datetime | 否 | 过期时间，不填则永久 |
| `needPassword` | boolean | 否 | 是否需要访问密码 |

**`permissionLevel` 可选值**：

| 值 | 说明 |
|----|------|
| `flow_only` | 仅浏览项目流程（不可见文件） |
| `flow_preview` | 浏览流程 + 预览文件（水印） |
| `flow_download` | 浏览流程 + 预览 + 下载文件 |

**响应**：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "shareId": 1,
    "shareToken": "tk1726983600",
    "shareUrl": "https://share.rocgit.com/tk1726983600",
    "qrCodeUrl": "https://cdn.rocgit.com/qr/tk1726983600.png",
    "permissionLevel": "flow_preview",
    "viewCount": 0,
    "expiresAt": "2027-09-22T23:59:59",
    "createdAt": "2026-09-22 10:00:00"
  }
}
```

---

### 分享列表

```
GET /api/v1/share/list
Authorization: Bearer {token}
```

查看我创建的所有分享链接。

**Query 参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `projectId` | long | ✅ | 项目 ID |

---

### 分享详情

```
GET /api/v1/share/{shareId}
Authorization: Bearer {token}
```

---

### 更新分享权限

```
PUT /api/v1/share/{shareId}
Authorization: Bearer {token}
Content-Type: application/json
```

**请求体**：

```json
{
  "permissionLevel": "flow_download",
  "expiresAt": "2026-12-31T23:59:59"
}
```

---

### 取消分享

```
DELETE /api/v1/share/{shareId}
Authorization: Bearer {token}
```

---

### 重新生成分享链接

```
POST /api/v1/share/{shareId}/regenerate
Authorization: Bearer {token}
```

旧链接立即失效，生成新链接。

---

### 分享统计

```
GET /api/v1/share/{shareId}/stats
Authorization: Bearer {token}
```

---

### 访问日志

```
GET /api/v1/share/{shareId}/access-logs
Authorization: Bearer {token}
```

---

### 生成分享海报

```
POST /api/v1/share/{shareId}/poster
Authorization: Bearer {token}
```

---

### 校验分享令牌

```
POST /api/v1/share/validate
Authorization: Bearer {token}
Content-Type: application/json
```

访客在访问前校验分享链接是否有效。

**请求体**：

```json
{
  "shareToken": "tk1726983600",
  "password": ""
}
```

---

### 访客接口（无需认证）

以下接口无需登录即可访问（通过分享令牌）：

```
GET /api/v1/share/{shareToken}
GET /api/v1/share/{shareToken}/overview
GET /api/v1/share/{shareToken}/timeline
GET /api/v1/share/{shareToken}/files
GET /api/v1/share/{shareToken}/file/{fileId}/preview
GET /api/v1/share/{shareToken}/file/{fileId}/download
GET /api/v1/share/{shareToken}/discussions
GET /api/v1/share/{shareToken}/decisions
```

---

## 🛡️ 安全设计

### JWT Token 流程

```
用户登录 → 微信 code → 后端验证 → 颁发 JWT Token
    ↓
请求时携带 Authorization: Bearer {token}
    ↓
后端拦截器验证 Token 有效性
    ↓
通过 → 解析用户ID → 处理业务
失败 → 返回 401
```

### 安全措施

1. **HTTPS**：生产环境强制 HTTPS
2. **JWT 时效**：Token 有效期 7 天，自动刷新
3. **防注入**：所有输入参数严格校验
4. **权限控制**：组长/成员权限分离
5. **文件安全**：预览带水印，下载链接临时有效
6. **敏感操作日志**：所有关键操作记录审计日志

---

## 📝 附录

### A. 数据类型对照

| OpenAPI 类型 | Java 类型 | 说明 |
|-------------|----------|------|
| `string` | `String` | 字符串 |
| `integer` | `Integer` | 32位整数 |
| `int64` | `Long` | 64位整数 |
| `boolean` | `Boolean` | 布尔值 |
| `date` | `LocalDate` | 日期 `yyyy-MM-dd` |
| `date-time` | `LocalDateTime` | 日期时间 `yyyy-MM-ddTHH:mm:ss` |
| `array` | `List<T>` | 数组 |
| `object` | 自定义类 | 对象 |

### B. 常用状态值

**文件状态**：`pending` / `approved` / `rejected`

**任务状态**：`pending` / `in_progress` / `completed` / `abandoned`

**项目状态**：`active` / `completed` / `archived`

**成员角色**：`leader` / `member`

**优先级**：`high` / `normal` / `low`

**反馈类型**：`bug` / `suggestion` / `complaint`

**分享权限**：`flow_only` / `flow_preview` / `flow_download`

### C. 接口统计

| 模块 | GET | POST | PUT | DELETE | 小计 |
|------|-----|------|-----|--------|------|
| 01 通用 | 2 | 3 | 0 | 0 | 5 |
| 02 用户 | 3 | 3 | 1 | 0 | 7 |
| 03 小组 | 4 | 3 | 1 | 1 | 9 |
| 04 项目 | 6 | 4 | 2 | 1 | 13 |
| 05 文件 | 7 | 4 | 4 | 1 | 16 |
| 06 版本 | 2 | 1 | 0 | 0 | 3 |
| 07 任务 | 4 | 4 | 1 | 1 | 10 |
| 08 研讨 | 2 | 1 | 1 | 1 | 5 |
| 09 决策 | 2 | 1 | 1 | 1 | 5 |
| 10 成果 | 2 | 1 | 1 | 1 | 5 |
| 11 日志 | 3 | 0 | 0 | 0 | 3 |
| 12 消息 | 3 | 3 | 1 | 0 | 7 |
| 13 公告 | 1 | 0 | 0 | 0 | 1 |
| 14 分享 | 8 | 3 | 1 | 1 | 13 |
| **合计** | **49** | **31** | **14** | **8** | **102** |

> 注：以上为部分统计，实际以 Knife4j 在线文档为准。

---

## 🔗 相关资源

- **在线文档（Knife4j）**：`http://localhost:8080/doc.html`
- **Swagger UI**：`http://localhost:8080/swagger-ui/index.html`
- **OpenAPI JSON**：`http://localhost:8080/v3/api-docs`
- **OpenAPI YAML**：`openapi.yaml`（项目根目录）
- **项目源码**：`knife4j-spring/`（Spring Boot + Knife4j 集成示例）

---

*本文档由 Knife4j 自动生成，如有不一致请以在线文档为准。*
