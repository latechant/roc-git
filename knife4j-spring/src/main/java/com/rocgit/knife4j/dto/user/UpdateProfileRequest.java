package com.rocgit.knife4j.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 更新个人信息请求
 */
@Data
@Schema(description = "更新个人信息请求")
public class UpdateProfileRequest {

    @Schema(description = "新昵称", example = "新昵称")
    private String nickname;

    @Schema(description = "新头像URL", example = "https://cdn.rocgit.com/avatar/new.jpg")
    private String avatarUrl;
}
