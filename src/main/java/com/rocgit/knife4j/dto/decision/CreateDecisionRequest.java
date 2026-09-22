package com.rocgit.knife4j.dto.decision;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 创建决策请求
 */
@Data
@Schema(description = "创建决策请求")
public class CreateDecisionRequest {

    @NotNull
    @Schema(description = "所属项目ID",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Long projectId;

    @NotBlank
    @Schema(description = "决策标题",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @NotBlank
    @Schema(description = "决策详细内容",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @Schema(description = "来源研讨记录ID（可选）")
    private Long discussionId;
}
