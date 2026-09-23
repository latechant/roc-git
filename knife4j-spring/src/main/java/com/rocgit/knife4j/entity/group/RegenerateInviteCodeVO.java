package com.rocgit.knife4j.entity.group;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 重新生成邀请码响应
 */
@Data
@Schema(description = "重新生成邀请码响应")
public class RegenerateInviteCodeVO {

    @Schema(description = "新邀请码", example = "XYZ789")
    private String inviteCode;

    @Schema(description = "新二维码URL")
    private String qrCodeUrl;

    @Schema(description = "过期时间")
    private LocalDateTime expiresAt;
}
