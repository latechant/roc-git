package com.rocgit.knife4j.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 阶段模板表
 */
@Data
@TableName("stage_template")
public class StageTemplate {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String templateName;

    private String stageName;

    private Integer orderNum;

    private Integer isPublic;
}
