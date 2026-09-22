package com.rocgit.knife4j.entity.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 未读消息统计
 */
@Data
@Schema(description = "未读消息统计")
public class UnreadCountVO {

    @Schema(description = "未读总数")
    private Integer total;

    @Schema(description = "系统消息数")
    private Integer system;

    @Schema(description = "任务消息数")
    private Integer task;

    @Schema(description = "@提及数")
    private Integer mention;

    @Schema(description = "审核消息数")
    private Integer audit;
}
