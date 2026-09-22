package com.rocgit.knife4j.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * 完成任务请求
 */
@Data
@Schema(description = "完成任务请求")
public class CompleteTaskRequest {

    @Size(max = 500, message = "完成说明不超过 500 字")
    @Schema(description = "完成说明", example = "已实现微信登录，单元测试通过")
    private String remark;

    @Schema(description = "完成凭证文件ID列表")
    private List<Long> proofFiles;
}
