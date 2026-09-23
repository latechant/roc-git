package com.rocgit.knife4j.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件版本快照表
 */
@Data
@TableName("file_version")
public class FileVersion {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long fileId;

    private Integer version;

    private String url;

    private Long size;

    private Long uploaderId;

    private String comment;

    private LocalDateTime createdAt;
}
