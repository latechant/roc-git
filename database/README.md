# ROC-git · 数据库文件夹说明

本文件夹集中管理 ROC-git（大学生课程小组协作与文档版本管理平台）的全部数据库文件与设计文档，是**数据库层的唯一权威来源**。任何后端开发、联调、报告编写都以本目录为准。

## 一、文件清单

| 文件 | 说明 |
| --- | --- |
| `schema.sql` | 建库建表脚本（MySQL 8.0）。可重复执行：先 `DROP DATABASE` 再重建，**执行会清空数据**。含 15 张表、外键、索引、字段/表中文注释 |
| `seed.sql` | 初始化数据：系统默认阶段模板（软件工程课程设计 9 阶段、数据库课程设计 8 阶段）。需在 schema.sql 之后执行 |
| `README.md` | 本文档：目录说明、表关系、状态机、接续开发指引 |

## 二、数据库概要

- 库名：`roc_git`；版本：MySQL 8.0；引擎：InnoDB；字符集：utf8mb4 / utf8mb4_unicode_ci
- 时间字段统一 `DATETIME`，`created_at` 默认当前时间，`updated_at` 自动更新
- 主键统一 `BIGINT UNSIGNED AUTO_INCREMENT`
- 表结构与实验报告 **2.4 数据库设计** 一节一一对应（含字段）

## 三、表清单与关系（15 张表）

| 表名 | 用途 | 对应功能点 |
| --- | --- | --- |
| `user` | 用户（管理员/教师/学生），含角色分层 | 功能点 1 |
| `course` | 课程，教师创建 | 功能点 1 |
| `project` | 作业项目（即小组），含状态与当前阶段 | 功能点 1、12、15 |
| `project_member` | 项目成员（组长/组员/教师），`(project_id,user_id)` 唯一 | 功能点 1 |
| `stage` | 项目自定义阶段（增删改、排序、起止时间） | 功能点 2、12 |
| `stage_template` | 系统默认阶段模板（seed 预置） | 功能点 2 |
| `task` | 任务分工（负责人 + 截止时间） | 功能点 3、8 |
| `file` | 资料/成果文件，含审核状态机与冗余标记 | 功能点 4、5、6、10 |
| `file_version` | 文件版本快照（`(file_id,version_no)` 唯一） | 功能点 11 |
| `file_review` | 文件审核记录（通过/驳回 + 原因） | 功能点 5、10 |
| `meeting` | 研讨记录（议题、参与人、决策、纪要） | 功能点 7 |
| `meeting_action` | 研讨落地项（关联任务/文件/阶段） | 功能点 8 |
| `message` | 消息提醒（阶段变更/截止预警/审核结果/新版本/研讨） | 进度提醒 |
| `operation_log` | 操作留痕（全流程溯源、教师过程性评价依据） | 功能点 13、14 |
| `archive` | 期末归档（一个项目一条，唯一） | 功能点 15 |

**核心关系**：`user 1:N course`；`course 1:N project`；`user N:M project`（经 `project_member`）；`project 1:N stage/task/file/meeting/operation_log`；`file 1:N file_version/file_review`；`meeting 1:N meeting_action`；`project 1:1 archive`。
**特别注意**：`project.current_stage_id` 是**逻辑外键**（指向 `stage.id`），为避免循环依赖未建物理外键，只建了索引。

## 四、关键状态机（后端实现必须遵守）

- `file.status`：`0 草稿 → 1 待审核 → 2 已通过 / 3 已驳回 → 4 已作废`；驳回后可修改重新提交（回到 1）
- `file.redundant_flag`：0 正常 / 1 闲置 / 2 冗余（支持批量清理、归档隐藏）
- `file.deleted`：0 正常 / 1 归档隐藏（不物理删除，保留可追溯）
- `stage.status`：0 未开始 / 1 进行中 / 2 已完成；`project.status`：1 进行中 / 2 已归档 / 3 已结项
- `task.status`：0 待开始 / 1 进行中 / 2 已完成 / 3 已逾期（逾期由定时任务扫描 deadline 更新）
- `user.role`：1 管理员 / 2 教师 / 3 学生；`project_member.role_in_project`：1 组长 / 2 组员 / 3 教师

## 五、执行方式

```bash
# 本地 MySQL 命令行（顺序执行）
mysql -u root -p < database/schema.sql
mysql -u root -p < database/seed.sql
```
也可用 Navicat / MySQL Workbench 分别导入两个文件。schema 可重复执行（会重建库），seed 重复执行会因模板重名产生重复行，业务库请按需清理或改为幂等插入。

## 六、接续开发指引（给后续接手的 Agent）

1. **后端对接**：后端按技术选型文档使用 **SpringBoot + MyBatis-Plus + MySQL**。按本目录表结构生成实体与 Mapper；字段名统一 `下划线命名`，MyBatis-Plus 开启 `map-underscore-to-camel-case` 自动映射驼峰。实体/接口命名与表一一对应（User/Course/Project/ProjectMember/Stage/StageTemplate/Task/File/FileVersion/FileReview/Meeting/MeetingAction/Message/OperationLog/Archive）。
2. **前端联调切换**：
   - uni-app 端（`repository/roc-git/src/utils`）与小程序端（`miniprogram/utils/api.js`）目前均为 **mock 数据**；
   - 后端就绪后，将 `useMock` 置为 `false`、`baseUrl` 改为真实后端 HTTPS 地址，接口路径已按 `/api/...` 预留，无需改页面代码。
3. **字段变更纪律**：任何对表结构的增删改，**必须同步**修改 `schema.sql` 与实验报告 2.4 节；涉及状态枚举的变更同步更新本 README「四、关键状态机」。
4. **权限分层**：管理员（系统级）→ 教师（课程/项目过程性评价、查阅全部过程材料）→ 组长（发布任务、编辑阶段、协同审核）→ 组员（上传资料、提交成果、参与审核），后端按 `user.role` + `project_member.role_in_project` 双重校验。
5. **进度提醒**：截止预警与阶段提醒由后端定时任务扫描 `task.deadline`、`stage.end_date` 生成 `message` 记录，前端消息页直接读取。
6. **文件存储**：`file.storage_path` / `file_version.file_path` 存服务端路径或 OSS key；上传接口（含分片/断点续传，对应选型文档 WebUploader 能力）由后端实现，文件本体与元数据分离管理。

## 七、变更记录

| 日期 | 变更 |
| --- | --- |
| 2026-09-16 | 初始建库：15 张表 + 阶段模板 seed + 本说明文档 |
