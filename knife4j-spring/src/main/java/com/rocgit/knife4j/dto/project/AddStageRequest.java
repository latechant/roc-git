package com.rocgit.knife4j.dto.project;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

/**
 * 新增项目阶段请求
 */
@Data
@Schema(description = "新增项目阶段请求")
public class AddStageRequest {

    @NotBlank(message = "阶段名称不能为空")
    @Schema(description = "阶段名称", example = "测试阶段",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "顺序号", example = "5")
    private Integer orderNum;

    @Schema(description = "开始日期", example = "2026-10-01")
    private LocalDate startDate;

    @Schema(description = "结束日期", example = "2026-10-15")
    private LocalDate endDate;
}
