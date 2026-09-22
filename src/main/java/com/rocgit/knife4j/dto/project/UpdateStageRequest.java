package com.rocgit.knife4j.dto.project;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 更新阶段请求
 */
@Data
@Schema(description = "更新阶段请求")
public class UpdateStageRequest {

    @Schema(description = "新阶段名")
    private String name;

    @Schema(description = "新顺序")
    private Integer orderNum;

    @Schema(description = "新开始日期")
    private LocalDate startDate;

    @Schema(description = "新结束日期")
    private LocalDate endDate;
}
