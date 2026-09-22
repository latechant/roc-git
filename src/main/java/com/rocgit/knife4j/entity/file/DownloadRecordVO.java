package com.rocgit.knife4j.entity.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件下载记录
 */
@Data
@Schema(description = "下载记录")
public class DownloadRecordVO {

    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "下载用户ID")
    private Long userId;

    @Schema(description = "下载用户昵称")
    private String nickname;

    @Schema(description = "下载IP")
    private String ip;

    @Schema(description = "下载时间")
    private LocalDateTime downloadedAt;

    @Schema(description = "下载版本号")
    private Integer version;
}
