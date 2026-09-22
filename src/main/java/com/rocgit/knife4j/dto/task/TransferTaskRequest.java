package com.rocgit.knife4j.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 转移任务请求
 */
@Data
@Schema(description = "转移任务请求")
public class TransferTaskRequest {

    @NotNull
    @Schema(description = "新负责人用户ID", example = "10002",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Long newAssigneeId;

    @Schema(description = "转移说明")
    private String remark;
}
