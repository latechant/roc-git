package com.rocgit.knife4j.entity.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件审核日志
 */
@Data
@Schema(description = "审核日志")
public class AuditLogVO {

    @Schema(description = "日志ID")
    private Long logId;

    @Schema(description = "动作", example = "approve",
            allowableValues = {"approve", "reject", "resubmit", "move"})
    private String action;

    @Schema(description = "驳回原因")
    private String reason;

    @Schema(description = "审核人")
    private AuditorInfo auditor;

    @Schema(description = "审核时间")
    private LocalDateTime createdAt;

    @Data
    @Schema(description = "审核人信息")
    public static class AuditorInfo {
        private Long userId;
        private String nickname;
        private String avatarUrl;
    }
}
