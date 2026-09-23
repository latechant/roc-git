package com.rocgit.knife4j.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分享访问日志表
 */
@Data
@TableName("share_access_log")
public class ShareAccessLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long shareId;

    private String visitorId;

    private String ip;

    private String device;

    private String action;

    private Long fileId;

    private LocalDateTime accessTime;
}
