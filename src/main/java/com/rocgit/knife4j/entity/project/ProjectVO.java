package com.rocgit.knife4j.entity.project;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 项目 VO（精简）
 */
@Data
@Schema(description = "项目信息")
public class ProjectVO {

    @Schema(description = "项目ID")
    private Long projectId;

    @Schema(description = "所属小组ID")
    private Long groupId;

    @Schema(description = "项目名称")
    private String name;

    @Schema(description = "项目描述")
    private String description;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "进度 0-100")
    private Integer progress;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
