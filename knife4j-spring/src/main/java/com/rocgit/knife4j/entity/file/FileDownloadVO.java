package com.rocgit.knife4j.entity.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 文件下载链接响应
 */
@Data
@Schema(description = "文件下载响应")
public class FileDownloadVO {

    @Schema(description = "下载URL")
    private String downloadUrl;

    @Schema(description = "过期时间戳（毫秒）")
    private Long expiresAt;

    @Schema(description = "文件名")
    private String filename;
}
