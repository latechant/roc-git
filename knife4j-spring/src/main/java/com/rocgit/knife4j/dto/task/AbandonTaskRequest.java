package com.rocgit.knife4j.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Size;

/**
 * 放弃任务请求
 */
@Data
@Schema(description = "放弃任务请求")
public class AbandonTaskRequest {

    @Size(max = 200)
    @Schema(description = "放弃原因", example = "工作量超出预期")
    private String reason;
}
