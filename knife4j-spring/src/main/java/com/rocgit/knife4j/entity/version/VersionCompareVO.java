package com.rocgit.knife4j.entity.version;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 版本对比结果
 */
@Data
@Schema(description = "版本对比")
public class VersionCompareVO {

    @Schema(description = "差异对比URL")
    private String diffUrl;

    @Schema(description = "新增行数")
    private Integer additions;

    @Schema(description = "删除行数")
    private Integer deletions;

    @Schema(description = "差异片段列表")
    private List<DiffHunk> hunks;

    @Data
    @Schema(description = "差异片段")
    public static class DiffHunk {
        private Integer startLine;
        private String content;
        private String type;
    }
}
