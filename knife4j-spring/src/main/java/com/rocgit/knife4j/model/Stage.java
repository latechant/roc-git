package com.rocgit.knife4j.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 项目阶段表
 */
@Data
@TableName("stage")
public class Stage {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long projectId;

    private String name;

    private Integer orderNum;

    private String status;

    private LocalDate startDate;

    private LocalDate endDate;

    private String remark;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
