package com.rocgit.knife4j.dto.achievement;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 更新成果请求
 */
@Data
@Schema(description = "更新成果请求")
public class UpdateAchievementRequest {

    @Schema(description = "新标题")
    private String title;

    @Schema(description = "新描述")
    private String description;

    @Schema(description = "新关联文件ID列表")
    private List<Long> relatedFileIds;
}
