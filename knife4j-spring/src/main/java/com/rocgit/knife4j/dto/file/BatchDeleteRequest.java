package com.rocgit.knife4j.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 批量删除文件请求
 */
@Data
@Schema(description = "批量删除文件请求")
public class BatchDeleteRequest {

    @NotEmpty
    @Size(max = 20, message = "单次最多删除 20 个文件")
    @Schema(description = "文件ID列表（≤20）",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> fileIds;

    @Schema(description = "删除原因", example = "已更新版本")
    private String reason;
}
