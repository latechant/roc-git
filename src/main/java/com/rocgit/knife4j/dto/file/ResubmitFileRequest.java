package com.rocgit.knife4j.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 重新提交被驳回文件请求
 */
@Data
@Schema(description = "重新提交文件请求")
public class ResubmitFileRequest {

    @Schema(description = "新文件二进制", format = "binary",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private MultipartFile file;

    @Schema(description = "重新提交说明")
    private String remark;
}
