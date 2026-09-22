package com.rocgit.knife4j.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 重命名文件请求
 */
@Data
@Schema(description = "重命名文件请求")
public class RenameFileRequest {

    @NotBlank
    @Schema(description = "新文件名", example = "需求文档v2.docx",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
}
