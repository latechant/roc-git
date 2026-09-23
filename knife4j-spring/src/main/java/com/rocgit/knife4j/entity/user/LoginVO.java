package com.rocgit.knife4j.entity.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录响应数据
 */
@Data
@Schema(description = "登录响应数据")
public class LoginVO {

    @Schema(description = "JWT Token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    @Schema(description = "用户ID", example = "10001")
    private Long userId;

    @Schema(description = "微信 openid", example = "oUpF8uMuAJ...")
    private String openid;

    @Schema(description = "昵称", example = "张三")
    private String nickname;

    @Schema(description = "头像URL")
    private String avatarUrl;

    @Schema(description = "是否新用户（首次登录）")
    private Boolean isNewUser;
}
