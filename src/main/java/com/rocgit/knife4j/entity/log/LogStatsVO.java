package com.rocgit.knife4j.entity.log;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 项目日志统计
 */
@Data
@Schema(description = "项目日志统计")
public class LogStatsVO {

    @Schema(description = "日志总数")
    private Long totalLogs;

    @Schema(description = "今日日志数")
    private Integer todayLogs;

    @Schema(description = "活跃用户数")
    private Integer activeUsers;

    @Schema(description = "文件操作数")
    private Integer fileOperations;

    @Schema(description = "任务操作数")
    private Integer taskOperations;
}
