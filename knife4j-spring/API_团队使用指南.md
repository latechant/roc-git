# 🚀 ROC-git API 接口使用指南（给队友）

> 📖 **完整接口文档**：[API接口使用文档.md](./API接口使用文档.md)（102 个接口 + 完整字段说明）
> 🌐 **在线文档**：启动后访问 `http://localhost:8080/doc.html`

---

## ⚡ 3 步快速上手

### 第 1 步：启动后端服务

```bash
cd d:\ProjectSpace\2026年上学年\软件设计实践\knife4j-spring
mvn spring-boot:run
```

看到以下输出表示启动成功：

```
========================================
ROC-git Knife4j API 文档已启动
访问：http://localhost:8080/doc.html
========================================

Started Application in 6.227 seconds
```

### 第 2 步：打开在线文档

浏览器访问：**http://localhost:8080/doc.html** （中文界面，推荐）

或：**http://localhost:8080/swagger-ui/index.html** （英文 Swagger UI）

### 第 3 步：登录获取 Token

在 Knife4j 中点击右上角「Authorize」按钮，输入测试账号的 JWT Token。

获取测试 Token（开发期间）：

```bash
curl -X POST http://localhost:8080/api/v1/user/login \
  -H "Content-Type: application/json" \
  -d '{
    "code": "test_mock_code",
    "nickname": "测试用户",
    "avatarUrl": "https://cdn.rocgit.com/avatar/default.jpg"
  }'
```

把响应中的 `data.token` 字段复制到 Knife4j 的授权框中。

---

## 🛠️ 工具链

### 必备工具

| 工具 | 用途 | 下载 |
|------|------|------|
| **IntelliJ IDEA / VS Code** | 代码 IDE | 自由选择 |
| **Apifox / Postman** | API 调试 | [Apifox 下载](https://apifox.com/) |
| **微信开发者工具** | 调试小程序 | [官方下载](https://developers.weixin.qq.com/miniprogram/dev/devtools/download.html) |

### 🧪 用 Apifox/Postman 调试

#### 1. 导入 OpenAPI 规范

- Apifox：项目设置 → 导入 → OpenAPI → 选择 [`openapi.yaml`](./openapi.yaml)
- Postman：Import → Link → `http://localhost:8080/v3/api-docs`

#### 2. 配置环境变量

| 变量名 | 值 |
|--------|-----|
| `BASE_URL` | `http://localhost:8080` |
| `TOKEN` | 登录接口返回的 JWT Token |

#### 3. Authorization 配置

在 Collection 级别配置：

```
Type: Bearer Token
Token: {{TOKEN}}
```

---

## 📱 微信小程序调用示例

### utils/request.js（封装网络请求）

```javascript
const BASE_URL = 'http://localhost:8080';

// 从本地缓存获取 token
function getToken() {
  return wx.getStorageSync('token') || '';
}

// 统一网络请求
function request({ url, method = 'GET', data = {}, needAuth = true }) {
  return new Promise((resolve, reject) => {
    const header = { 'Content-Type': 'application/json' };
    if (needAuth) {
      header['Authorization'] = `Bearer ${getToken()}`;
    }

    wx.request({
      url: BASE_URL + url,
      method,
      data,
      header,
      success: (res) => {
        if (res.data.code === 200) {
          resolve(res.data.data);
        } else if (res.data.code === 401) {
          // Token 过期，重新登录
          wx.removeStorageSync('token');
          wx.navigateTo({ url: '/pages/login/login' });
          reject(res.data);
        } else {
          wx.showToast({ title: res.data.msg, icon: 'none' });
          reject(res.data);
        }
      },
      fail: (err) => reject(err)
    });
  });
}

module.exports = { request, getToken };
```

### 使用示例：获取个人信息

```javascript
const { request } = require('./utils/request.js');

async function loadProfile() {
  try {
    const profile = await request({
      url: '/api/v1/user/profile',
      method: 'GET'
    });
    console.log('用户信息：', profile);
    this.setData({ userInfo: profile });
  } catch (err) {
    console.error('加载失败', err);
  }
}
```

### 使用示例：上传文件

```javascript
async function uploadFile(filePath, projectId, stageId) {
  return new Promise((resolve, reject) => {
    const token = wx.getStorageSync('token');
    wx.uploadFile({
      url: 'http://localhost:8080/api/v1/file/upload',
      filePath,
      name: 'file',
      formData: { projectId, stageId },
      header: { 'Authorization': `Bearer ${token}` },
      success: (res) => {
        const data = JSON.parse(res.data);
        if (data.code === 200) resolve(data.data);
        else reject(data);
      },
      fail: reject
    });
  });
}
```

---

## 🧪 测试用例

### 测试场景 1：完整业务流

```javascript
// 1. 组长登录
const loginRes = await request({
  url: '/api/v1/user/login',
  method: 'POST',
  needAuth: false,
  data: { code: 'mock_code', nickname: '张三', avatarUrl: '...' }
});
wx.setStorageSync('token', loginRes.token);

// 2. 创建小组
const groupRes = await request({
  url: '/api/v1/group/create',
  method: 'POST',
  data: { name: '课程设计组', description: '...' }
});

// 3. 创建项目
const projectRes = await request({
  url: '/api/v1/project/create',
  method: 'POST',
  data: {
    groupId: groupRes.groupId,
    name: '期末大作业',
    stages: [
      { name: '需求', orderNum: 1 },
      { name: '开发', orderNum: 2 }
    ]
  }
});

// 4. 上传文件
const fileRes = await uploadFile('/path/to/file.docx', projectRes.projectId);

// 5. 创建任务
const taskRes = await request({
  url: '/api/v1/task/create',
  method: 'POST',
  data: {
    projectId: projectRes.projectId,
    name: '完成登录',
    assigneeId: 10002,
    deadline: '2026-12-31 23:59:59',
    priority: 'high'
  }
});
```

---

## ❓ 常见问题

### Q1: Knife4j 显示"未授权"怎么办？

**A**: 在 Knife4j 右上角点击「Authorize」按钮，粘贴有效的 JWT Token。

### Q2: 小程序调用出现 "不在以下 request 合法域名列表中" 错误？

**A**: 微信开发者工具勾选「不校验合法域名」（开发期间）。生产环境需在小程序后台配置 `https://api.rocgit.com` 为合法域名。

### Q3: 后端返回 401 错误？

**A**: Token 过期或未携带。重新登录获取 Token，并在请求头加上 `Authorization: Bearer {token}`。

### Q4: 文件上传失败？

**A**: 检查：
1. 后端 `multipart/form-data` 接口是否正常
2. Spring Boot 默认文件大小限制（`spring.servlet.multipart.max-file-size: 100MB`）
3. Nginx 反向代理的 `client_max_body_size` 设置

### Q5: 如何模拟微信登录？

**A**: 开发阶段可以直接调用 `/api/v1/user/login`，传任意 `code`（后端 mock），后续可在 [`config/WebMvcConfig.java`](./knife4j-spring/src/main/java/com/rocgit/knife4j/config/WebMvcConfig.java) 替换为真实的微信 API 调用。

---

## 📚 相关文档

- 📖 [完整接口文档（API接口使用文档.md）](./API接口使用文档.md)
- 🏗️ [需求规格说明书（需求规格说明书.md）](./需求规格说明书.md)
- 🔧 [Knife4j 部署指南（KNIFE4J_DOC.md）](./KNIFE4J_DOC.md)
- 🌐 [Swagger 静态部署指南（SWAGGER_GUIDE.md）](./SWAGGER_GUIDE.md)
- 📁 [OpenAPI 规范（openapi.yaml）](./openapi.yaml)

---

## 🤝 约定

1. **统一字段命名**：所有字段采用 **camelCase**（驼峰命名）
2. **时间格式**：使用 ISO 8601 格式（`2026-09-22T10:00:00`）
3. **文件大小**：统一使用字节（Byte）单位
4. **命名空间**：`/api/v1/{module}/{action}`
5. **提交前必查**：所有 POST/PUT 接口必填字段在文档中用 ✅ 标注

---

**问题反馈**：提交 [Issue](https://github.com/rocgit/api-docs/issues) 或联系 @API 维护者

*Last Updated: 2026-09-22*
