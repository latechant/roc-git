# ROC-git · 数据库文件夹说明

本文件夹集中管理 ROC-git（大学生课程小组协作与文档版本管理平台）的全部数据库文件与设计文档，是**数据库层的唯一权威来源**。任何后端开发、联调、报告编写都以本目录为准。

> ⚠️ **v2 重大变更（2026-09-23）**：表结构已按「后端实现 + 需求」重构，详见文末「变更记录」。与旧版（course 体系）不再兼容。

## 一、文件清单

| 文件 | 说明 |
| --- | --- |
| `schema.sql` | 建库建表脚本（MySQL 8.0）。可重复执行：先 `DROP DATABASE` 再重建，**执行会清空数据**。含 22 张表、外键、索引、中文注释 |
| `seed.sql` | 初始化数据：系统默认阶段模板（软件工程课程设计 9 阶段、数据库课程设计 8 阶段）。需在 schema.sql 之后执行 |
| `README.md` | 本文档：目录说明、表关系、状态机、接续开发指引 |

## 二、数据库概要

- 库名：`roc_git`；版本：MySQL 8.0；引擎：InnoDB；字符集：utf8mb4 / utf8mb4_unicode_ci
- 时间字段统一 `DATETIME`，`created_at` 默认当前时间，`updated_at` 自动更新
- 主键统一 `BIGINT UNSIGNED AUTO_INCREMENT`
- 状态字段统一 `VARCHAR` 英文枚举（与后端 VO 一致，见「四、状态机」）

## 三、表清单与关系（22 张表）

### 基础组织
| 表名 | 用途 |
| --- | --- |
| `user` | 用户表（微信 openid 登录） |
| `group` | 小组表（组长 + 邀请码） |
| `group_member` | 小组成员表（组长/组员/教师，成员状态） |

### 项目与阶段
| 表名 | 用途 |
| --- | --- |
| `project` | 项目表（含进度、当前阶段、分享标记） |
| `stage` | 项目阶段表（自定义，可增删改、排序） |
| `stage_template` | 系统默认阶段模板表 |

### 任务与文件
| 表名 | 用途 |
| --- | --- |
| `task` | 任务表（负责人、截止、优先级、进度） |
| `file` | 文件资料表（审核状态机、版本、收藏、冗余标记） |
| `file_version` | 文件版本快照表 |
| `file_review` | 文件审核记录表 |

### 协作与决策
| 表名 | 用途 |
| --- | --- |
| `discussion` | 研讨记录表 |
| `decision` | 决策表（可关联研讨） |
| `achievement` | 成果表 |
| `achievement_file` | 成果关联文件表 |

### 通知与发布
| 表名 | 用途 |
| --- | --- |
| `announcement` | 公告表 |
| `announcement_read` | 公告已读表 |
| `share` | 结项分享表（权限级别、访问统计） |
| `share_access_log` | 分享访问日志表 |
| `message` | 消息表 |
| `user_subscription` | 消息订阅表（5 个通知开关） |

### 留痕与归档
| 表名 | 用途 |
| --- | --- |
| `operation_log` | 操作留痕表（全流程溯源） |
| `archive` | 期末归档表 |

**核心关系**：
- `user 1:N group_member N:1 group`（N:M，经 group_member，带角色）
- `group 1:N project`（一个小组下多个项目）
- `project 1:N stage/task/file/discussion/decision/achievement/operation_log`
- `file 1:N file_version/file_review`；`file 1:N file（parent_id 版本关联）`
- `decision N:1 discussion`（决策可关联研讨）
- `achievement N:M file`（经 achievement_file）
- `announcement N:M user`（经 announcement_read）
- `project 1:1 share`；`share 1:N share_access_log`
- `project 1:1 archive`

## 四、关键状态机（后端实现必须遵守）

| 表.字段 | 枚举值 |
| --- | --- |
| `user` | 无状态字段（微信登录，openid 唯一） |
| `group.status` | `active`=活跃，`archived`=已归档 |
| `group_member.role` | `leader`=组长，`member`=组员，`teacher`=教师 |
| `group_member.status` | `active`=在组，`inviting`=邀请中，`removed`=已移除 |
| `project.status` | `active`=进行中，`completed`=已完成，`archived`=已归档 |
| `stage.status` | `pending`=未开始，`active`=进行中，`completed`=已完成 |
| `task.status` | `pending`=待开始，`in_progress`=进行中，`completed`=已完成 |
| `task.priority` | `high`=高，`medium`=中，`low`=低 |
| `file.status` | `pending`=待审核，`approved`=已通过，`rejected`=已驳回，`archived`=已归档 |
| `file.redundant_flag` | 0=正常，1=闲置，2=冗余 |
| `file.deleted` | 0=正常，1=逻辑删除（保留追溯） |
| `file_review.action` | `approved`=通过，`rejected`=驳回 |
| `announcement.level` | `normal`=普通，`important`=重要，`urgent`=紧急 |
| `share.permission_level` | `view_only`=仅查看，`view_with_preview`=可预览，`view_with_download`=可下载 |
| `share_access_log.action` | `view`=浏览，`download`=下载 |
| `message.type` | `system`=系统，`task`=任务，`mention`=@提及，`audit`=审核 |
| `message.is_read` | 0=未读，1=已读 |

## 五、执行方式

```bash
# 本地 MySQL 命令行（顺序执行）
mysql -u root -p < database/schema.sql
mysql -u root -p < database/seed.sql
```
也可用 Navicat / MySQL Workbench 分别导入两个文件。schema 可重复执行（会重建库），seed 重复执行会因模板重名产生重复行，业务库请按需清理或改为幂等插入。

## 六、接续开发指引（给后续接手的 Agent）

1. **后端对接**：后端按 `pom.xml` 当前无数据库依赖，接入时引入 MyBatis-Plus + MySQL 驱动，按本目录表结构生成实体与 Mapper；字段名统一 `下划线命名`，MyBatis-Plus 开启 `map-underscore-to-camel-case` 自动映射驼峰。实体命名与表一一对应（User/Group/GroupMember/Project/Stage/StageTemplate/Task/File/FileVersion/FileReview/Discussion/Decision/Achievement/Announcement/Share/Message/UserSubscription/OperationLog/Archive 等）。
2. **前端联调**：
   - uni-app 端（`repository/roc-git/src/utils/api.js`）**已接入真实接口**（登录/鉴权为真实 JWT）；后端接入数据库后，登录接口需改为按 `openid` 查/建 `user` 表记录，其余接口改为读写对应表。
   - 原生小程序端（`miniprogram/utils/api.js`）仍为 mock；将 `api.js` 的 `BASE_URL` 改为真实后端 HTTPS 地址即可。
3. **微信登录映射**：`user.openid` 唯一；后端 `login` 接口收到微信 code 换取 openid，查表无则注册新用户（`is_new_user=true`），有则返回已有用户。
4. **字段变更纪律**：任何对表结构的增删改，**必须同步**修改 `schema.sql` 与实验报告 2.4 节；涉及状态枚举的变更同步更新本 README「四、关键状态机」。
5. **权限分层**：管理员（系统级）→ 教师（课程/项目过程性评价）→ 组长（发布任务、编辑阶段、协同审核）→ 组员（上传资料、提交成果、参与审核），后端按 `group_member.role` 校验。
6. **进度提醒**：截止预警与阶段提醒由后端定时任务扫描 `task.deadline`、`stage.end_date` 生成 `message` 记录，前端消息页直接读取；通知开关读取 `user_subscription`。
7. **文件存储**：`file.url` / `file_version.url` 存服务端路径或 OSS key；上传接口由后端实现，文件本体与元数据分离管理。

## 七、变更记录

| 日期 | 变更 |
| --- | --- |
| 2026-09-16 | 初始建库：15 张表（course 体系）+ 阶段模板 seed + 本说明文档 |
| 2026-09-23 | **v2 重构**：①用户改微信 openid 体系；②course 改 group + group_member；③新增 decision/achievement/announcement/share/discussion/subscription 等表；④状态枚举改英文 VARCHAR；共 22 张表。与后端实现、需求数据字典对齐 |
| 2026-09-23 | 更新「六、接续开发指引」：uni-app 端已接入真实接口（非 mock），小程序端仍为 mock |
