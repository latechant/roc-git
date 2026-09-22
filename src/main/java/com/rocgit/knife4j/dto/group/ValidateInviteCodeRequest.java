package com.rocgit.knife4j.dto.group;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 校验邀请码请求
 */
@Data
@Schema(description = "校验邀请码请求")
public class ValidateInviteCodeRequest {

    @NotBlank
    @Schema(description = "邀请码", example = "ABC123",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String inviteCode;
}
