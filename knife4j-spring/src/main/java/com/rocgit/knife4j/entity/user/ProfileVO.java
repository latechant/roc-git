package com.rocgit.knife4j.entity.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户个人信息 VO
 */
@Data
@Schema(description = "个人信息")
public class ProfileVO {

    @Schema(description = "用户ID", example = "10001")
    private Long userId;

    @Schema(description = "微信 openid")
    private String openid;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像URL")
    private String avatarUrl;

    @Schema(description = "注册时间")
    private LocalDateTime createdAt;

    @Schema(description = "加入的小组数")
    private Integer groupCount;

    @Schema(description = "参与的项目数")
    private Integer projectCount;
}
