package com.rocgit.knife4j.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 导出文件清单请求
 */
@Data
@Schema(description = "导出文件清单请求")
public class ExportFileListRequest {

    @Schema(description = "导出格式", example = "xlsx",
            allowableValues = {"xlsx", "csv"},
            defaultValue = "xlsx")
    private String format = "xlsx";

    @Schema(description = "限定阶段ID（可选）")
    private Long stageId;

    @Schema(description = "是否包含版本历史")
    private Boolean includeVersions;
}
