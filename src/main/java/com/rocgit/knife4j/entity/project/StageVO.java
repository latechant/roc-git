package com.rocgit.knife4j.entity.project;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 项目阶段 VO
 */
@Data
@Schema(description = "项目阶段")
public class StageVO {

    @Schema(description = "阶段ID", example = "1")
    private Long stageId;

    @Schema(description = "阶段名称", example = "需求分析")
    private String name;

    @Schema(description = "顺序号", example = "1")
    private Integer orderNum;

    @Schema(description = "状态", example = "in_progress",
            allowableValues = {"pending", "in_progress", "completed"})
    private String status;

    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;

    @Schema(description = "文件数")
    private Integer fileCount;

    @Schema(description = "任务数")
    private Integer taskCount;

    @Schema(description = "完成任务数")
    private Integer completedTaskCount;
}
