package com.rocgit.knife4j.entity.group;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 校验邀请码响应
 */
@Data
@Schema(description = "校验邀请码响应")
public class ValidateInviteCodeVO {

    @Schema(description = "邀请码是否有效")
    private Boolean valid;

    @Schema(description = "小组名称")
    private String groupName;

    @Schema(description = "成员数量")
    private Integer memberCount;

    @Schema(description = "组长昵称")
    private String leaderName;
}
