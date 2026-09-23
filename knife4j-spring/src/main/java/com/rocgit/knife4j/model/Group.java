package com.rocgit.knife4j.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 小组表
 */
@Data
@TableName("`group`")
public class Group {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String description;

    private Long leaderId;

    private String inviteCode;

    private String qrCodeUrl;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
