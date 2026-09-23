package com.rocgit.knife4j.dto.discussion;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 更新研讨请求
 */
@Data
@Schema(description = "更新研讨请求")
public class UpdateDiscussionRequest {

    @Schema(description = "新主题")
    private String topic;

    @Schema(description = "新内容")
    private String content;

    @Schema(description = "新会议时间")
    private LocalDateTime meetingTime;

    @Schema(description = "新参会人员ID列表")
    private List<Long> attendees;
}
