package com.rocgit.knife4j.entity.achievement;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 成果详情
 */
@Data
@Schema(description = "项目成果")
public class AchievementVO {

    @Schema(description = "成果ID")
    private Long achievementId;

    @Schema(description = "所属项目ID")
    private Long projectId;

    @Schema(description = "所属阶段ID")
    private Long stageId;

    @Schema(description = "所属阶段名称")
    private String stageName;

    @Schema(description = "成果标题")
    private String title;

    @Schema(description = "成果描述")
    private String description;

    @Schema(description = "贡献者")
    private Long contributorId;

    @Schema(description = "贡献者昵称")
    private String contributorName;

    @Schema(description = "关联文件")
    private List<RelatedFile> relatedFiles;

    @Schema(description = "登记时间")
    private LocalDateTime createdAt;

    @Data
    @Schema(description = "关联文件")
    public static class RelatedFile {
        private Long fileId;
        private String name;
    }
}
