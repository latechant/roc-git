package com.rocgit.knife4j.dto.share;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 创建分享链接请求
 */
@Data
@Schema(description = "创建分享链接请求")
public class ShareCreateRequest {

    @NotNull
    @Schema(description = "项目ID",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Long projectId;

    @NotBlank
    @Schema(description = "权限级别",
            example = "flow_only",
            allowableValues = {"flow_only", "flow_preview", "flow_download"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String permissionLevel;

    @Schema(description = "过期时间（不填则永久）")
    private LocalDateTime expiresAt;

    @Schema(description = "是否需要密码")
    private Boolean needPassword;

    @Schema(description = "访问密码")
    private String password;
}
