package com.rocgit.knife4j.dto.group;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 转让组长请求
 */
@Data
@Schema(description = "转让组长请求")
public class TransferLeaderRequest {

    @NotNull
    @Schema(description = "新组长用户ID", example = "10002",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Long newLeaderId;
}
