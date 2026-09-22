package com.rocgit.knife4j.dto.group;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 更新小组请求
 */
@Data
@Schema(description = "更新小组请求")
public class UpdateGroupRequest {

    @Size(min = 2, max = 20, message = "小组名称长度需在 2-20 字之间")
    @Schema(description = "小组名称", minLength = 2, maxLength = 20)
    private String name;

    @Schema(description = "小组描述")
    private String description;
}
