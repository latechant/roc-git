package com.rocgit.knife4j.controller;

import com.rocgit.knife4j.common.Result;
import com.rocgit.knife4j.dto.project.*;
import com.rocgit.knife4j.entity.project.*;
import com.rocgit.knife4j.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

/**
 * 项目模块 Controller - 接入真实 MySQL
 */
@RestController
@RequestMapping("/api/v1/project")
@Tag(name = "04.项目", description = "项目管理相关接口")
@SecurityRequirement(name = "Bearer")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Operation(summary = "创建项目", description = "在小组下创建新项目")
    @PostMapping("/create")
    public Result<ProjectDetailVO> create(@Valid @RequestBody CreateProjectRequest request,
                                           @RequestAttribute("userId") String userId) {
        ProjectDetailVO data = projectService.createProject(request, Long.parseLong(userId));
        return Result.success(data);
    }

    @Operation(summary = "项目列表", description = "查询小组下的项目列表")
    @GetMapping("/list")
    public Result<List<ProjectVO>> list(@Parameter(description = "小组ID", example = "1") @RequestParam Long groupId) {
        List<ProjectVO> list = projectService.listProjects(groupId);
        return Result.success(list);
    }

    @Operation(summary = "项目详情", description = "获取项目详细信息")
    @GetMapping("/{projectId}")
    public Result<ProjectDetailVO> detail(@PathVariable Long projectId,
                                         @RequestAttribute("userId") String userId) {
        ProjectDetailVO data = projectService.getProjectDetailForUser(projectId, Long.parseLong(userId));
        return Result.success(data);
    }

    @Operation(summary = "项目成员列表", description = "获取项目的成员列表")
    @GetMapping("/{projectId}/members")
    public Result<List<ProjectMemberVO>> members(@PathVariable Long projectId) {
        List<ProjectMemberVO> list = projectService.getProjectMembers(projectId);
        return Result.success(list);
    }

    @Operation(summary = "更新项目", description = "更新项目信息")
    @PutMapping("/{projectId}")
    public Result<Void> update(@PathVariable Long projectId, @Valid @RequestBody UpdateProjectRequest request) {
        projectService.updateProject(projectId, request);
        return Result.success("更新成功", null);
    }

    @Operation(summary = "删除项目", description = "物理删除项目（级联删除阶段/任务等）")
    @DeleteMapping("/{projectId}")
    public Result<Void> delete(@PathVariable Long projectId) {
        projectService.deleteProject(projectId);
        return Result.success("删除成功", null);
    }

    @Operation(summary = "复制项目", description = "复制一个项目作为模板")
    @PostMapping("/{projectId}/copy")
    public Result<ProjectVO> copy(@PathVariable Long projectId, @Valid @RequestBody CopyProjectRequest request) {
        ProjectVO data = projectService.copyProject(projectId, request);
        return Result.success(data);
    }

    @Operation(summary = "项目结项", description = "完成项目结项流程")
    @PostMapping("/{projectId}/complete")
    public Result<Void> complete(@PathVariable Long projectId) {
        projectService.completeProject(projectId);
        return Result.success("结项成功", null);
    }

    @Operation(summary = "项目归档", description = "归档项目（当前列表自动排除）")
    @PutMapping("/{projectId}/archive")
    public Result<Void> archive(@PathVariable Long projectId) {
        projectService.archiveProject(projectId);
        return Result.success("归档成功", null);
    }

    @Operation(summary = "项目进度", description = "获取项目整体进度")
    @GetMapping("/{projectId}/progress")
    public Result<Map<String, Object>> progress(@PathVariable Long projectId) {
        Map<String, Object> data = projectService.getProjectProgress(projectId);
        return Result.success(data);
    }

    @Operation(summary = "项目动态", description = "项目最近的活动记录")
    @GetMapping("/{projectId}/activities")
    public Result<List<ActivityVO>> activities(
            @PathVariable Long projectId,
            @Parameter(description = "类型筛选", example = "file",
                    schema = @io.swagger.v3.oas.annotations.media.Schema(allowableValues = {"file", "task", "stage", "member"}))
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        List<ActivityVO> list = projectService.getProjectActivities(projectId, type, pageNum, pageSize);
        return Result.success(list);
    }

    @Operation(summary = "任务统计", description = "项目的任务完成情况")
    @GetMapping("/{projectId}/task-stats")
    public Result<Map<String, Object>> taskStats(@PathVariable Long projectId) {
        Map<String, Object> data = projectService.getTaskStats(projectId);
        return Result.success(data);
    }

    @Operation(summary = "新增阶段", description = "为项目添加新阶段")
    @PostMapping("/{projectId}/stage")
    public Result<StageVO> addStage(@PathVariable Long projectId, @Valid @RequestBody AddStageRequest request) {
        StageVO data = projectService.addStage(projectId, request);
        return Result.success(data);
    }

    @Operation(summary = "阶段列表", description = "项目的所有阶段")
    @GetMapping("/{projectId}/stage")
    public Result<List<StageVO>> stageList(@PathVariable Long projectId) {
        List<StageVO> list = projectService.getStageList(projectId);
        return Result.success(list);
    }

    @Operation(summary = "调整阶段顺序", description = "重新排列项目阶段")
    @PutMapping("/{projectId}/stage/order")
    public Result<Void> stageOrder(@PathVariable Long projectId, @Valid @RequestBody StageOrderRequest request) {
        projectService.updateStageOrder(projectId, request);
        return Result.success("顺序已更新", null);
    }

    @Operation(summary = "阶段详情", description = "获取阶段详细信息")
    @GetMapping("/{projectId}/stage/{stageId}")
    public Result<StageVO> stageDetail(@PathVariable Long projectId, @PathVariable Long stageId) {
        StageVO data = projectService.getStageDetail(projectId, stageId);
        return Result.success(data);
    }

    @Operation(summary = "更新阶段", description = "更新阶段信息")
    @PutMapping("/{projectId}/stage/{stageId}")
    public Result<Void> updateStage(@PathVariable Long projectId, @PathVariable Long stageId,
                                    @Valid @RequestBody UpdateStageRequest request) {
        projectService.updateStage(projectId, stageId, request);
        return Result.success("更新成功", null);
    }

    @Operation(summary = "删除阶段", description = "删除项目阶段")
    @DeleteMapping("/{projectId}/stage/{stageId}")
    public Result<Void> deleteStage(@PathVariable Long projectId, @PathVariable Long stageId) {
        projectService.deleteStage(projectId, stageId);
        return Result.success("删除成功", null);
    }

    @Operation(summary = "阶段推进", description = "完成当前阶段并激活下一阶段，同时维护 current_stage_id（成员校验）")
    @PostMapping("/{projectId}/stage/{stageId}/advance")
    public Result<Map<String, Object>> advance(@RequestAttribute("userId") String userId,
                                               @PathVariable Long projectId, @PathVariable Long stageId) {
        Map<String, Object> data = projectService.advanceStage(projectId, stageId, Long.parseLong(userId));
        return Result.success(data);
    }

    @Operation(summary = "项目模板列表", description = "获取可用的项目模板")
    @GetMapping("/template/list")
    public Result<List<ProjectTemplateVO>> templateList() {
        List<ProjectTemplateVO> list = projectService.getTemplateList();
        return Result.success(list);
    }

    @Operation(summary = "模板详情", description = "查看项目模板详情")
    @GetMapping("/template/{templateId}")
    public Result<ProjectTemplateVO> templateDetail(@PathVariable Long templateId) {
        ProjectTemplateVO data = projectService.getTemplateDetail(templateId);
        return Result.success(data);
    }

    @Operation(summary = "创建项目模板", description = "保存项目为模板")
    @PostMapping("/template")
    public Result<ProjectTemplateVO> createTemplate(@Valid @RequestBody CreateTemplateRequest request) {
        ProjectTemplateVO data = projectService.createTemplate(request);
        return Result.success(data);
    }
}
