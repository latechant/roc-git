-- ============================================================================
-- ROC-git 大学生课程小组协作平台 · 数据库结构（v2 · 对齐后端实现与需求）
-- 数据库：roc_git  |  MySQL 8.0  |  InnoDB  |  utf8mb4
--
-- 版本说明（v2，2026-09-23 重构）：
--   1. 用户体系改为【微信 openid 登录】（需求数据字典 8.1.1：openid/nickname/avatar_url，
--      明确"不需要手机号、学号、工号"），与后端 UserController 实现一致。
--   2. 组织体系由旧 course(课程) 改为【group(小组) + group_member(成员)】，
--      与后端 GroupController、需求数据字典 8.1.2/8.1.3 一致。
--   3. 补充后端已实现、但旧库缺失的功能实体：decision(决策)、achievement(成果)、
--      announcement(公告)、share(分享)、user_subscription(订阅)、discussion(研讨)、
--      share_access_log(访问日志)。
--   4. 表结构、状态枚举与后端 VO 字段一一对应（见 database/README.md）。
--   5. 本脚本可重复执行（先 DROP DATABASE 再重建），执行会清空全部数据。
-- ============================================================================

DROP DATABASE IF EXISTS roc_git;
CREATE DATABASE roc_git DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE roc_git;

-- ----------------------------------------------------------------------------
-- 1. 用户表（微信 openid 登录）
-- ----------------------------------------------------------------------------
CREATE TABLE `user` (
  `id`         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `openid`     VARCHAR(64)  NOT NULL COMMENT '微信 OpenID（唯一）',
  `nickname`   VARCHAR(32)  DEFAULT NULL COMMENT '微信昵称',
  `avatar_url` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_openid` (`openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表（微信登录）';

-- ----------------------------------------------------------------------------
-- 2. 小组表（一个课程作业下的小组，组长 + 成员）
-- ----------------------------------------------------------------------------
CREATE TABLE `group` (
  `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name`        VARCHAR(50)  NOT NULL COMMENT '小组名称',
  `description` VARCHAR(255) DEFAULT NULL COMMENT '小组描述',
  `leader_id`   BIGINT UNSIGNED NOT NULL COMMENT '组长（user.id）',
  `invite_code` VARCHAR(16)  DEFAULT NULL COMMENT '邀请码（加入小组用）',
  `qr_code_url` VARCHAR(255) DEFAULT NULL COMMENT '邀请二维码URL',
  `status`      VARCHAR(16)  NOT NULL DEFAULT 'active' COMMENT '状态：active=活跃，archived=已归档',
  `created_at`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_group_invite` (`invite_code`),
  KEY `idx_group_leader` (`leader_id`),
  CONSTRAINT `fk_group_leader` FOREIGN KEY (`leader_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='小组表';

-- ----------------------------------------------------------------------------
-- 3. 小组成员表（组长/组员/教师，成员状态）
-- ----------------------------------------------------------------------------
CREATE TABLE `group_member` (
  `id`         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `group_id`   BIGINT UNSIGNED NOT NULL COMMENT '小组（group.id）',
  `user_id`    BIGINT UNSIGNED NOT NULL COMMENT '成员（user.id）',
  `role`       VARCHAR(16) NOT NULL DEFAULT 'member' COMMENT '角色：leader=组长，member=组员，teacher=教师',
  `status`     VARCHAR(16) NOT NULL DEFAULT 'active' COMMENT '状态：active=在组，inviting=邀请中，removed=已移除',
  `remark`     VARCHAR(64) DEFAULT NULL COMMENT '成员备注（组长可改）',
  `joined_at`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_group_member` (`group_id`, `user_id`),
  KEY `idx_gm_user` (`user_id`),
  CONSTRAINT `fk_gm_group` FOREIGN KEY (`group_id`) REFERENCES `group` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_gm_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='小组成员表';

-- ----------------------------------------------------------------------------
-- 4. 项目表（一个小组下多个项目；current_stage_id 为逻辑外键避免循环）
-- ----------------------------------------------------------------------------
CREATE TABLE `project` (
  `id`               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `group_id`         BIGINT UNSIGNED NOT NULL COMMENT '所属小组（group.id）',
  `name`             VARCHAR(100) NOT NULL COMMENT '项目名称',
  `description`      TEXT         COMMENT '项目描述',
  `status`           VARCHAR(16)  NOT NULL DEFAULT 'active' COMMENT '状态：active=进行中，completed=已完成，archived=已归档',
  `progress`         INT NOT NULL DEFAULT 0 COMMENT '进度（0-100）',
  `current_stage_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '当前阶段（逻辑外键 stage.id，可空）',
  `created_by`       BIGINT UNSIGNED NOT NULL COMMENT '创建人（user.id）',
  `is_shared`        TINYINT NOT NULL DEFAULT 0 COMMENT '是否已分享：0=否，1=是',
  `share_token`      VARCHAR(64)  DEFAULT NULL COMMENT '结项分享Token',
  `archived_at`      DATETIME     DEFAULT NULL COMMENT '归档时间',
  `created_at`       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_project_group` (`group_id`),
  KEY `idx_project_creator` (`created_by`),
  KEY `idx_project_stage` (`current_stage_id`),
  CONSTRAINT `fk_project_group` FOREIGN KEY (`group_id`) REFERENCES `group` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_project_creator` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目表';

-- ----------------------------------------------------------------------------
-- 5. 项目阶段表（自定义，可增删改、排序、起止时间）
-- ----------------------------------------------------------------------------
CREATE TABLE `stage` (
  `id`         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `project_id` BIGINT UNSIGNED NOT NULL COMMENT '所属项目（project.id）',
  `name`       VARCHAR(50) NOT NULL COMMENT '阶段名称',
  `order_num`  INT NOT NULL DEFAULT 0 COMMENT '阶段顺序',
  `status`     VARCHAR(16) NOT NULL DEFAULT 'pending' COMMENT '状态：pending=未开始，active=进行中，completed=已完成',
  `start_date` DATE DEFAULT NULL COMMENT '开始日期',
  `end_date`   DATE DEFAULT NULL COMMENT '截止日期',
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
  `template_name` VARCHAR(50) NOT NULL COMMENT '模板名称',
  `stage_name`    VARCHAR(50) NOT NULL COMMENT '模板阶段名',
  `order_num`     INT NOT NULL DEFAULT 0 COMMENT '阶段顺序',
  `is_public`     TINYINT NOT NULL DEFAULT 1 COMMENT '是否公共模板：1=是，0=否',
  PRIMARY KEY (`id`),
  KEY `idx_template_name` (`template_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='阶段模板表';

-- ----------------------------------------------------------------------------
-- 7. 任务表（组长发布，负责人 + 截止时间 + 优先级 + 进度）
-- ----------------------------------------------------------------------------
CREATE TABLE `task` (
  `id`                BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `project_id`        BIGINT UNSIGNED NOT NULL COMMENT '所属项目（project.id）',
  `stage_id`          BIGINT UNSIGNED DEFAULT NULL COMMENT '所属阶段（stage.id，可空）',
  `name`              VARCHAR(100) NOT NULL COMMENT '任务标题',
  `description`       TEXT         COMMENT '任务描述',
  `assignee_id`       BIGINT UNSIGNED DEFAULT NULL COMMENT '负责人（user.id，可空）',
  `deadline`          DATETIME     DEFAULT NULL COMMENT '截止时间',
  `priority`          VARCHAR(16)  NOT NULL DEFAULT 'medium' COMMENT '优先级：high=高，medium=中，low=低',
  `status`            VARCHAR(16)  NOT NULL DEFAULT 'pending' COMMENT '状态：pending=待开始，in_progress=进行中，completed=已完成',
  `progress`          INT NOT NULL DEFAULT 0 COMMENT '进度（0-100）',
  `completed_at`      DATETIME     DEFAULT NULL COMMENT '完成时间',
  `completion_remark` VARCHAR(255) DEFAULT NULL COMMENT '完成说明',
  `created_at`        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_task_project` (`project_id`),
  KEY `idx_task_stage` (`stage_id`),
  KEY `idx_task_assignee` (`assignee_id`),
  CONSTRAINT `fk_task_project` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_task_stage` FOREIGN KEY (`stage_id`) REFERENCES `stage` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_task_assignee` FOREIGN KEY (`assignee_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务表';

-- ----------------------------------------------------------------------------
-- 8. 文件资料表（审核状态机 + 版本 + 收藏 + 冗余标记）
-- ----------------------------------------------------------------------------
CREATE TABLE `file` (
  `id`             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `project_id`     BIGINT UNSIGNED NOT NULL COMMENT '所属项目（project.id）',
  `stage_id`       BIGINT UNSIGNED DEFAULT NULL COMMENT '所属阶段（stage.id，可空）',
  `name`           VARCHAR(200) NOT NULL COMMENT '文件名',
  `url`            VARCHAR(500) DEFAULT NULL COMMENT '文件URL/存储路径',
  `size`           BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '文件大小（字节）',
  `type`           VARCHAR(20)  DEFAULT NULL COMMENT '文件类型（docx/pdf/png 等）',
  `uploader_id`    BIGINT UNSIGNED NOT NULL COMMENT '上传人（user.id）',
  `status`         VARCHAR(16)  NOT NULL DEFAULT 'pending' COMMENT '审核状态：pending=待审核，approved=已通过，rejected=已驳回，archived=已归档',
  `reject_reason`  TEXT         COMMENT '驳回原因',
  `version`        INT NOT NULL DEFAULT 1 COMMENT '当前版本号',
  `is_favorite`    TINYINT NOT NULL DEFAULT 0 COMMENT '是否收藏：0=否，1=是',
  `redundant_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '冗余标记：0=正常，1=闲置，2=冗余',
  `is_latest`      TINYINT NOT NULL DEFAULT 1 COMMENT '是否最新版本：1=是，0=否',
  `parent_id`      BIGINT UNSIGNED DEFAULT NULL COMMENT '父文件ID（版本关联，file.id）',
  `deleted`        TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=正常，1=已删（保留追溯）',
  `created_at`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `updated_at`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_file_project` (`project_id`),
  KEY `idx_file_stage` (`stage_id`),
  KEY `idx_file_uploader` (`uploader_id`),
  KEY `idx_file_status` (`status`),
  CONSTRAINT `fk_file_project` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_file_stage` FOREIGN KEY (`stage_id`) REFERENCES `stage` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_file_uploader` FOREIGN KEY (`uploader_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件资料表';

-- ----------------------------------------------------------------------------
-- 9. 文件版本快照表（每次修改生成新版本）
-- ----------------------------------------------------------------------------
CREATE TABLE `file_version` (
  `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `file_id`     BIGINT UNSIGNED NOT NULL COMMENT '文件（file.id）',
  `version`     INT NOT NULL COMMENT '版本号（从 1 递增）',
  `url`         VARCHAR(500) NOT NULL COMMENT '该版本URL/路径',
  `size`        BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '该版本大小',
  `uploader_id` BIGINT UNSIGNED NOT NULL COMMENT '提交人（user.id）',
  `comment`     VARCHAR(255) DEFAULT NULL COMMENT '版本备注',
  `created_at`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '版本生成时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_file_version` (`file_id`, `version`),
  KEY `idx_fv_uploader` (`uploader_id`),
  CONSTRAINT `fk_fv_file` FOREIGN KEY (`file_id`) REFERENCES `file` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_fv_uploader` FOREIGN KEY (`uploader_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件版本快照表';

-- ----------------------------------------------------------------------------
-- 10. 文件审核记录表（通过/驳回 + 原因）
-- ----------------------------------------------------------------------------
CREATE TABLE `file_review` (
  `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `file_id`     BIGINT UNSIGNED NOT NULL COMMENT '被审文件（file.id）',
  `reviewer_id` BIGINT UNSIGNED NOT NULL COMMENT '审核人（user.id）',
  `action`      VARCHAR(16) NOT NULL COMMENT '审核动作：approved=通过，rejected=驳回',
  `reason`      VARCHAR(255) DEFAULT NULL COMMENT '原因（驳回必填）',
  `reviewed_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '审核时间',
  PRIMARY KEY (`id`),
  KEY `idx_review_file` (`file_id`),
  CONSTRAINT `fk_review_file` FOREIGN KEY (`file_id`) REFERENCES `file` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_review_reviewer` FOREIGN KEY (`reviewer_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件审核记录表';

-- ----------------------------------------------------------------------------
-- 11. 研讨记录表（线上线下研讨：议题、内容、参会人、时间）
-- ----------------------------------------------------------------------------
CREATE TABLE `discussion` (
  `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `project_id`  BIGINT UNSIGNED NOT NULL COMMENT '所属项目（project.id）',
  `topic`       VARCHAR(200) NOT NULL COMMENT '研讨主题',
  `content`     TEXT         COMMENT '研讨内容',
  `meeting_time` DATETIME    DEFAULT NULL COMMENT '会议时间',
  `attendees`   VARCHAR(500) DEFAULT NULL COMMENT '参会人ID（逗号分隔，user.id）',
  `created_by`  BIGINT UNSIGNED NOT NULL COMMENT '创建人（user.id）',
  `created_at`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_disc_project` (`project_id`),
  CONSTRAINT `fk_disc_project` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_disc_creator` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='研讨记录表';

-- ----------------------------------------------------------------------------
-- 12. 决策表（团队重要决策，可关联研讨）
-- ----------------------------------------------------------------------------
CREATE TABLE `decision` (
  `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `project_id`    BIGINT UNSIGNED NOT NULL COMMENT '所属项目（project.id）',
  `title`         VARCHAR(200) NOT NULL COMMENT '决策标题',
  `content`       TEXT         COMMENT '决策内容',
  `discussion_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '来源研讨（discussion.id，可空）',
  `creator_id`    BIGINT UNSIGNED NOT NULL COMMENT '决策人（user.id）',
  `created_at`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_decision_project` (`project_id`),
  KEY `idx_decision_disc` (`discussion_id`),
  CONSTRAINT `fk_decision_project` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_decision_disc` FOREIGN KEY (`discussion_id`) REFERENCES `discussion` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_decision_creator` FOREIGN KEY (`creator_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='决策表';

-- ----------------------------------------------------------------------------
-- 13. 成果表（阶段成果登记，关联文件）
-- ----------------------------------------------------------------------------
CREATE TABLE `achievement` (
  `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `project_id`    BIGINT UNSIGNED NOT NULL COMMENT '所属项目（project.id）',
  `stage_id`      BIGINT UNSIGNED DEFAULT NULL COMMENT '所属阶段（stage.id，可空）',
  `title`         VARCHAR(200) NOT NULL COMMENT '成果标题',
  `description`   TEXT         COMMENT '成果描述',
  `contributor_id` BIGINT UNSIGNED NOT NULL COMMENT '贡献者（user.id）',
  `created_at`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登记时间',
  PRIMARY KEY (`id`),
  KEY `idx_ach_project` (`project_id`),
  KEY `idx_ach_stage` (`stage_id`),
  CONSTRAINT `fk_ach_project` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_ach_stage` FOREIGN KEY (`stage_id`) REFERENCES `stage` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_ach_contributor` FOREIGN KEY (`contributor_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='成果表';

-- 成果关联文件表（一个成果关联多个文件）
CREATE TABLE `achievement_file` (
  `id`             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `achievement_id` BIGINT UNSIGNED NOT NULL COMMENT '成果（achievement.id）',
  `file_id`        BIGINT UNSIGNED NOT NULL COMMENT '文件（file.id）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ach_file` (`achievement_id`, `file_id`),
  CONSTRAINT `fk_achf_achievement` FOREIGN KEY (`achievement_id`) REFERENCES `achievement` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_achf_file` FOREIGN KEY (`file_id`) REFERENCES `file` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='成果关联文件表';

-- ----------------------------------------------------------------------------
-- 14. 公告表（系统公告）
-- ----------------------------------------------------------------------------
CREATE TABLE `announcement` (
  `id`         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title`      VARCHAR(200) NOT NULL COMMENT '公告标题',
  `content`    TEXT         COMMENT '公告内容',
  `level`      VARCHAR(16)  NOT NULL DEFAULT 'normal' COMMENT '重要级别：normal=普通，important=重要，urgent=紧急',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公告表';

-- 公告已读表（记录用户已读状态）
CREATE TABLE `announcement_read` (
  `id`              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `announcement_id` BIGINT UNSIGNED NOT NULL COMMENT '公告（announcement.id）',
  `user_id`         BIGINT UNSIGNED NOT NULL COMMENT '用户（user.id）',
  `read_at`         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '已读时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ann_read` (`announcement_id`, `user_id`),
  CONSTRAINT `fk_annr_ann` FOREIGN KEY (`announcement_id`) REFERENCES `announcement` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_annr_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公告已读表';

-- ----------------------------------------------------------------------------
-- 15. 分享表（结项分享，访客访问）
-- ----------------------------------------------------------------------------
CREATE TABLE `share` (
  `id`               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `project_id`       BIGINT UNSIGNED NOT NULL COMMENT '分享项目（project.id）',
  `share_token`      VARCHAR(64)  NOT NULL COMMENT '分享Token（随机唯一）',
  `share_url`        VARCHAR(255) DEFAULT NULL COMMENT '分享链接',
  `qr_code_url`      VARCHAR(255) DEFAULT NULL COMMENT '二维码URL',
  `permission_level` VARCHAR(32)  NOT NULL DEFAULT 'view_only' COMMENT '权限：view_only=仅查看，view_with_preview=可预览，view_with_download=可下载',
  `view_count`       INT NOT NULL DEFAULT 0 COMMENT '浏览次数',
  `need_password`    TINYINT NOT NULL DEFAULT 0 COMMENT '是否需密码：0=否，1=是',
  `expires_at`       DATETIME     DEFAULT NULL COMMENT '过期时间（null=永不过期）',
  `created_at`       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_share_token` (`share_token`),
  KEY `idx_share_project` (`project_id`),
  CONSTRAINT `fk_share_project` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分享表';

-- 分享访问日志表（访客浏览/下载留痕）
CREATE TABLE `share_access_log` (
  `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `share_id`    BIGINT UNSIGNED NOT NULL COMMENT '分享（share.id）',
  `visitor_id`  VARCHAR(64)  DEFAULT NULL COMMENT '访客标识（未登录用设备指纹）',
  `ip`          VARCHAR(64)  DEFAULT NULL COMMENT '访客IP',
  `device`      VARCHAR(128) DEFAULT NULL COMMENT '设备信息',
  `action`      VARCHAR(16)  NOT NULL DEFAULT 'view' COMMENT '动作：view=浏览，download=下载',
  `file_id`     BIGINT UNSIGNED DEFAULT NULL COMMENT '关联文件（file.id，可空）',
  `access_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
  PRIMARY KEY (`id`),
  KEY `idx_sharelog_share` (`share_id`),
  CONSTRAINT `fk_sharelog_share` FOREIGN KEY (`share_id`) REFERENCES `share` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分享访问日志表';

-- ----------------------------------------------------------------------------
-- 16. 消息表（阶段变更/截止预警/审核结果/新版本/研讨/公告）
-- ----------------------------------------------------------------------------
CREATE TABLE `message` (
  `id`         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id`    BIGINT UNSIGNED NOT NULL COMMENT '接收人（user.id）',
  `project_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '关联项目（project.id，可空）',
  `type`       VARCHAR(16) NOT NULL DEFAULT 'system' COMMENT '类型：system=系统，task=任务，mention=@提及，audit=审核',
  `title`      VARCHAR(200) DEFAULT NULL COMMENT '消息标题',
  `content`    VARCHAR(255) DEFAULT NULL COMMENT '消息内容',
  `is_read`    TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读：0=未读，1=已读',
  `link`       VARCHAR(255) DEFAULT NULL COMMENT '跳转链接',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_message_user` (`user_id`, `is_read`),
  KEY `idx_message_project` (`project_id`),
  CONSTRAINT `fk_message_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_message_project` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息表';

-- ----------------------------------------------------------------------------
-- 17. 消息订阅表（用户消息偏好设置，5 个开关）
-- ----------------------------------------------------------------------------
CREATE TABLE `user_subscription` (
  `id`             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id`        BIGINT UNSIGNED NOT NULL COMMENT '用户（user.id）',
  `audit_notify`   TINYINT NOT NULL DEFAULT 1 COMMENT '审核通知：1=开，0=关',
  `task_remind`    TINYINT NOT NULL DEFAULT 1 COMMENT '任务提醒：1=开，0=关',
  `member_notify`  TINYINT NOT NULL DEFAULT 1 COMMENT '成员动态：1=开，0=关',
  `stage_notify`   TINYINT NOT NULL DEFAULT 1 COMMENT '阶段变更通知：1=开，0=关',
  `mention_notify` TINYINT NOT NULL DEFAULT 1 COMMENT '@提及：1=开，0=关',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sub_user` (`user_id`),
  CONSTRAINT `fk_sub_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息订阅表';

-- ----------------------------------------------------------------------------
-- 18. 操作留痕表（全流程可视化追溯、教师过程性评价依据）
-- ----------------------------------------------------------------------------
CREATE TABLE `operation_log` (
  `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `project_id`  BIGINT UNSIGNED DEFAULT NULL COMMENT '所属项目（project.id，可空）',
  `user_id`     BIGINT UNSIGNED DEFAULT NULL COMMENT '操作人（user.id，可空）',
  `action`      VARCHAR(50) NOT NULL COMMENT '操作类型（upload/approve/reject/create_task/complete_task 等）',
  `target_type` VARCHAR(20) DEFAULT NULL COMMENT '对象类型（file/task/stage/discussion/decision 等）',
  `target_id`   BIGINT UNSIGNED DEFAULT NULL COMMENT '对象ID',
  `target_name` VARCHAR(200) DEFAULT NULL COMMENT '对象名称',
  `detail`      VARCHAR(255) DEFAULT NULL COMMENT '操作详情',
  `ip`          VARCHAR(64)  DEFAULT NULL COMMENT '操作IP',
  `created_at`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`),
  KEY `idx_log_project_time` (`project_id`, `created_at`),
  KEY `idx_log_user` (`user_id`),
  CONSTRAINT `fk_log_project` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_log_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作留痕表';

-- ----------------------------------------------------------------------------
-- 19. 期末归档表（小组/项目全量资料一键归档）
-- ----------------------------------------------------------------------------
CREATE TABLE `archive` (
  `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `project_id`   BIGINT UNSIGNED NOT NULL COMMENT '归档项目（project.id）',
  `archive_by`   BIGINT UNSIGNED NOT NULL COMMENT '归档操作人（user.id）',
  `package_path` VARCHAR(255) DEFAULT NULL COMMENT '归档包路径',
  `summary`      TEXT         COMMENT '归档说明',
  `archive_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '归档时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_archive_project` (`project_id`),
  CONSTRAINT `fk_archive_project` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_archive_by` FOREIGN KEY (`archive_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='期末归档表';
