# ROC-git · uni-app（Vue 3）

原有 6 个 HTML 页面已迁移为 **uni-app + Vue 3 + Vite**，支持 H5 和微信小程序。原文字、示例数据、颜色、字号、卡片尺寸、导航及弹层内容保留。原 HTML 已备份到桌面的 `快速UI-原始HTML备份-20260916-180046` 文件夹，并从项目中删除。新项目源码在 `src`；根目录 `index.html` 是 Vite 的 H5 入口，需要保留。

这是前端原型迁移，原项目没有后端。原来仅弹出提示的上传、审核、邀请、版本恢复、归档等操作仍保留原型行为，没有接入真实登录、文件存储、数据库或业务 API。原 README 提到 `teacher.html`，但实际没有该文件；“我指导的小组”和“过程性评价”入口保留，目标页面仍缺失，没有编造新内容。首页原本引用未定义的 `showFileSearch()`，迁移后保留其进入项目页的导航。

## VS Code 预览

**可以使用 VS Code，不强制使用 HBuilderX。** 安装 Node.js 20 或 22 LTS，在 VS Code 打开项目根目录，终端执行：

```powershell
npm.cmd install
npm.cmd run dev:h5
```

打开终端地址，默认 **http://127.0.0.1:5173/**。保存 Vue 文件后自动刷新。浏览器开发者工具可切换手机视口，原设计上限是 **393 × 852 px**，在该尺寸下对照最方便。新项目不能通过 Live Server 或双击 Vue 文件运行。

Windows PowerShell 如果提示 `npm.ps1` 被执行策略阻止，使用 `npm.cmd` 即可，不需要修改系统执行策略。macOS、Linux 或其他终端使用 `npm`。

VS Code 建议安装 **Vue - Official** 扩展。浏览器预览的是 H5；小程序模拟器和微信真机预览需要微信开发者工具。

## 微信小程序开发预览

1. 安装并登录[微信开发者工具](https://developers.weixin.qq.com/miniprogram/dev/devtools/download.html)。正式发布需要在[微信公众平台](https://mp.weixin.qq.com/)注册小程序，取得 AppID，并将当前微信用户加入开发者名单。
2. 在 `src/manifest.json` 填写 `mp-weixin.appid`，即微信小程序 AppID（通常以 `wx` 开头），不是上层 DCloud `appid`。仅试运行可以在开发者工具使用测试模式；正式发布必须使用真实 AppID。
3. 在项目根目录执行：

```powershell
npm.cmd run dev:mp-weixin
```

4. 微信开发者工具选择“导入项目”，目录选择 **`dist/dev/mp-weixin`**，填写一致的 AppID。不要导入根目录或 `src`，工具需要的是编译后的原生小程序工程。
5. 保持终端运行，修改 Vue 页面后会自动重新编译。在开发者工具查看效果，点击“预览”，用微信扫码检查真机。

无需再在微信开发者工具执行“构建 npm”，依赖已经由 uni-app 编译器处理。

## 上传与正式发布

填写真实 AppID 后执行：

```powershell
npm.cmd run build:mp-weixin
```

微信开发者工具导入 **`dist/build/mp-weixin`**，编译并检查页面，点击“上传”，填写版本号和备注。随后登录微信公众平台，在“版本管理”中将开发版本提交审核，审核通过后再发布。小程序代码通过此流程发布，无需把 Vue 源码上传到网页服务器。

目前没有网络业务接口，演示界面无需配置业务服务器。将来接入后端时，要在公众平台配置 HTTPS 合法域名（request、uploadFile、downloadFile 等），并实现真实登录、上传、权限和数据持久化。

微信状态栏、右上角胶囊和原生选择器弹出界面由微信控制，无法与 HTML 浏览器逐像素相同；本项目采用自定义导航和本地样式保留原布局。发布前请在自己的手机检查顶部按钮与胶囊、底部导航是否重叠。

## HBuilderX 用法

也可以安装[HBuilderX](https://www.dcloud.io/hbuilderx.html)，导入项目根目录，先安装 npm 依赖，再选择“运行 → 运行到浏览器”或“运行 → 运行到小程序模拟器 → 微信开发者工具”。按提示配置微信开发者工具路径，发行菜单可生成微信发布包。

这是 CLI 项目，业务源码在 `src`。命令行输出固定为 `dist/dev/mp-weixin`、`dist/build/mp-weixin`；HBuilderX 菜单运行或发行时以控制台提示的实际目录为准。不要单独把 `src` 当作 HBuilderX 工程导入。

## 页面与代码

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
- `src/components/NativeFragment.vue`：新增成员、阶段、任务和研讨落地项的原生表单行。
- `src/components/NativeDate.vue`：仅 H5 使用的浏览器日期输入，小程序使用 `picker`。
- `docs/original-readme.md`、`docx_content.txt`：保留原说明和需求材料。

Vue 页面使用 `view`、`text`、`button`、`scroll-view`、`input`、`textarea`、`picker`、`image`，没有用 web-view 包装 HTML。原型动作通过响应式状态适配器保留，以减少重写造成的展示和行为变化。

后续维护直接编辑 `src`。`npm.cmd run migrate` 是一次性迁移工具，会从桌面备份的原 HTML **覆盖生成的页面、控制器和初始状态**，正常开发不要重复运行。内容和布局对照检查也读取该备份；若备份移动，可设置环境变量 `ORIGINAL_HTML_DIR` 指定新路径。正常运行及构建不依赖备份。

## 验证

```powershell
npm.cmd run check
npm.cmd run build:h5
npm.cmd run build:mp-weixin
```

内容检查逐一对照全部 6 页的元素、文字、属性，包括隐藏弹层和表单初始值，并检查本地图标及主要状态操作。两个 build 命令分别验证 H5 和微信小程序编译。

启动 H5 开发服务后，在另一终端执行：

```powershell
npm.cmd run check:browser
npm.cmd run check:layout
```

浏览器检查使用本机 Microsoft Edge，验证 Tab、筛选、弹层、动态表单等交互。布局检查在 393 × 852 视口生成 `artifacts/*-original.png`、`artifacts/*-uniapp.png` 和 `layout-report.json`。HTML 对照使用本地 Tailwind 和原 Lucide 图形，以免在线 CDN 影响结果。H5 检查不能代替微信开发者工具和微信真机验证。

官方参考：[uni-app CLI 创建和运行](https://uniapp.dcloud.net.cn/quickstart-cli)、[CLI 编译](https://uniapp.dcloud.net.cn/worktile/CLI.html)、[HBuilderX 运行与发行](https://uniapp.dcloud.net.cn/quickstart)。
