package com.rocgit.knife4j.dto.share;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * 更新分享权限请求
 */
@Data
@Schema(description = "更新分享权限请求")
public class ShareUpdateRequest {

    @NotBlank
    @Schema(description = "新权限级别", allowableValues = {"flow_only", "flow_preview", "flow_download"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String permissionLevel;

    @Schema(description = "新过期时间")
    private LocalDateTime expiresAt;
}
