package com.rocgit.knife4j.entity.group;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 成员贡献统计
 */
@Data
@Schema(description = "成员贡献统计")
public class MemberStatsVO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像")
    private String avatarUrl;

    @Schema(description = "贡献分")
    private Integer contributions;

    @Schema(description = "上传文件数")
    private Integer fileCount;

    @Schema(description = "完成任务数")
    private Integer taskCount;

    @Schema(description = "排名", example = "1")
    private Integer rank;
}
