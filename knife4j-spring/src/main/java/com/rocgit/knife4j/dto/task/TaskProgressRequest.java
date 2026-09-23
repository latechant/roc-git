package com.rocgit.knife4j.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 更新任务进度请求
 */
@Data
@Schema(description = "更新任务进度请求")
public class TaskProgressRequest {

    @NotNull
    @Min(0) @Max(100)
    @Schema(description = "进度 0-100", example = "75",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer progress;

    @Schema(description = "进度说明")
    private String remark;
}
