package com.rocgit.knife4j.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 移动文件请求
 */
@Data
@Schema(description = "移动文件请求")
public class MoveFileRequest {

    @NotNull(message = "目标阶段ID不能为空")
    @Schema(description = "目标阶段ID", example = "3",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Long targetStageId;
}
