package com.rocgit.knife4j.entity.decision;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 决策详情
 */
@Data
@Schema(description = "决策详情")
public class DecisionVO {

    @Schema(description = "决策ID")
    private Long decisionId;

    @Schema(description = "所属项目ID")
    private Long projectId;

    @Schema(description = "决策标题")
    private String title;

    @Schema(description = "决策内容")
    private String content;

    @Schema(description = "来源研讨ID")
    private Long discussionId;

    @Schema(description = "关联研讨主题")
    private String relatedDiscussionTopic;

    @Schema(description = "决策人ID")
    private Long creatorId;

    @Schema(description = "决策人昵称")
    private String creatorName;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
