package com.rocgit.knife4j.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 更新任务请求
 */
@Data
@Schema(description = "更新任务请求")
public class UpdateTaskRequest {

    @Schema(description = "新任务名称")
    private String name;

    @Schema(description = "新任务描述")
    private String description;

    @Schema(description = "新负责人ID")
    private Long assigneeId;

    @Schema(description = "新截止时间")
    private LocalDateTime deadline;

    @Schema(description = "新优先级", allowableValues = {"high", "normal", "low"})
    private String priority;
}
