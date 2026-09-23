package com.rocgit.knife4j.dto.version;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 版本回滚请求
 */
@Data
@Schema(description = "版本回滚请求")
public class VersionRollbackRequest {

    @NotNull
    @Schema(description = "目标版本号", example = "3",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer targetVersion;

    @Schema(description = "回滚说明", example = "新版本有 bug，回退到 v3")
    private String remark;
}
