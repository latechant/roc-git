package com.rocgit.knife4j.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 期末归档表
 */
@Data
@TableName("archive")
public class Archive {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long projectId;

    private Long archiveBy;

    private String packagePath;

    private String summary;

    private LocalDateTime archiveTime;
}
