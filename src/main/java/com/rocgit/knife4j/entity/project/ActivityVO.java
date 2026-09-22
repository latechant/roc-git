package com.rocgit.knife4j.entity.project;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 项目活动/动态
 */
@Data
@Schema(description = "项目动态")
public class ActivityVO {

    @Schema(description = "动态ID")
    private Long id;

    @Schema(description = "动态类型", example = "file",
            allowableValues = {"file", "task", "stage", "member"})
    private String type;

    @Schema(description = "操作动作", example = "上传了文件")
    private String action;

    @Schema(description = "操作用户ID")
    private Long userId;

    @Schema(description = "操作用户名")
    private String userName;

    @Schema(description = "目标ID")
    private Long targetId;

    @Schema(description = "目标名称", example = "需求文档.docx")
    private String targetName;

    @Schema(description = "发生时间")
    private String createdAt;
}
