# ROC-git · 课程小组文档协作管理平台

**软件系统分析与设计综合实践** 课程项目。大学生以小组为单位协作完成课程作业，平台统一管理资料文件、版本、任务分工、研讨决策与审核归档，解决文件散乱、版本混乱、审核缺失、过程不可追溯、协作效率低等痛点。

本仓库（`repository/roc-git`）为项目主代码仓，同时存放数据库、接口规范与报告材料。

## 目录结构

```
roc-git/
├── src/                     # uni-app + Vue 3 前端源码（H5 / 微信小程序双端）
│   ├── utils/api.js         #   真实接口请求层（token 管理、/api 反代）
│   └── utils/bind-api.js    #   后端数据绑定层（页面节点渲染）
├── database/                # MySQL 数据库层（建库建表 + 种子数据 + 说明）
│   ├── schema.sql           #   建库建表脚本（MySQL 8.0，15 张表，可重复执行）
│   ├── seed.sql             #   初始化数据（默认阶段模板）
│   └── README.md            #   表清单、表关系、状态机、接续开发指引
├── knife4j-spring/          # Spring Boot + Knife4j 接口文档后端
│   └── README.md            #   双模式打包（Fat WAR / 标准 WAR）部署指南
├── openapi.yaml             # OpenAPI 3.0 规范（146 个 REST 接口）
├── SWAGGER_GUIDE.md         # Swagger/Knife4j 使用与部署指南
├── scripts/                 # 迁移与验证脚本（见下文「脚本」）
├── docs/                    # 报告与需求材料
│   ├── original-readme.md   #   原始 HTML 版说明备份
│   ├── docx_content.txt     #   报告 docx 正文提取
│   ├── 功能点.docx          #   功能点清单
│   ├── 项目可行技术栈选型….docx  # 技术栈选型（SpringBoot+MyBatis-Plus+MySQL 等）
│   └── 《软件系统分析与设计…》.docx  # 课程报告参考模板
├── 需求规格说明书.md         # 需求文档 v1.9（概述/角色/用例/类图/时序图/功能/流程/数据字典等）
├── package.json             # 前端依赖与脚本（uni-app CLI）
├── vite.config.js           # Vite 配置
├── index.html               # Vite H5 入口（保留）
└── readme.md                # 本文件
```

> 主项目根目录另有两处独立内容（不在此 git 仓内）：
> - `miniprogram/`：**原生微信小程序**前端，与 uni-app 端并存，目前均使用 mock 数据；
> - `docs/`：`数据字典.md`、`数据建模设计.md`，与报告「2.4 数据库设计」及 `database/schema.sql` 一一对应。

## 三端关系

| 端 | 位置 | 技术 | 状态 |
|---|---|---|---|
| 前端 A | `src/` | uni-app + Vue 3 + Vite | 已迁移，可编译 H5 / 微信小程序；H5 已接入真实后端接口 |
| 前端 B | 主目录 `miniprogram/` | 原生微信小程序 | 原型，mock 数据 |
| 后端 | `knife4j-spring/` | Spring Boot + Knife4j | 接口文档后端，已部署，鉴权链路可用 |
| 数据库 | `database/` | MySQL 8.0 | schema + seed 就绪 |

uni-app 端（前端 A）通过 `src/utils/api.js`（真实请求层）+ `src/utils/bind-api.js`（数据绑定层）接入后端真实接口：`api.js` 统一管理 token、以相对 `/api` 走 nginx 反向代理，页面控制器创建后调用 `bind-api` 把后端数据渲染进对应节点，无需改动原型布局。原生小程序端仍为 mock。接口路径统一按 `/api/...` 预留。

---

## 前端 A：uni-app（Vue 3）

原有 6 个 HTML 页面已迁移为 **uni-app + Vue 3 + Vite**，支持 H5 和微信小程序。原文字、示例数据、颜色、字号、卡片尺寸、导航及弹层内容保留。原 HTML 已备份到桌面的 `快速UI-原始HTML备份-20260916-180046` 文件夹，并从项目中删除。新项目源码在 `src`；根目录 `index.html` 是 Vite 的 H5 入口，需要保留。

这是前端原型迁移，原项目没有后端。原来仅弹出提示的上传、审核、邀请、版本恢复、归档等操作仍保留原型行为。本次已为前端新增真实接口接入层，H5 部署后页面数据来自真实后端接口（详见下方「线上部署与真实接口接入」），但后端当前为接口文档演示服务（无数据库），返回演示数据；登录/鉴权链路为真实 JWT。原 README 提到 `teacher.html`，但实际没有该文件；“我指导的小组”和“过程性评价”入口保留，目标页面仍缺失，没有编造新内容。首页原本引用未定义的 `showFileSearch()`，迁移后保留其进入项目页的导航。

### VS Code 预览

**可以使用 VS Code，不强制使用 HBuilderX。** 安装 Node.js 20 或 22 LTS，在 VS Code 打开项目根目录，终端执行：

```powershell
npm.cmd install
npm.cmd run dev:h5
```

打开终端地址，默认 **http://127.0.0.1:5173/**。保存 Vue 文件后自动刷新。浏览器开发者工具可切换手机视口，原设计上限是 **393 × 852 px**，在该尺寸下对照最方便。新项目不能通过 Live Server 或双击 Vue 文件运行。

Windows PowerShell 如果提示 `npm.ps1` 被执行策略阻止，使用 `npm.cmd` 即可，不需要修改系统执行策略。macOS、Linux 或其他终端使用 `npm`。

VS Code 建议安装 **Vue - Official** 扩展。浏览器预览的是 H5；小程序模拟器和微信真机预览需要微信开发者工具。

### 微信小程序开发预览

1. 安装并登录[微信开发者工具](https://developers.weixin.qq.com/miniprogram/dev/devtools/download.html)。正式发布需要在[微信公众平台](https://mp.weixin.qq.com/)注册小程序，取得 AppID，并将当前微信用户加入开发者名单。
2. 在 `src/manifest.json` 填写 `mp-weixin.appid`，即微信小程序 AppID（通常以 `wx` 开头），不是上层 DCloud `appid`。仅试运行可以在开发者工具使用测试模式；正式发布必须使用真实 AppID。
3. 在项目根目录执行：

```powershell
npm.cmd run dev:mp-weixin
```

4. 微信开发者工具选择“导入项目”，目录选择 **`dist/dev/mp-weixin`**，填写一致的 AppID。不要导入根目录或 `src`，工具需要的是编译后的原生小程序工程。
5. 保持终端运行，修改 Vue 页面后会自动重新编译。在开发者工具查看效果，点击“预览”，用微信扫码检查真机。

无需再在微信开发者工具执行“构建 npm”，依赖已经由 uni-app 编译器处理。

### 上传与正式发布

填写真实 AppID 后执行：

```powershell
npm.cmd run build:mp-weixin
```

微信开发者工具导入 **`dist/build/mp-weixin`**，编译并检查页面，点击“上传”，填写版本号和备注。随后登录微信公众平台，在“版本管理”中将开发版本提交审核，审核通过后再发布。小程序代码通过此流程发布，无需把 Vue 源码上传到网页服务器。

目前 H5 端已接入真实后端接口（见「线上部署与真实接口接入」）；小程序端如需发布上线，需在微信公众平台配置 HTTPS 合法域名（request、uploadFile、downloadFile 等），并实现真实登录、上传、权限和数据持久化。小程序端接口已通过 `src/utils/api.js` 预留，`BASE_URL` 指向完整域名即可复用。

微信状态栏、右上角胶囊和原生选择器弹出界面由微信控制，无法与 HTML 浏览器逐像素相同；本项目采用自定义导航和本地样式保留原布局。发布前请在自己的手机检查顶部按钮与胶囊、底部导航是否重叠。

### HBuilderX 用法

也可以安装[HBuilderX](https://www.dcloud.io/hbuilderx.html)，导入项目根目录，先安装 npm 依赖，再选择“运行 → 运行到浏览器”或“运行 → 运行到小程序模拟器 → 微信开发者工具”。按提示配置微信开发者工具路径，发行菜单可生成微信发布包。

这是 CLI 项目，业务源码在 `src`。命令行输出固定为 `dist/dev/mp-weixin`、`dist/build/mp-weixin`；HBuilderX 菜单运行或发行时以控制台提示的实际目录为准。不要单独把 `src` 当作 HBuilderX 工程导入。

### 页面与代码

| 原页面 | 新页面 |
| --- | --- |
| `home.html` | `src/pages/home/home.vue` |
| `project.html` | `src/pages/project/project.vue` |
| `create.html` | `src/pages/create/create.vue` |
| `meeting.html` | `src/pages/meeting/meeting.vue` |
| `messages.html` | `src/pages/messages/messages.vue` |
| `profile.html` | `src/pages/profile/profile.vue` |

- `src/pages.json`：页面注册、自定义导航配置。
- `src/manifest.json`：微信 AppID、版本、平台配置。
- `src/styles/`：本地样式，保留 Tailwind 原数值，rem 转为 px，复杂类名转为小程序兼容类名。
- `src/static/icons/`：原 Lucide 图标的本地 PNG，不依赖在线 Iconify。
- `src/controllers/`：各页面原型操作。
- `src/data/`：页面初始状态、样式映射。
- `src/utils/page-controller.js`：Vue 响应式状态适配器，以及 uni-app 导航、剪贴板、定时器适配，没有浏览器 DOM 或 eval。
- `src/utils/api.js`：真实接口请求层，基于 `uni.request`，自动管理 JWT token、以相对 `/api` 走 nginx 反向代理（H5 部署时）。涵盖认证、小组、项目、任务、消息、研讨等全部后端接口封装。
- `src/utils/bind-api.js`：数据绑定层，将后端接口返回的数据渲染进各页面 DOM 节点，保留原型布局与交互；页面控制器创建后调用对应 `load*` 函数。
- `src/components/NativeFragment.vue`：新增成员、阶段、任务和研讨落地项的原生表单行。
- `src/components/NativeDate.vue`：仅 H5 使用的浏览器日期输入，小程序使用 `picker`。
- `docs/original-readme.md`、`docx_content.txt`：保留原说明和需求材料。

Vue 页面使用 `view`、`text`、`button`、`scroll-view`、`input`、`textarea`、`picker`、`image`，没有用 web-view 包装 HTML。原型动作通过响应式状态适配器保留，以减少重写造成的展示和行为变化。

后续维护直接编辑 `src`。`npm.cmd run migrate` 是一次性迁移工具，会从桌面备份的原 HTML **覆盖生成的页面、控制器和初始状态**，正常开发不要重复运行。内容和布局对照检查也读取该备份；若备份移动，可设置环境变量 `ORIGINAL_HTML_DIR` 指定新路径。正常运行及构建不依赖备份。

---

## 数据库层（database/）

集中管理全部数据库文件与设计文档，是**数据库层的唯一权威来源**。任何后端开发、联调、报告编写都以本目录为准。详见 `database/README.md`。

- 库名 `roc_git`，MySQL 8.0，InnoDB，utf8mb4。
- 共 **15 张表**：user、course、project、project_member、stage、stage_template、task、file、file_version、file_review、meeting、meeting_action、message、operation_log、archive。
- 关键状态机（file.status、project.status、task.status、user.role 等）及执行方式见 `database/README.md` 第四、五节。

---

## 后端：knife4j-spring（Spring Boot + Knife4j）

提供 ROC-git 接口文档的 Spring Boot 后端，支持两种打包模式（`java -jar` 直接运行 / 打成标准 WAR 部署外部 Tomcat）。详见 `knife4j-spring/README.md`。

```bash
cd knife4j-spring
mvn clean package -DskipTests        # 默认 Fat WAR
java -jar target/knife4j-spring-1.0.0.war
# 访问 http://localhost:8080/doc.html
```

> 注：本项目用 **JDK 17** 编译运行。若本机是 JDK 23，编译会因 Spring Boot 2.7.18 默认的 Lombok 1.18.30 不兼容而失败（Lombok 不生成 getter/setter），已在 `pom.xml` 通过 `<lombok.version>1.18.36</lombok.version>` 覆盖解决。

---

## 线上部署与真实接口接入

当前已部署至公网服务器（Ubuntu 22.04），前后端链路为真实 HTTP 调用。**访问地址：http://116.62.81.113/ （前端）、http://116.62.81.113:8080/doc.html （接口文档）。**

### 架构

```
用户 ── :80 nginx ──┬─ 静态前端 /opt/roc-git/h5（uni-app H5 构建产物）
                    └─ /api/* 反向代理 ── :8080 Spring Boot 后端（systemd 服务 rocgit）
```

### 部署要点

| 项 | 说明 |
|---|---|
| 后端 | `knife4j-spring-1.0.0.war`（Fat WAR）置于 `/opt/roc-git`，由 systemd 服务 `rocgit` 守护，开机自启、崩溃自动重启 |
| 前端 | `npm run build:h5` 产物上传至 `/opt/roc-git/h5`，nginx 托管并做 `/api` 反向代理到本地 8080 |
| 登录放行 | `WebMvcConfig` 将 `/api/v1/user/login` 加入 JWT 拦截器白名单（原误写为不存在的 `/api/v1/login`） |
| JWT | `JwtUtil` 以 `Keys.hmacShaKeyFor` + HS256 真实签发 token（原 HS512 base64 解析字符串密钥会抛异常）；`UserController.login` 返回真实 token |

### 前端接入方式

前端新增两层，实现"不改动原型布局、真实调用后端"：

- `src/utils/api.js`：封装全部后端接口，自动存储/注入 `Bearer` token；`baseUrl` 默认相对 `/api`（H5 经 nginx 反代），小程序端可覆盖为完整域名。
- `src/utils/bind-api.js`：给页面节点 `ui['nX'].textContent` 赋值即覆盖原型文本（模板优先渲染 `textContent`），保留布局与交互。每个页面控制器创建后调用对应 `load*` 函数异步拉取并渲染。

已接入页面：首页（登录、个人资料、小组、项目列表、未读数）、项目详情、个人中心、消息、研讨记录。

### 当前限制

后端为**接口文档演示服务，无数据库**（controller 直接构造返回），故页面数据为演示值（如项目名固定"课程设计项目"）。但**登录/鉴权为真实 JWT、请求为真实 HTTP 调用**；后续接入 MySQL 后，前端无需改动即可展示真实数据。原生小程序端仍为 mock，未接入。

## OpenAPI 接口规范

`openapi.yaml` 定义了 ROC-git 全部 **146 个 REST 接口**（用户 9、小组 15、项目 17、文件 28、版本 3、任务 12、协作 18、消息 12、分享 27、通用 5），JWT 认证。使用与部署方式见 `SWAGGER_GUIDE.md`（Swagger Editor / Swagger UI / Redoc / Prism mock 等）。

## 脚本（scripts/）

| 命令 | 脚本 | 作用 |
|---|---|---|
| `npm run migrate` | `migrate.mjs` | 一次性迁移工具，从备份原 HTML 覆盖生成页面/控制器/初始状态，正常开发勿重复运行 |
| `npm run check` | `check.mjs` | 内容检查：逐一对照 6 页元素、文字、属性（含隐藏弹层、表单初始值） |
| `npm run check:browser` | `interaction-check.mjs` | 浏览器交互检查（本机 Edge）：Tab、筛选、弹层、动态表单 |
| `npm run check:layout` | `preview-check.mjs` | 393×852 视口布局检查，生成 `artifacts/*-original.png`、`*-uniapp.png`、`layout-report.json` |
| `npm run build:h5` | — | 验证 H5 编译 |
| `npm run build:mp-weixin` | — | 验证微信小程序编译 |

## 验证

```powershell
npm.cmd run check
npm.cmd run build:h5
npm.cmd run build:mp-weixin
```

启动 H5 开发服务后，在另一终端执行：

```powershell
npm.cmd run check:browser
npm.cmd run check:layout
```

HTML 对照使用本地 Tailwind 和原 Lucide 图形，以免在线 CDN 影响结果。H5 检查不能代替微信开发者工具和微信真机验证。

官方参考：[uni-app CLI 创建和运行](https://uniapp.dcloud.net.cn/quickstart-cli)、[CLI 编译](https://uniapp.dcloud.net.cn/worktile/CLI.html)、[HBuilderX 运行与发行](https://uniapp.dcloud.net.cn/quickstart)。
