package com.rocgit.knife4j.entity.group;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 小组响应 VO
 */
@Data
@Schema(description = "小组信息")
public class GroupVO {

    @Schema(description = "小组ID", example = "1")
    private Long groupId;

    @Schema(description = "小组名称", example = "课程设计第十二组")
    private String name;

    @Schema(description = "小组描述")
    private String description;

    @Schema(description = "组长ID", example = "10001")
    private Long leaderId;

    @Schema(description = "组长昵称", example = "张三")
    private String leaderName;

    @Schema(description = "成员数量", example = "5")
    private Integer memberCount;

    @Schema(description = "项目数量", example = "3")
    private Integer projectCount;

    @Schema(description = "邀请码", example = "ABC123")
    private String inviteCode;

    @Schema(description = "二维码URL")
    private String qrCodeUrl;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
