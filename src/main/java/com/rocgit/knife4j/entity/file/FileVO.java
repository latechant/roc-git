package com.rocgit.knife4j.entity.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件信息 VO
 */
@Data
@Schema(description = "文件信息")
public class FileVO {

    @Schema(description = "文件ID", example = "1")
    private Long fileId;

    @Schema(description = "文件名", example = "需求文档.docx")
    private String name;

    @Schema(description = "下载URL")
    private String url;

    @Schema(description = "文件大小（字节）")
    private Long size;

    @Schema(description = "文件类型/扩展名", example = "docx")
    private String type;

    @Schema(description = "状态", example = "approved",
            allowableValues = {"pending", "approved", "rejected"})
    private String status;

    @Schema(description = "版本号", example = "3")
    private Integer version;

    @Schema(description = "所属项目ID")
    private Long projectId;

    @Schema(description = "所属阶段ID")
    private Long stageId;

    @Schema(description = "所属阶段名称")
    private String stageName;

    @Schema(description = "上传者信息")
    private UploaderInfo uploader;

    @Schema(description = "是否已收藏")
    private Boolean isFavorite;

    @Schema(description = "上传时间")
    private LocalDateTime uploadedAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    @Data
    @Schema(description = "上传者信息")
    public static class UploaderInfo {
        @Schema(description = "用户ID")
        private Long userId;

        @Schema(description = "昵称")
        private String nickname;

        @Schema(description = "头像")
        private String avatarUrl;
    }
}
