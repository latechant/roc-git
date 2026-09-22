package com.rocgit.knife4j.entity.share;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分享访问日志
 */
@Data
@Schema(description = "访问日志")
public class AccessLogVO {

    @Schema(description = "访客ID（匿名）")
    private String visitorId;

    @Schema(description = "访问时间")
    private LocalDateTime accessTime;

    @Schema(description = "访客IP")
    private String ip;

    @Schema(description = "访问设备", example = "Chrome/Windows")
    private String device;

    @Schema(description = "访问动作", example = "view")
    private String action;

    @Schema(description = "访问的文件ID")
    private Long fileId;
}
