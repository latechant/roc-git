package com.rocgit.knife4j.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件审核记录表
 */
@Data
@TableName("file_review")
public class FileReview {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long fileId;

    private Long reviewerId;

    private String action;

    private String reason;

    private LocalDateTime reviewedAt;
}
