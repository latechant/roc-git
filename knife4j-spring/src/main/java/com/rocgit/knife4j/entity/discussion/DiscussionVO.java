package com.rocgit.knife4j.entity.discussion;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 研讨记录详情
 */
@Data
@Schema(description = "研讨记录")
public class DiscussionVO {

    @Schema(description = "研讨ID")
    private Long discussionId;

    @Schema(description = "所属项目ID")
    private Long projectId;

    @Schema(description = "主题")
    private String topic;

    @Schema(description = "详细内容")
    private String content;

    @Schema(description = "会议时间")
    private LocalDateTime meetingTime;

    @Schema(description = "参会人员")
    private List<Attendee> attendees;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Data
    @Schema(description = "参会人")
    public static class Attendee {
        private Long userId;
        private String nickname;
        private String avatarUrl;
    }
}
