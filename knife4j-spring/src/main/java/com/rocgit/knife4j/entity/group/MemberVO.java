package com.rocgit.knife4j.entity.group;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 小组成员 VO
 */
@Data
@Schema(description = "小组成员")
public class MemberVO {

    @Schema(description = "用户ID", example = "10001")
    private Long userId;

    @Schema(description = "昵称", example = "张三")
    private String nickname;

    @Schema(description = "头像URL")
    private String avatarUrl;

    @Schema(description = "角色", example = "leader",
            allowableValues = {"leader", "member"})
    private String role;

    @Schema(description = "加入时间")
    private LocalDateTime joinedAt;

    @Schema(description = "状态", example = "active",
            allowableValues = {"active", "inactive"})
    private String status;

    @Schema(description = "备注名（组长设置）", example = "前端大佬")
    private String remark;
}
