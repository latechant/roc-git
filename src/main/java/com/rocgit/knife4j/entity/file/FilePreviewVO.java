package com.rocgit.knife4j.entity.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 文件预览响应
 */
@Data
@Schema(description = "文件预览响应")
public class FilePreviewVO {

    @Schema(description = "预览URL（带水印）")
    private String url;

    @Schema(description = "文件类型", example = "pdf")
    private String type;

    @Schema(description = "是否水印", example = "true")
    private Boolean watermark;
}
