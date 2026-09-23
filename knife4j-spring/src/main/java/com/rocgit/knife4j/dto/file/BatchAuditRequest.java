package com.rocgit.knife4j.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 批量审核请求
 */
@Data
@Schema(description = "批量审核请求")
public class BatchAuditRequest {

    @NotEmpty
    @Schema(description = "待审核文件ID列表",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> fileIds;

    @Schema(description = "动作", example = "approve",
            allowableValues = {"approve", "reject"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String action;

    @Schema(description = "驳回原因（仅 reject 时需要）")
    private String reason;

    @Schema(description = "审核人备注")
    private String remark;
}
