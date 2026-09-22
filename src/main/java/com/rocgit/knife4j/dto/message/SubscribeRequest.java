package com.rocgit.knife4j.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 订阅设置请求
 */
@Data
@Schema(description = "订阅设置请求")
public class SubscribeRequest {

    @Schema(description = "类型", example = "audit",
            allowableValues = {"audit", "task", "member", "stage", "mention"})
    private String type;

    @Schema(description = "是否订阅")
    private Boolean enabled;

    @Schema(description = "订阅渠道")
    private String channel;
}
