package com.rocgit.knife4j.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件资料表
 */
@Data
@TableName("file")
public class File {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long projectId;

    private Long stageId;

    private String name;

    private String url;

    private Long size;

    private String type;

    private Long uploaderId;

    private String status;

    private String rejectReason;

    private Integer version;

    private Integer isFavorite;

    private Integer redundantFlag;

    private Integer isLatest;

    private Long parentId;

    private Integer deleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
