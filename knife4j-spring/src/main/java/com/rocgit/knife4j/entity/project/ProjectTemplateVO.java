package com.rocgit.knife4j.entity.project;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 项目模板 VO
 */
@Data
@Schema(description = "项目模板")
public class ProjectTemplateVO {

    @Schema(description = "模板ID")
    private Long templateId;

    @Schema(description = "模板名称", example = "标准课程设计模板")
    private String name;

    @Schema(description = "模板描述")
    private String description;

    @Schema(description = "阶段数量", example = "5")
    private Integer stageCount;

    @Schema(description = "是否公开")
    private Boolean isPublic;
}
