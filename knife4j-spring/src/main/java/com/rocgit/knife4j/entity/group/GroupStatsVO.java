package com.rocgit.knife4j.entity.group;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 小组统计数据
 */
@Data
@Schema(description = "小组统计")
public class GroupStatsVO {

    @Schema(description = "成员数量")
    private Integer memberCount;

    @Schema(description = "项目数量")
    private Integer projectCount;

    @Schema(description = "进行中项目数量")
    private Integer activeProjectCount;

    @Schema(description = "已结项项目数量")
    private Integer completedProjectCount;

    @Schema(description = "总文件数")
    private Integer totalFileCount;

    @Schema(description = "总任务数")
    private Integer totalTaskCount;
}
