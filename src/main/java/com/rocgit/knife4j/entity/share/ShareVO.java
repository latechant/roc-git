package com.rocgit.knife4j.entity.share;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分享响应数据
 */
@Data
@Schema(description = "分享信息")
public class ShareVO {

    @Schema(description = "分享ID")
    private Long shareId;

    @Schema(description = "分享Token")
    private String shareToken;

    @Schema(description = "分享链接")
    private String shareUrl;

    @Schema(description = "二维码URL")
    private String qrCodeUrl;

    @Schema(description = "权限级别")
    private String permissionLevel;

    @Schema(description = "浏览次数")
    private Integer viewCount;

    @Schema(description = "过期时间")
    private LocalDateTime expiresAt;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "是否需要密码")
    private Boolean needPassword;
}
