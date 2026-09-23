package com.rocgit.knife4j.entity.log;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志条目
 */
@Data
@Schema(description = "操作日志")
public class OperationLogVO {

    @Schema(description = "日志ID")
    private Long logId;

    @Schema(description = "动作类型", example = "file_upload")
    private String action;

    @Schema(description = "操作用户ID")
    private Long userId;

    @Schema(description = "操作用户名")
    private String userName;

    @Schema(description = "目标类型", example = "file")
    private String targetType;

    @Schema(description = "目标ID")
    private Long targetId;

    @Schema(description = "目标名称")
    private String targetName;

    @Schema(description = "动作详情")
    private String detail;

    @Schema(description = "操作时间")
    private LocalDateTime createdAt;

    @Schema(description = "客户端IP")
    private String ip;
}
