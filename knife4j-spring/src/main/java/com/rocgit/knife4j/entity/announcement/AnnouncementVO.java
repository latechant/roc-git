package com.rocgit.knife4j.entity.announcement;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统公告
 */
@Data
@Schema(description = "系统公告")
public class AnnouncementVO {

    @Schema(description = "公告ID")
    private Long announcementId;

    @Schema(description = "公告标题")
    private String title;

    @Schema(description = "公告内容")
    private String content;

    @Schema(description = "是否已读")
    private Boolean isRead;

    @Schema(description = "重要级别", example = "normal",
            allowableValues = {"normal", "important", "urgent"})
    private String level;

    @Schema(description = "发布时间")
    private LocalDateTime createdAt;
}
