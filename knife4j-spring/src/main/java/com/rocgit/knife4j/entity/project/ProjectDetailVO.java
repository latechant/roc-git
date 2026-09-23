package com.rocgit.knife4j.entity.project;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 项目详情 VO
 */
@Data
@Schema(description = "项目详情")
public class ProjectDetailVO {

    @Schema(description = "项目ID")
    private Long projectId;

    @Schema(description = "所属小组ID")
    private Long groupId;

    @Schema(description = "项目名称")
    private String name;

    @Schema(description = "项目描述")
    private String description;

    @Schema(description = "当前阶段名")
    private String currentStage;

    @Schema(description = "整体进度 0-100")
    private Integer progress;

    @Schema(description = "状态", allowableValues = {"active", "completed", "archived"})
    private String status;

    @Schema(description = "项目阶段列表")
    private List<StageVO> stages;

    @Schema(description = "成员数量")
    private Integer memberCount;

    @Schema(description = "文件数量")
    private Integer fileCount;

    @Schema(description = "任务总数")
    private Integer taskCount;

    @Schema(description = "已完成任务数")
    private Integer completedTaskCount;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
