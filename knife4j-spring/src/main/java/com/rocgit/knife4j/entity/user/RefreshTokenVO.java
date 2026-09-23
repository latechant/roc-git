package com.rocgit.knife4j.entity.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 刷新 Token 响应
 */
@Data
@Schema(description = "刷新Token响应")
public class RefreshTokenVO {

    @Schema(description = "新 JWT Token")
    private String token;

    @Schema(description = "有效期（秒）", example = "7200")
    private Integer expiresIn;
}
