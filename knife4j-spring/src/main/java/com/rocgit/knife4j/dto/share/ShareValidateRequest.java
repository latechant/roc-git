package com.rocgit.knife4j.dto.share;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 校验分享令牌请求
 */
@Data
@Schema(description = "校验分享令牌请求")
public class ShareValidateRequest {

    @NotBlank
    @Schema(description = "分享令牌",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String shareToken;

    @Schema(description = "访问密码（如设置）")
    private String password;
}
