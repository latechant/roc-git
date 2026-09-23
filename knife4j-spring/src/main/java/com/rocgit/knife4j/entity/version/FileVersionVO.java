package com.rocgit.knife4j.entity.version;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件版本历史
 */
@Data
@Schema(description = "文件版本")
public class FileVersionVO {

    @Schema(description = "版本号")
    private Integer version;

    @Schema(description = "版本说明/提交信息")
    private String comment;

    @Schema(description = "上传者")
    private Long uploaderId;

    @Schema(description = "上传者昵称")
    private String uploaderName;

    @Schema(description = "文件大小（字节）")
    private Long size;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "下载URL")
    private String url;
}
