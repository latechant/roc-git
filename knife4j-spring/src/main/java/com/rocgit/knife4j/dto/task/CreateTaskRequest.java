package com.rocgit.knife4j.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 创建任务请求
 */
@Data
@Schema(description = "创建任务请求")
public class CreateTaskRequest {

    @NotNull
    @Schema(description = "所属项目ID", example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Long projectId;

    @Schema(description = "所属阶段ID（可空，未分组任务不传）", example = "1")
    private Long stageId;

    @NotBlank
    @Schema(description = "任务名称", example = "完成登录模块",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "任务描述", example = "实现微信扫码登录功能")
    private String description;

    @NotNull
    @Schema(description = "负责人用户ID", example = "10001",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Long assigneeId;

    @NotNull
    @Schema(description = "截止时间",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime deadline;

    @Schema(description = "优先级", example = "high",
            allowableValues = {"high", "normal", "low"})
    private String priority;

    @Schema(description = "关联文件ID列表")
    private List<Long> relatedFileIds;
}
