package com.rocgit.knife4j.dto.group;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Size;

/**
 * 修改成员备注请求
 */
@Data
@Schema(description = "修改成员备注请求")
public class UpdateRemarkRequest {

    @Size(max = 20, message = "备注不超过 20 字")
    @Schema(description = "备注名", example = "前端大佬", maxLength = 20)
    private String remark;
}
