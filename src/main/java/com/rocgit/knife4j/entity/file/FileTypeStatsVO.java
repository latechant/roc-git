package com.rocgit.knife4j.entity.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 文件类型统计
 */
@Data
@Schema(description = "文件类型统计")
public class FileTypeStatsVO {

    @Schema(description = "文档类型数", example = "10")
    private Integer document;

    @Schema(description = "图片数", example = "8")
    private Integer image;

    @Schema(description = "代码文件数", example = "6")
    private Integer code;

    @Schema(description = "压缩包数", example = "2")
    private Integer archive;

    @Schema(description = "其他数", example = "2")
    private Integer other;

    @Schema(description = "总数", example = "28")
    private Integer total;
}
