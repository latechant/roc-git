package com.rocgit.knife4j.entity.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 订阅设置
 */
@Data
@Schema(description = "订阅设置")
public class SubscriptionVO {

    @Schema(description = "审核通知")
    private Boolean auditNotify;

    @Schema(description = "任务提醒")
    private Boolean taskRemind;

    @Schema(description = "成员动态")
    private Boolean memberNotify;

    @Schema(description = "阶段变更通知")
    private Boolean stageNotify;

    @Schema(description = "@提及")
    private Boolean mentionNotify;
}
