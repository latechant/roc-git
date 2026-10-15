# ROC-git 项目 · AI 交接提示词

> **用途**：新开一个 AI 对话窗口时，把下面「二、提示词正文」整段复制粘贴，新窗口的 AI 即可无损接手当前任务。
> **生成日期**：2026-10-15
> **生成人**：上一窗口 AI

---

## 一、交接摘要（给人看的，不必复制）

| 项 | 内容 |
|---|---|
| 项目 | ROC-git 课程小组文档协作管理平台 |
| 代码仓库 | `d:\zxc\work\git\roc-git\repository\roc-git`（前端 A，uni-app + Vue 3） |
| 当前阶段 | 前端重构完成，旧 DOM-adapter 架构已全部清理；**仅维护微信小程序端**（H5 冻结）；小程序体验版已上线 |
| 生产服务器 | 116.62.81.113（Ubuntu 22.04，阿里云） |
| 后端 | Spring Boot + Knife4j，systemd 服务 `rocgit`，端口 8080 |
| 数据库 | MySQL 8.0，库名 `roc_git`，22 张表 |
| 分支 | `main`，本地领先 `origin/main` 22 个 commit（未推送），HEAD = `58b29b4` |

---

## 二、提示词正文（整段复制给新窗口的 AI）

```
你现在接手 ROC-git 项目。请先完整读完本提示词，再行动。不要凭猜测改动代码或架构。

== 一、项目是什么 ==
ROC-git 是「课程小组文档协作管理平台」，大学生以小组为单位协作完成课程作业，
统一管理资料文件、版本、任务分工、研讨决策、审核归档。

仓库根目录：d:\zxc\work\git\roc-git\repository\roc-git
注意：这是「前端 A」仓（uni-app + Vue 3）。仓库外还有「前端 B」原生小程序
(miniprogram/)，不要混淆。后端 (knife4j-spring/) 与数据库 (database/) 在本仓内。

四端关系：
- 前端 A：src/  uni-app + Vue 3 + Vite，编译为 H5 / 微信小程序（本仓，主战场）
- 前端 B：miniprogram/  原生微信小程序（本仓之外，mock 数据，不维护）
- 后端：knife4j-spring/  Spring Boot + Knife4j + MyBatis-Plus + MySQL 8.0
- 数据库：database/  MySQL 8.0，22 张表

== 二、当前状态（截至 2026-10-15） ==
1. 前端重构已完成：架构从旧「DOM-adapter 节点树」迁移为标准 Vue 3 组合式 API
   + Pinia。在线页面：登录、首页、消息、个人中心、项目详情（文件/阶段/成员/
   溯源/任务 5 个 Tab）、创建项目页。
2. 研讨页：已整页删除（2026-10-15），旧 DOM-adapter 架构（page-controller.js、
   bind-api.js、classes.js/json、NativeFragment.vue、controllers/、data/）
   及失效脚本 migrate.mjs/check.mjs 已全部清理。后端研讨接口按「接口预留」保留。
3. 分享页：功能暂不上线，代码保留（pages/share/share.vue 已是标准 Vue）。
4. 个人中心「教学管理」模块（我指导的小组/过程性评价）已弃用移除——需求 v1.1
   起无教师角色，后端无教师端接口。数据库 role 枚举中的 teacher 值保留未动。
5. 仅构建微信小程序端（npm.cmd run build:mp-weixin）。H5 构建命令保留但不再
   验证，服务器 H5 部署冻结不再更新。代码中 #ifdef H5 分支保留不动。
6. 小程序端：已用真实 AppID wxaa0955243cf80259 上传体验版。登录页使用微信官方
   type="nickname" 输入框获取真实昵称，必填；缓存昵称为「微信用户」时不预填。
7. 文件上传限制已上线（前后端+nginx 三层）：单文件 50MB、请求 60MB、类型白名单
   （md/txt/pdf/doc/docx/xls/xlsx/ppt/pptx/png/jpg/jpeg/zip）、文件名消毒防路径穿越。
8. docs/ 已整理：主报告《软件系统分析与设计综合实践》.docx + 任务与报告对照及
   错位说明.md + flowchart_images/ + 参考文献/。中间材料（报告参考模板、功能点、
   技术栈选型、界面交互设计报告体系等）已全部删除。
9. Git：分支 main，本地领先 origin/main 22 个 commit（未推送），HEAD = 58b29b4。

== 三、硬约束（必须遵守，违反即失败）==
1. 每完成一个模块单独提交一个 commit，message 格式：
   feat(refactor): [模块名] 描述（小组功能用 feat(group):，UI 调整用 feat(ui):，
   清理用 chore(refactor):/chore(ui):/chore(docs):）
2. 所有改动必须先写验收清单 → 再实现 → 最后对照清单逐条验证。
3. 禁止 AI 自主进行架构决策、跨模块大改、业务逻辑「优化」。
   只允许在已经定好的规范内实现代码。
4. 小步提交、每步可验证可回退。不允许一次性大规模改动。
5. 长主线任务必须把进度记录到独立 md 文档（现有 REFACTOR_PLAN.md），
   防止上下文丢失。
6. 只构建微信小程序端，不再构建/验证 H5。
7. 微信小程序登录页必须保留 type="nickname" 输入框（获取真实昵称的唯一途径），
   必填，空值仅提示不发起登录；缓存昵称为「微信用户」时不预填。
8. 小组功能无独立详情页，所有小组管理操作统一通过 GroupPopup 弹窗完成。
9. 页面右上角操作与微信胶囊冲突时，采用「合并元素」方案（操作入口合并进
   标题栏），而非移动元素位置——项目详情页标题即「更多操作」入口（可点击+⋯图标）。

== 四、关键路径与命令 ==
必读文档：
- REFACTOR_PLAN.md        —— 重构规划、进度、验收清单、AI 安全原则（最重要）
- readme.md               —— 项目全貌、部署架构、开发/预览方式
- 需求规格说明书.md        —— 需求文档（v1.1 起无教师角色，纯组内协作）
- database/README.md      —— 表结构、状态机
- docs/任务与报告对照及错位说明.md —— 报告描述与工程实现的对照

常用命令（在仓库根目录执行，PowerShell 用 npm.cmd 避免执行策略拦截）：
- npm.cmd run dev:mp-weixin   编译小程序到 dist/dev/mp-weixin（开发）
- npm.cmd run build:mp-weixin 构建小程序包（产物 dist/build/mp-weixin，唯一构建目标）
- npm.cmd run dev:h5 / build:h5  命令保留但已冻结，不再验证
- npm.cmd run check:layout    393×852 视口布局检查（读桌面原 HTML 备份）

== 五、服务器与部署 ==
生产服务器：116.62.81.113（阿里云 Ubuntu 22.04）
SSH：ssh -i "$env:USERPROFILE\.ssh\rocgit_tmp.pem" root@116.62.81.113
架构：
  :80 nginx ──┬─ 静态前端 /opt/roc-git/h5（uni-app H5 产物，已冻结不再更新）
              ├─ /api/* 反代 → :8080 Spring Boot（systemd 服务 rocgit）
              └─ client_max_body_size 60m（配合上传限制）
后端：/opt/roc-git/knife4j-spring-1.0.0.war（Fat WAR，systemd rocgit 守护）
数据库：MySQL 8.0 库 roc_git，仅本机 127.0.0.1:3306 可达
文件存储：/opt/roc-git/files/{uuid}/原名，映射为 /files/** 静态资源；
         无内容去重机制，相同内容重复上传会各存一份。

后端接口前缀为 /api（如 /api/v1/user/login），冒烟测试时注意；新用户登录需
avatarUrl 字段；PowerShell 下 curl 传 JSON 用 --data @file 避免截断。

后端变更部署流程（可回退）：
1. 备份 war：cp /opt/roc-git/knife4j-spring-1.0.0.war{,.bakN}
2. 上传新 war → systemctl restart rocgit
3. nginx 变更前先备份 /etc/nginx/sites-available/rocgit，改完 nginx -t 再 reload

== 六、已知环境坑（会重复踩，提前规避）==
1. npm 安装依赖失败：本机 npm 配了全局代理，需先执行
   npm config delete proxy; npm config delete https-proxy
   再 npm install。装 Pinia 必须指定 2.1.7（vue 3.4.21 不兼容 pinia 最新版）。
2. git 推送走仓库本地代理：`.git/config` 配了 `http(s).proxy=http://127.0.0.1:3066`
   （local 级，非全局/系统代理）。推送前必须先启动 sing-box：
   非沙箱运行 `D:\zxc\work\proxy\sing-box\ai\start_proxy.ps1`（沙箱内启动的进程
   会随命令结束被清理）；github.com 已在代理白名单。代理未运行时推送报
   "Failed to connect to 127.0.0.1 port 3066"。
3. 微信开发者工具导入目录必须是编译产物 dist/dev/mp-weixin 或 dist/build/mp-weixin，
   不要导入根目录或 src。
4. 小程序构建产物会压缩改写函数名与 CSS 属性（paddingRight→padding-right、
   translateX(-50%)→translate(-50%)），验证功能时用特征串而非原函数名 grep。
5. PowerShell 不支持 && 与 heredoc，链式命令用 ; 分隔；commit message 用
   git commit -F <文件> 传入。

== 七、下一步待办（需向用户确认优先级）==
已知未完成项：
- [ ] 小程序端真实接口接入确认 + 后端 HTTPS 合法域名配置（正式发布前置条件；
      体验版/开发版不校验域名可先用，正式发布必须 HTTPS）
- [ ] 消息页「全部已读」按钮被胶囊遮挡——已提议按「合并进标题」原则处理，
      待用户确认（参照项目详情页方案，commit 948361d）
- [ ] 后端 DB 密码/JWT 密钥明文打包在 war 中，待改为环境变量注入
- [ ] 数据备份策略未建立
- [ ] 服务器历史文件/孤儿文件：用户确认暂不清理，仅记录规则——直接删磁盘文件
      会导致下载/版本回滚/审核/溯源断链；仅删 DB 记录会留孤儿文件；只有无任何
      DB 引用的孤立文件可安全清理
- [ ] 分享页（用户选择跳过，如恢复需按 REFACTOR_PLAN 第 13 步）
- [ ] 本地 main 领先 origin/main 22 个 commit，待推送（注意先绕过 git 代理）

== 八、你的工作方式 ==
- 动手前先读 REFACTOR_PLAN.md 和 readme.md，理解既有约定。
- 任何非平凡改动先给方案和验收清单，等用户确认后再写代码。
- 不擅自决定架构、不跨模块大改、不「顺手优化」业务逻辑。
- 每步完成后自验证（npm.cmd run build:mp-weixin + 关键路径），再单独 commit，
  告知用户可回退点。
- 删除文件前必须先反查引用（Grep 全仓），确认无断链风险后再删；
  未跟踪文件删除不可回退，需向用户明示。
- 不确定的信息不要编造，直接问用户。

现在，请先回复：你已理解项目背景、当前状态和硬约束，并询问用户本轮要处理的具体任务。
```

---

## 三、关键信息速查（补充资料，供 AI 按需查阅）

### 3.1 技术栈

| 类别 | 选型 |
|---|---|
| 框架 | Vue 3 组合式 API（`<script setup>`） |
| 跨端 | uni-app 3.x（仅维护微信小程序端，H5 冻结） |
| 状态管理 | Pinia 2.1.7 |
| 样式 | Tailwind CSS 3.4.17 + scoped CSS |
| 构建 | Vite 5.2.8 |
| 请求 | uni.request 封装（`src/utils/api.js`），H5 走相对 `/api` 经 nginx 反代；小程序端 BASE_URL 覆盖为完整域名即可复用 |

### 3.2 目录结构（src/）

```
src/
├── stores/           # Pinia：user / group / project / message
├── components/       # AppButton / AppCard / AppEmpty / AppBadge / MemberAvatar / TabBar
│   └── project/      # ProjectFileTab / ProjectStageTab / ProjectTaskTab / ProjectMemberTab / ProjectTraceTab / GroupPopup
├── pages/            # login / home / project / create / messages / profile（+ 保留 share）
├── utils/            # api.js（请求层）、format.js、avatar.js
├── pages.json        # 页面注册、自定义导航
├── manifest.json     # 微信 AppID、版本、平台配置（mp-weixin.appid = wxaa0955243cf80259）
└── main.js           # 入口，已集成 Pinia
```

### 3.3 登录/鉴权要点

- `WebMvcConfig` JWT 白名单：`/api/v1/user/login`、`/api/v1/share/p/**`、`/api/v1/group/validate-invite-code`
- 小程序端：`uni.login` 取 code → 后端 code2session 换 openid；昵称由用户通过 `type="nickname"` 输入框主动填写（2022 年微信隐私政策后无静默获取 API）
- H5 端以 `wx_user_<设备ID>` 作为 code 走登录（H5 已冻结，仅备查）；token 存本地 storage，请求带 `Authorization: Bearer <token>`

### 3.4 最近提交历史（尾部）

```
58b29b4 chore(docs): [docs] 整理文档目录，移除被主报告取代的中间材料
a4dd112 chore(docs): [报告] 教学管理模块一致性标注（口径A）
b7810ea chore(ui): [个人中心] 移除未实现的「教学管理」占位模块
78d036e chore(refactor): [旧架构] 删除失效迁移/校验脚本并同步过期文档
68825e9 chore(refactor): [旧架构] 清理已孤儿的 DOM-adapter 残留文件
a7fe7c4 chore(ui): [研讨] 删除研讨记录页及其 controller 与原型数据
96e8eaf chore(ui): [协作消息] 移除研讨 Tab 及关联映射
948361d feat(ui): [项目详情页] 标题与更多操作合并为可点元素
4b581cc feat(file): [后端] 文件上传增加大小/类型/文件名安全限制
7afcee3 fix(login): [登录页] 缓存昵称为占位名微信用户时不再预填
```

---

*文档维护人：项目组 · 最后更新 2026-10-15*
