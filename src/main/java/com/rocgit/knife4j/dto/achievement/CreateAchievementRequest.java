package com.rocgit.knife4j.dto.achievement;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 创建成果请求
 */
@Data
@Schema(description = "创建成果请求")
public class CreateAchievementRequest {

    @NotNull
    @Schema(description = "所属项目ID",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Long projectId;

    @NotNull
    @Schema(description = "关联阶段ID",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Long stageId;

    @NotBlank
    @Schema(description = "成果标题",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @NotBlank
    @Schema(description = "成果描述",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String description;

    @Schema(description = "关联文件ID列表")
    private List<Long> relatedFileIds;
}
