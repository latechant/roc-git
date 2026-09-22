package com.rocgit.knife4j.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 通用分页请求
 */
@Data
@Schema(description = "分页请求参数")
public class PageRequest {

    @Schema(description = "页码（从 1 开始）", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "20")
    private Integer pageSize = 20;

    @Schema(description = "排序字段", example = "createdAt")
    private String orderBy;

    @Schema(description = "排序方向：asc/desc", example = "desc")
    private String orderDir = "desc";
}
