package com.rocgit.knife4j.dto.project;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 创建项目模板请求
 */
@Data
@Schema(description = "创建项目模板请求")
public class CreateTemplateRequest {

    @NotBlank(message = "模板名称不能为空")
    @Schema(description = "模板名称", example = "标准课程设计模板",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "模板描述")
    private String description;

    @Schema(description = "阶段配置",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<StageItem> stages;

    @Schema(description = "是否公开模板")
    private Boolean isPublic;

    @Data
    @Schema(description = "阶段项")
    public static class StageItem {
        @Schema(description = "阶段名")
        private String name;

        @Schema(description = "顺序号")
        private Integer orderNum;
    }
}
