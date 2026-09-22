package com.rocgit.knife4j.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 审核驳回请求
 */
@Data
@Schema(description = "审核驳回请求")
public class RejectFileRequest {

    @NotBlank
    @Size(min = 5, max = 200, message = "驳回原因需在 5-200 字")
    @Schema(description = "驳回原因", example = "缺少第三章节的内容",
            minLength = 5, maxLength = 200,
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String reason;
}
