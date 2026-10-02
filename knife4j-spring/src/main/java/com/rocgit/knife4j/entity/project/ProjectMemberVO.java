package com.rocgit.knife4j.entity.project;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 项目成员 VO
 */
@Data
@Schema(description = "项目成员")
public class ProjectMemberVO {

    @Schema(description = "用户ID", example = "10001")
    private Long userId;

    @Schema(description = "昵称", example = "张三")
    private String nickname;

    @Schema(description = "角色：leader=组长，member=组员，teacher=教师", example = "member")
    private String role;

    @Schema(description = "加入时间")
    private LocalDateTime joinedAt;
}
