package com.rocgit.knife4j.dto.project;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 调整阶段顺序请求
 */
@Data
@Schema(description = "调整阶段顺序请求")
public class StageOrderRequest {

    @Schema(description = "阶段ID按新顺序排列", example = "[3, 1, 2, 4, 5]",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> stageIds;
}
