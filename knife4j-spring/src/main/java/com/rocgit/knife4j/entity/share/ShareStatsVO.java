package com.rocgit.knife4j.entity.share;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 分享统计数据
 */
@Data
@Schema(description = "分享统计")
public class ShareStatsVO {

    @Schema(description = "总浏览数")
    private Integer totalViews;

    @Schema(description = "独立访客数")
    private Integer uniqueVisitors;

    @Schema(description = "总下载数")
    private Integer downloadCount;

    @Schema(description = "访问趋势")
    private List<ViewTrendItem> viewTrend;

    @Data
    @Schema(description = "每日访问趋势")
    public static class ViewTrendItem {
        @Schema(description = "日期", example = "2026-09-21")
        private String date;

        @Schema(description = "浏览数")
        private Integer views;
    }
}
