-- ============================================================================
-- ROC-git 大学生课程小组协作平台 · 数据库结构
-- 数据库：roc_git  |  MySQL 8.0  |  InnoDB  |  utf8mb4
-- 说明：
--   1. 本脚本可重复执行（先 DROP DATABASE 再重建），执行会清空全部数据，请勿在
--      已有业务数据的库上直接运行。
--   2. 表结构对应实验报告 2.4 数据库设计一节，字段增删必须同步更新报告。
--   3. 表关系、状态机、接续开发指引见同目录 README.md。
-- ============================================================================

DROP DATABASE IF EXISTS roc_git;
CREATE DATABASE roc_git DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE roc_git;

-- ----------------------------------------------------------------------------
-- 1. 用户表（管理员 / 教师 / 学生）
-- ----------------------------------------------------------------------------
CREATE TABLE `user` (
  `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username`      VARCHAR(50)  NOT NULL COMMENT '登录名',
  `password_hash` VARCHAR(255) NOT NULL COMMENT '密码哈希（BCrypt）',
  `real_name`     VARCHAR(50)  NOT NULL COMMENT '姓名',
  `student_no`    VARCHAR(20)  DEFAULT NULL COMMENT '学号（教师/管理员可为空）',
  `role`          TINYINT      NOT NULL DEFAULT 3 COMMENT '系统角色：1=管理员，2=教师，3=学生',
  `avatar`        VARCHAR(255) DEFAULT NULL COMMENT '头像地址',
  `phone`         VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
  `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_username` (`username`),
  UNIQUE KEY `uk_user_student_no` (`student_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表（管理员/教师/学生）';

-- ----------------------------------------------------------------------------
-- 2. 课程表（教师创建）
-- ----------------------------------------------------------------------------
CREATE TABLE `course` (
  `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `teacher_id`  BIGINT UNSIGNED NOT NULL COMMENT '授课教师（user.id）',
  `course_name` VARCHAR(100) NOT NULL COMMENT '课程名称',
  `course_code` VARCHAR(30)  DEFAULT NULL COMMENT '课程编号',
  `term`        VARCHAR(20)  DEFAULT NULL COMMENT '学期',
  `description` TEXT         COMMENT '课程简介',
  `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1=进行中，2=已结束',
  `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_course_teacher` (`teacher_id`),
  CONSTRAINT `fk_course_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程表';

-- ----------------------------------------------------------------------------
-- 3. 作业项目表（即小组，一个课程下多个项目）
--    注：current_stage_id 为逻辑外键（避免与 stage 循环依赖），不建物理外键
-- ----------------------------------------------------------------------------
CREATE TABLE `project` (
  `id`               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `course_id`        BIGINT UNSIGNED NOT NULL COMMENT '所属课程（course.id）',
  `name`             VARCHAR(100) NOT NULL COMMENT '项目名称',
  `description`      TEXT         COMMENT '项目简介',
  `status`           TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1=进行中，2=已归档，3=已结项',
  `current_stage_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '当前阶段（逻辑外键 stage.id，可空）',
  `created_by`       BIGINT UNSIGNED NOT NULL COMMENT '创建人（组长，user.id）',
  `archived_at`      DATETIME     DEFAULT NULL COMMENT '归档时间',
  `created_at`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_project_course` (`course_id`),
  KEY `idx_project_creator` (`created_by`),
  KEY `idx_project_stage` (`current_stage_id`),
  CONSTRAINT `fk_project_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`),
  CONSTRAINT `fk_project_creator` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作业项目表（即小组）';

-- ----------------------------------------------------------------------------
-- 4. 项目成员表（组长 / 组员 / 教师 分层权限）
-- ----------------------------------------------------------------------------
CREATE TABLE `project_member` (
  `id`              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `project_id`      BIGINT UNSIGNED NOT NULL COMMENT '项目（project.id）',
  `user_id`         BIGINT UNSIGNED NOT NULL COMMENT '成员（user.id）',
  `role_in_project` TINYINT NOT NULL DEFAULT 2 COMMENT '项目内角色：1=组长，2=组员，3=教师',
  `joined_at`       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_member` (`project_id`, `user_id`),
  KEY `idx_member_user` (`user_id`),
  CONSTRAINT `fk_member_project` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_member_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目成员表（组长/组员/教师）';

-- ----------------------------------------------------------------------------
-- 5. 项目阶段表（自定义，支持增删改、排序、起止时间）
-- ----------------------------------------------------------------------------
CREATE TABLE `stage` (
  `id`         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `project_id` BIGINT UNSIGNED NOT NULL COMMENT '所属项目（project.id）',
  `name`       VARCHAR(50) NOT NULL COMMENT '阶段名称（可自定义）',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '阶段顺序',
  `start_date` DATE DEFAULT NULL COMMENT '开始日期',
  `end_date`   DATE DEFAULT NULL COMMENT '截止日期',
  `status`     TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0=未开始，1=进行中，2=已完成',
  `remark`     VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_stage_project` (`project_id`),
  CONSTRAINT `fk_stage_project` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目阶段表';

-- ----------------------------------------------------------------------------
-- 6. 系统默认阶段模板表（新建项目一键套用）
-- ----------------------------------------------------------------------------
CREATE TABLE `stage_template` (
  `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `template_name` VARCHAR(50) NOT NULL COMMENT '模板名称（如：软件工程课程设计）',
  `stage_name`    VARCHAR(50) NOT NULL COMMENT '模板阶段名',
  `sort_order`    INT NOT NULL DEFAULT 0 COMMENT '阶段顺序',
  PRIMARY KEY (`id`),
  KEY `idx_template_name` (`template_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统默认阶段模板表';

-- ----------------------------------------------------------------------------
-- 7. 任务分工表（组长发布，负责人 + 截止时间）
-- ----------------------------------------------------------------------------
CREATE TABLE `task` (
  `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `project_id`  BIGINT UNSIGNED NOT NULL COMMENT '所属项目（project.id）',
  `stage_id`    BIGINT UNSIGNED DEFAULT NULL COMMENT '所属阶段（stage.id，可空）',
  `title`       VARCHAR(100) NOT NULL COMMENT '任务内容',
  `assignee_id` BIGINT UNSIGNED NOT NULL COMMENT '负责人（user.id）',
  `deadline`    DATETIME DEFAULT NULL COMMENT '截止时间',
  `status`      TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0=待开始，1=进行中，2=已完成，3=已逾期',
  `remark`      VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `created_at`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_task_project` (`project_id`),
  KEY `idx_task_stage` (`stage_id`),
  KEY `idx_task_assignee` (`assignee_id`),
  CONSTRAINT `fk_task_project` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_task_stage` FOREIGN KEY (`stage_id`) REFERENCES `stage` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_task_assignee` FOREIGN KEY (`assignee_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务分工表';

-- ----------------------------------------------------------------------------
-- 8. 文件资料表（资料 / 成果，状态机：草稿→待审核→已通过/已驳回→已作废）
-- ----------------------------------------------------------------------------
CREATE TABLE `file` (
  `id`             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `project_id`     BIGINT UNSIGNED NOT NULL COMMENT '所属项目（project.id）',
  `stage_id`       BIGINT UNSIGNED DEFAULT NULL COMMENT '所属阶段（stage.id，可空）',
  `file_name`      VARCHAR(200) NOT NULL COMMENT '文件名',
  `file_type`      VARCHAR(20)  DEFAULT NULL COMMENT '扩展名（如 docx/pdf/png）',
  `file_size`      BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '文件大小（字节）',
  `storage_path`   VARCHAR(255) NOT NULL COMMENT '存储路径（磁盘/OSS）',
  `uploader_id`    BIGINT UNSIGNED NOT NULL COMMENT '上传人（user.id）',
  `status`         TINYINT NOT NULL DEFAULT 0 COMMENT '状态机：0=草稿，1=待审核，2=已通过，3=已驳回，4=已作废',
  `redundant_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '冗余标记：0=正常，1=闲置，2=冗余',
  `version_no`     INT NOT NULL DEFAULT 1 COMMENT '当前版本号',
  `deleted`        TINYINT NOT NULL DEFAULT 0 COMMENT '归档隐藏：0=正常，1=已归档隐藏',
  `created_at`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_file_project` (`project_id`),
  KEY `idx_file_stage` (`stage_id`),
  KEY `idx_file_uploader` (`uploader_id`),
  KEY `idx_file_status` (`status`),
  CONSTRAINT `fk_file_project` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_file_stage` FOREIGN KEY (`stage_id`) REFERENCES `stage` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_file_uploader` FOREIGN KEY (`uploader_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件资料表（资料与成果，含审核状态机）';

-- ----------------------------------------------------------------------------
-- 9. 文件版本快照表（每次修改生成新版本，支持比对与回滚）
-- ----------------------------------------------------------------------------
CREATE TABLE `file_version` (
  `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `file_id`     BIGINT UNSIGNED NOT NULL COMMENT '文件（file.id）',
  `version_no`  INT NOT NULL COMMENT '版本号（从 1 递增）',
  `file_path`   VARCHAR(255) NOT NULL COMMENT '该版本的存储路径',
  `file_size`   BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '该版本大小（字节）',
  `operator_id` BIGINT UNSIGNED NOT NULL COMMENT '提交人（user.id）',
  `remark`      VARCHAR(255) DEFAULT NULL COMMENT '修改备注',
  `created_at`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '版本生成时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_file_version` (`file_id`, `version_no`),
  KEY `idx_version_operator` (`operator_id`),
  CONSTRAINT `fk_version_file` FOREIGN KEY (`file_id`) REFERENCES `file` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_version_operator` FOREIGN KEY (`operator_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件版本快照表';

-- ----------------------------------------------------------------------------
-- 10. 文件审核记录表（挂单审核准入：通过 / 驳回 + 原因）
-- ----------------------------------------------------------------------------
CREATE TABLE `file_review` (
  `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `file_id`     BIGINT UNSIGNED NOT NULL COMMENT '被审文件（file.id）',
  `reviewer_id` BIGINT UNSIGNED NOT NULL COMMENT '审核人（user.id）',
  `action`      TINYINT NOT NULL COMMENT '审核动作：1=通过，2=驳回',
  `reason`      VARCHAR(255) DEFAULT NULL COMMENT '驳回原因（通过时可为空）',
  `reviewed_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '审核时间',
  PRIMARY KEY (`id`),
  KEY `idx_review_file` (`file_id`),
  CONSTRAINT `fk_review_file` FOREIGN KEY (`file_id`) REFERENCES `file` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_review_reviewer` FOREIGN KEY (`reviewer_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件审核记录表';

-- ----------------------------------------------------------------------------
-- 11. 研讨记录表（线上线下研讨：议题、参与人、时间、决策结论、纪要）
-- ----------------------------------------------------------------------------
CREATE TABLE `meeting` (
  `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `project_id`  BIGINT UNSIGNED NOT NULL COMMENT '所属项目（project.id）',
  `meeting_no`  INT NOT NULL DEFAULT 1 COMMENT '研讨序号',
  `meeting_type` TINYINT NOT NULL DEFAULT 1 COMMENT '形式：1=线上，2=线下',
  `topic`       VARCHAR(200) NOT NULL COMMENT '研讨议题',
  `participants` VARCHAR(500) DEFAULT NULL COMMENT '参与人ID（逗号分隔，user.id）',
  `location`    VARCHAR(100) DEFAULT NULL COMMENT '地点（线下研讨）',
  `meeting_time` DATETIME DEFAULT NULL COMMENT '研讨时间',
  `decision`    TEXT COMMENT '决策结论',
  `minutes`     TEXT COMMENT '研讨纪要',
  `created_by`  BIGINT UNSIGNED NOT NULL COMMENT '记录人（user.id）',
  `created_at`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_meeting_project` (`project_id`),
  CONSTRAINT `fk_meeting_project` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_meeting_creator` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='研讨记录表';

-- ----------------------------------------------------------------------------
-- 12. 研讨落地项表（研讨结论转化为任务/文件/阶段的更新项）
-- ----------------------------------------------------------------------------
CREATE TABLE `meeting_action` (
  `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `meeting_id`  BIGINT UNSIGNED NOT NULL COMMENT '所属研讨（meeting.id）',
  `content`     VARCHAR(255) NOT NULL COMMENT '落地内容',
  `target_type` TINYINT DEFAULT NULL COMMENT '落地对象类型：1=任务，2=文件，3=阶段',
  `target_id`   BIGINT UNSIGNED DEFAULT NULL COMMENT '落地对象ID（task/file/stage.id）',
  `status`      TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0=待处理，1=已完成',
  `deadline`    DATETIME DEFAULT NULL COMMENT '截止时间',
  `assignee_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '负责人（user.id）',
  `created_at`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_action_meeting` (`meeting_id`),
  KEY `idx_action_assignee` (`assignee_id`),
  CONSTRAINT `fk_action_meeting` FOREIGN KEY (`meeting_id`) REFERENCES `meeting` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_action_assignee` FOREIGN KEY (`assignee_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='研讨落地项表';

-- ----------------------------------------------------------------------------
-- 13. 消息提醒表（阶段变更 / 截止预警 / 审核结果 / 新版本 / 研讨提醒）
-- ----------------------------------------------------------------------------
CREATE TABLE `message` (
  `id`         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id`    BIGINT UNSIGNED NOT NULL COMMENT '接收人（user.id）',
  `project_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '关联项目（project.id，可空）',
  `type`       TINYINT NOT NULL DEFAULT 1 COMMENT '类型：1=阶段变更，2=截止预警，3=审核结果，4=新版本，5=研讨提醒',
  `content`    VARCHAR(255) NOT NULL COMMENT '消息内容',
  `is_read`    TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读：0=未读，1=已读',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_message_user` (`user_id`, `is_read`),
  KEY `idx_message_project` (`project_id`),
  CONSTRAINT `fk_message_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_message_project` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息提醒表';

-- ----------------------------------------------------------------------------
-- 14. 操作留痕表（全流程可视化追溯、教师过程性评价依据）
-- ----------------------------------------------------------------------------
CREATE TABLE `operation_log` (
  `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `project_id`  BIGINT UNSIGNED NOT NULL COMMENT '所属项目（project.id）',
  `user_id`     BIGINT UNSIGNED NOT NULL COMMENT '操作人（user.id）',
  `action`      VARCHAR(50) NOT NULL COMMENT '操作类型（上传/审核/修改/回滚/归档等）',
  `target_type` VARCHAR(20) DEFAULT NULL COMMENT '对象类型（file/stage/task/meeting 等）',
  `target_id`   BIGINT UNSIGNED DEFAULT NULL COMMENT '对象ID',
  `detail`      VARCHAR(255) DEFAULT NULL COMMENT '操作详情',
  `created_at`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`),
  KEY `idx_log_project_time` (`project_id`, `created_at`),
  KEY `idx_log_user` (`user_id`),
  CONSTRAINT `fk_log_project` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_log_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作留痕表';

-- ----------------------------------------------------------------------------
-- 15. 期末归档表（课程/小组全量资料一键归档）
-- ----------------------------------------------------------------------------
CREATE TABLE `archive` (
  `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `project_id`   BIGINT UNSIGNED NOT NULL COMMENT '归档项目（project.id）',
  `archive_by`   BIGINT UNSIGNED NOT NULL COMMENT '归档操作人（user.id）',
  `archive_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '归档时间',
  `package_path` VARCHAR(255) DEFAULT NULL COMMENT '归档包路径',
  `summary`      TEXT COMMENT '归档说明',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_archive_project` (`project_id`),
  CONSTRAINT `fk_archive_project` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_archive_by` FOREIGN KEY (`archive_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='期末归档表';
