package com.rocgit.knife4j.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 研讨记录表
 */
@Data
@TableName("discussion")
public class Discussion {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long projectId;

    private String topic;

    private String content;

    private LocalDateTime meetingTime;

    private String attendees;

    private Long createdBy;

    private LocalDateTime createdAt;
}
