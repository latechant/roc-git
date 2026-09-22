package com.rocgit.knife4j.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 微信登录请求
 */
@Data
@Schema(description = "微信登录请求")
public class LoginRequest {

    @NotBlank(message = "code 不能为空")
    @Schema(description = "微信登录 code", example = "081C4t0000xxxxx",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @NotBlank(message = "昵称不能为空")
    @Schema(description = "用户昵称", example = "张三",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String nickname;

    @NotBlank(message = "头像URL不能为空")
    @Schema(description = "用户头像URL", example = "https://thirdwx.qlogo.cn/xxx",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String avatarUrl;
}
