package com.rocgit.knife4j.dto.group;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 创建小组请求
 */
@Data
@Schema(description = "创建小组请求")
public class CreateGroupRequest {

    @NotBlank(message = "小组名称不能为空")
    @Size(min = 2, max = 20, message = "小组名称长度需在 2-20 字之间")
    @Schema(description = "小组名称", example = "课程设计第十二组",
            minLength = 2, maxLength = 20,
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "小组描述", example = "负责软工实践项目")
    private String description;
}
