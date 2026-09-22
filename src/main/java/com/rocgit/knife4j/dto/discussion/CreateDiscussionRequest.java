package com.rocgit.knife4j.dto.discussion;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 创建研讨记录请求
 */
@Data
@Schema(description = "创建研讨记录请求")
public class CreateDiscussionRequest {

    @NotNull
    @Schema(description = "所属项目ID",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Long projectId;

    @NotBlank
    @Schema(description = "研讨主题", example = "需求评审",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String topic;

    @NotBlank
    @Schema(description = "研讨内容",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @NotNull
    @Schema(description = "会议时间",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime meetingTime;

    @Schema(description = "参会人员ID列表")
    private List<Long> attendees;
}
