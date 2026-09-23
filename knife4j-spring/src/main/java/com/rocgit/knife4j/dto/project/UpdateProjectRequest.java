package com.rocgit.knife4j.dto.project;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 更新项目请求
 */
@Data
@Schema(description = "更新项目请求")
public class UpdateProjectRequest {

    @Schema(description = "新项目名称")
    private String name;

    @Schema(description = "新项目描述")
    private String description;

    @Schema(description = "审核规则")
    private Object auditRule;

    @Schema(description = "提醒规则")
    private Object reminderRule;
}
