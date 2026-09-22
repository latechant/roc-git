package com.rocgit.knife4j.dto.group;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 加入小组请求
 */
@Data
@Schema(description = "加入小组请求")
public class JoinGroupRequest {

    @NotBlank(message = "邀请码不能为空")
    @Schema(description = "6 位邀请码", example = "ABC123",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String inviteCode;
}
