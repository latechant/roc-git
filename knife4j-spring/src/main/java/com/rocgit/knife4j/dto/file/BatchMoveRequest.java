package com.rocgit.knife4j.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 批量移动文件请求
 */
@Data
@Schema(description = "批量移动文件请求")
public class BatchMoveRequest {

    @NotEmpty
    @Size(max = 50, message = "单次最多移动 50 个文件")
    @Schema(description = "文件ID列表（≤50）",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> fileIds;

    @NotNull
    @Schema(description = "目标阶段ID",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Long targetStageId;
}
