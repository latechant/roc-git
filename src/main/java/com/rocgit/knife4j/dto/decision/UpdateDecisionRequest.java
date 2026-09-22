package com.rocgit.knife4j.dto.decision;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 更新决策请求
 */
@Data
@Schema(description = "更新决策请求")
public class UpdateDecisionRequest {

    @Schema(description = "新标题")
    private String title;

    @Schema(description = "新内容")
    private String content;
}
