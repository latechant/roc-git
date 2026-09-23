package com.rocgit.knife4j.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传请求（multipart/form-data）
 */
@Data
@Schema(description = "文件上传请求")
public class FileUploadRequest {

    @Schema(description = "上传的文件二进制", format = "binary",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private MultipartFile file;

    @Schema(description = "所属项目ID", example = "1")
    private Long projectId;

    @Schema(description = "目标阶段ID", example = "2")
    private Long stageId;

    @Schema(description = "上传说明", example = "第一版需求文档")
    private String remark;
}
