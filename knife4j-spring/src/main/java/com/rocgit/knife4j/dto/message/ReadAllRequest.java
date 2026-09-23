package com.rocgit.knife4j.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 全部已读请求
 */
@Data
@Schema(description = "全部已读请求")
public class ReadAllRequest {

    @NotNull
    @Schema(description = "消息类型，全部已读传 'all'",
            example = "all",
            allowableValues = {"all", "system", "task", "mention", "audit"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String type;
}
