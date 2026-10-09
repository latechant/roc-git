package com.rocgit.knife4j.dto.project;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 创建项目请求
 */
@Data
@Schema(description = "创建项目请求")
public class CreateProjectRequest {

    @NotNull(message = "小组ID不能为空")
    @Schema(description = "所属小组ID", example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Long groupId;

    @NotBlank(message = "项目名称不能为空")
    @Schema(description = "项目名称", example = "课程设计项目",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "项目描述", example = "基于 Spring Boot 的协作管理")
    private String description;

    @Schema(description = "项目模板ID（可选）", example = "1")
    private Long templateId;

    @Schema(description = "项目阶段列表")
    private List<StageItem> stages;

    @Schema(description = "项目成员列表")
    private List<MemberItem> members;

    @Schema(description = "审核规则")
    private AuditRule auditRule;

    @Schema(description = "提醒规则")
    private ReminderRule reminderRule;

    @Data
    @Schema(description = "阶段项")
    public static class StageItem {
        @Schema(description = "阶段名称", example = "需求分析")
        private String name;

        @Schema(description = "阶段顺序", example = "1")
        private Integer orderNum;

        @Schema(description = "开始日期", example = "2026-10-01")
        private java.time.LocalDate startDate;

        @Schema(description = "结束日期", example = "2026-10-07")
        private java.time.LocalDate endDate;
    }

    @Data
    @Schema(description = "项目成员项")
    public static class MemberItem {
        @Schema(description = "用户ID", example = "10001")
        private Long userId;

        @Schema(description = "角色：leader=组长，member=组员，teacher=教师", example = "member")
        private String role;
    }

    @Data
    @Schema(description = "审核规则")
    public static class AuditRule {
        @Schema(description = "是否需要审核")
        private Boolean requireAudit;

        @Schema(description = "审核人角色", example = "leader")
        private String auditorRole;
    }

    @Data
    @Schema(description = "提醒规则")
    public static class ReminderRule {
        @Schema(description = "任务到期前几天提醒", example = "[3, 1]")
        private List<Integer> taskReminderDays;
    }
}
