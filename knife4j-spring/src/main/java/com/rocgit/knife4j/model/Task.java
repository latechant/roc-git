package com.rocgit.knife4j.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务表
 */
@Data
@TableName("task")
public class Task {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long projectId;

    private Long stageId;

    private String name;

    private String description;

    private Long assigneeId;

    private LocalDateTime deadline;

    private String priority;

    private String status;

    private Integer progress;

    private LocalDateTime completedAt;

    private String completionRemark;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
