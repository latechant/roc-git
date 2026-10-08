package com.rocgit.knife4j.entity.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务详情 VO
 */
@Data
@Schema(description = "任务详情")
public class TaskVO {

    @Schema(description = "任务ID")
    private Long taskId;

    @Schema(description = "所属项目ID")
    private Long projectId;

    @Schema(description = "所属阶段ID")
    private Long stageId;

    @Schema(description = "项目名称")
    private String projectName;

    @Schema(description = "任务名称")
    private String name;

    @Schema(description = "任务描述")
    private String description;

    @Schema(description = "负责人ID")
    private Long assigneeId;

    @Schema(description = "负责人昵称")
    private String assigneeName;

    @Schema(description = "截止时间")
    private LocalDateTime deadline;

    @Schema(description = "优先级", allowableValues = {"high", "normal", "low"})
    private String priority;

    @Schema(description = "任务状态", allowableValues = {"pending", "in_progress", "completed", "abandoned"})
    private String status;

    @Schema(description = "进度 0-100")
    private Integer progress;

    @Schema(description = "关联文件列表")
    private List<RelatedFile> relatedFiles;

    @Schema(description = "完成时间")
    private LocalDateTime completedAt;

    @Schema(description = "完成说明")
    private String completionRemark;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Data
    @Schema(description = "关联文件")
    public static class RelatedFile {
        private Long fileId;
        private String name;
    }
}
