package com.rocgit.knife4j.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分享表
 */
@Data
@TableName("share")
public class Share {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long projectId;

    private String shareToken;

    private String shareUrl;

    private String qrCodeUrl;

    private String permissionLevel;

    private Integer viewCount;

    private Integer needPassword;

    private LocalDateTime expiresAt;

    private LocalDateTime createdAt;
}
