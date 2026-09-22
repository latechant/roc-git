package com.rocgit.knife4j.entity.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户贡献统计
 */
@Data
@Schema(description = "用户贡献统计")
public class ContributionStatsVO {

    @Schema(description = "上传文件数", example = "28")
    private Integer uploadedFiles;

    @Schema(description = "审核通过文件数", example = "25")
    private Integer approvedFiles;

    @Schema(description = "完成任务数", example = "12")
    private Integer completedTasks;

    @Schema(description = "发起研讨数", example = "8")
    private Integer discussions;

    @Schema(description = "加入天数", example = "365")
    private Integer joinDays;

    @Schema(description = "最后活跃时间")
    private LocalDateTime lastActiveTime;
}
