package com.rocgit.knife4j.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 项目表
 */
@Data
@TableName("project")
public class Project {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long groupId;

    private String name;

    private String description;

    private String status;

    private Integer progress;

    private Long currentStageId;

    private Long createdBy;

    private Integer isShared;

    private String shareToken;

    private String auditRule;

    private String reminderRule;

    private LocalDateTime archivedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
