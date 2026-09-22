package com.rocgit.knife4j.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 文件上传请求（multipart/form-data）
 */
@Data
@Schema(description = "文件上传请求")
public class UploadRequest {

    @Schema(description = "文件二进制内容", format = "binary", requiredMode = Schema.RequiredMode.REQUIRED)
    private byte[] file;

    @Schema(description = "上传类型", example = "file",
            allowableValues = {"file", "avatar", "poster"})
    private String type;

    @Schema(description = "所属项目ID", example = "1001")
    private Long projectId;
}
