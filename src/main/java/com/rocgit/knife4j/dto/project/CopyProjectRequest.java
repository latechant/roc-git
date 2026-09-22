package com.rocgit.knife4j.dto.project;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 复制项目请求
 */
@Data
@Schema(description = "复制项目请求")
public class CopyProjectRequest {

    @NotBlank(message = "新项目名不能为空")
    @Schema(description = "新项目名称", example = "课程设计项目 - 副本",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "是否包含文件", example = "false")
    private Boolean includeFiles;

    @Schema(description = "是否包含任务", example = "false")
    private Boolean includeTasks;
}
