package com.rocgit.knife4j.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 分页响应
 */
@Data
@Schema(description = "分页响应数据")
public class PageResult<T> {

    @Schema(description = "总记录数")
    private Long total;

    @Schema(description = "数据列表")
    private List<T> list;

    @Schema(description = "当前页码")
    private Integer pageNum;

    @Schema(description = "每页大小")
    private Integer pageSize;

    public PageResult() {}

    public PageResult(Long total, List<T> list, Integer pageNum, Integer pageSize) {
        this.total = total;
        this.list = list;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public static <T> PageResult<T> of(Long total, List<T> list, PageRequest pageRequest) {
        return new PageResult<>(total, list, pageRequest.getPageNum(), pageRequest.getPageSize());
    }
}
