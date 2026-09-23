package com.rocgit.knife4j.controller;

import com.rocgit.knife4j.common.Result;
import com.rocgit.knife4j.dto.project.*;
import com.rocgit.knife4j.entity.project.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1/project")
@Tag(name = "04.项目", description = "项目管理相关接口")
@SecurityRequirement(name = "Bearer")
public class ProjectController {

    @Operation(summary = "创建项目", description = "在小组下创建新项目")
    @PostMapping("/create")
    public Result<ProjectDetailVO> create(@Valid @RequestBody CreateProjectRequest request) {
        ProjectDetailVO data = new ProjectDetailVO();
        data.setProjectId(System.currentTimeMillis());
        data.setGroupId(request.getGroupId());
        data.setName(request.getName());
        data.setDescription(request.getDescription());
        data.setCurrentStage("需求分析");
        data.setProgress(0);
        data.setStatus("active");
        data.setMemberCount(5);
        data.setFileCount(0);
        data.setTaskCount(0);
        data.setCompletedTaskCount(0);
        data.setCreatedAt(LocalDateTime.now());
        return Result.success(data);
    }

    @Operation(summary = "项目列表", description = "查询小组下的项目列表")
    @GetMapping("/list")
    public Result<List<ProjectVO>> list(@Parameter(description = "小组ID", example = "1") @RequestParam Long groupId) {
        List<ProjectVO> list = new ArrayList<>();
        ProjectVO p = new ProjectVO();
        p.setProjectId(1L);
        p.setGroupId(groupId);
        p.setName("课程设计项目");
        p.setStatus("active");
        p.setProgress(65);
        p.setCreatedAt(LocalDateTime.now());
        list.add(p);
        return Result.success(list);
    }

    @Operation(summary = "项目详情", description = "获取项目详细信息")
    @GetMapping("/{projectId}")
    public Result<ProjectDetailVO> detail(@PathVariable Long projectId) {
        ProjectDetailVO data = new ProjectDetailVO();
        data.setProjectId(projectId);
        data.setName("课程设计项目");
        data.setDescription("基于 Spring Boot 的课程协作管理");
        data.setCurrentStage("实现阶段");
        data.setProgress(65);
        data.setStatus("active");

        StageVO s1 = new StageVO();
        s1.setStageId(1L);
        s1.setName("需求分析");
        s1.setOrderNum(1);
        s1.setStatus("completed");
        StageVO s2 = new StageVO();
        s2.setStageId(2L);
        s2.setName("概要设计");
        s2.setOrderNum(2);
        s2.setStatus("in_progress");
        data.setStages(Arrays.asList(s1, s2));
        data.setMemberCount(5);
        data.setFileCount(28);
        data.setTaskCount(20);
        data.setCompletedTaskCount(12);
        data.setCreatedAt(LocalDateTime.now().minusMonths(1));
        return Result.success(data);
    }

    @Operation(summary = "更新项目", description = "更新项目信息")
    @PutMapping("/{projectId}")
    public Result<Void> update(@PathVariable Long projectId, @Valid @RequestBody UpdateProjectRequest request) {
        return Result.success("更新成功", null);
    }

    @Operation(summary = "删除项目", description = "删除项目（软删除）")
    @DeleteMapping("/{projectId}")
    public Result<Void> delete(@PathVariable Long projectId) {
        return Result.success("删除成功", null);
    }

    @Operation(summary = "复制项目", description = "复制一个项目作为模板")
    @PostMapping("/{projectId}/copy")
    public Result<ProjectVO> copy(@PathVariable Long projectId, @Valid @RequestBody CopyProjectRequest request) {
        ProjectVO data = new ProjectVO();
        data.setProjectId(System.currentTimeMillis());
        data.setName(request.getName());
        data.setStatus("active");
        data.setProgress(0);
        return Result.success(data);
    }

    @Operation(summary = "项目结项", description = "完成项目结项流程")
    @PostMapping("/{projectId}/complete")
    public Result<Void> complete(@PathVariable Long projectId) {
        return Result.success("结项成功", null);
    }

    @Operation(summary = "项目进度", description = "获取项目整体进度")
    @GetMapping("/{projectId}/progress")
    public Result<Map<String, Object>> progress(@PathVariable Long projectId) {
        Map<String, Object> data = new HashMap<>();
        data.put("overall", 65);
        Map<String, Object> fileStats = new HashMap<>();
        fileStats.put("total", 28);
        fileStats.put("approved", 25);
        Map<String, Object> taskStats = new HashMap<>();
        taskStats.put("total", 20);
        taskStats.put("completed", 12);
        data.put("fileStats", fileStats);
        data.put("taskStats", taskStats);
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
        List<ActivityVO> list = new ArrayList<>();
        ActivityVO a = new ActivityVO();
        a.setId(1L);
        a.setType("file");
        a.setAction("上传了文件");
        a.setUserId(10001L);
        a.setUserName("张三");
        a.setTargetId(101L);
        a.setTargetName("需求文档.docx");
        a.setCreatedAt(LocalDateTime.now().toString());
        list.add(a);
        return Result.success(list);
    }

    @Operation(summary = "任务统计", description = "项目的任务完成情况")
    @GetMapping("/{projectId}/task-stats")
    public Result<Map<String, Object>> taskStats(@PathVariable Long projectId) {
        Map<String, Object> data = new HashMap<>();
        data.put("total", 20);
        data.put("completed", 12);
        data.put("in_progress", 5);
        data.put("pending", 3);
        return Result.success(data);
    }

    @Operation(summary = "新增阶段", description = "为项目添加新阶段")
    @PostMapping("/{projectId}/stage")
    public Result<StageVO> addStage(@PathVariable Long projectId, @Valid @RequestBody AddStageRequest request) {
        StageVO data = new StageVO();
        data.setStageId(System.currentTimeMillis());
        data.setName(request.getName());
        data.setOrderNum(request.getOrderNum());
        data.setStatus("pending");
        data.setStartDate(request.getStartDate());
        data.setEndDate(request.getEndDate());
        return Result.success(data);
    }

    @Operation(summary = "阶段列表", description = "项目的所有阶段")
    @GetMapping("/{projectId}/stage")
    public Result<List<StageVO>> stageList(@PathVariable Long projectId) {
        List<StageVO> list = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            StageVO s = new StageVO();
            s.setStageId((long) i);
            s.setName("阶段" + i);
            s.setOrderNum(i);
            s.setStatus(i <= 3 ? "completed" : "pending");
            s.setFileCount(i * 5);
            s.setTaskCount(i * 4);
            s.setCompletedTaskCount(i * 2);
            list.add(s);
        }
        return Result.success(list);
    }

    @Operation(summary = "调整阶段顺序", description = "重新排列项目阶段")
    @PutMapping("/{projectId}/stage/order")
    public Result<Void> stageOrder(@PathVariable Long projectId, @Valid @RequestBody StageOrderRequest request) {
        return Result.success("顺序已更新", null);
    }

    @Operation(summary = "阶段详情", description = "获取阶段详细信息")
    @GetMapping("/{projectId}/stage/{stageId}")
    public Result<StageVO> stageDetail(@PathVariable Long projectId, @PathVariable Long stageId) {
        StageVO data = new StageVO();
        data.setStageId(stageId);
        data.setName("实现阶段");
        data.setOrderNum(3);
        data.setStatus("in_progress");
        data.setStartDate(LocalDate.now());
        data.setEndDate(LocalDate.now().plusDays(14));
        data.setFileCount(12);
        data.setTaskCount(10);
        data.setCompletedTaskCount(5);
        return Result.success(data);
    }

    @Operation(summary = "更新阶段", description = "更新阶段信息")
    @PutMapping("/{projectId}/stage/{stageId}")
    public Result<Void> updateStage(@PathVariable Long projectId, @PathVariable Long stageId,
                                    @Valid @RequestBody UpdateStageRequest request) {
        return Result.success("更新成功", null);
    }

    @Operation(summary = "删除阶段", description = "删除项目阶段")
    @DeleteMapping("/{projectId}/stage/{stageId}")
    public Result<Void> deleteStage(@PathVariable Long projectId, @PathVariable Long stageId) {
        return Result.success("删除成功", null);
    }

    @Operation(summary = "项目模板列表", description = "获取可用的项目模板")
    @GetMapping("/template/list")
    public Result<List<ProjectTemplateVO>> templateList() {
        List<ProjectTemplateVO> list = new ArrayList<>();
        ProjectTemplateVO t = new ProjectTemplateVO();
        t.setTemplateId(1L);
        t.setName("标准课程设计模板");
        t.setDescription("包含5个标准阶段");
        t.setStageCount(5);
        t.setIsPublic(true);
        list.add(t);
        return Result.success(list);
    }

    @Operation(summary = "模板详情", description = "查看项目模板详情")
    @GetMapping("/template/{templateId}")
    public Result<ProjectTemplateVO> templateDetail(@PathVariable Long templateId) {
        ProjectTemplateVO data = new ProjectTemplateVO();
        data.setTemplateId(templateId);
        data.setName("标准课程设计模板");
        data.setStageCount(5);
        return Result.success(data);
    }

    @Operation(summary = "创建项目模板", description = "保存项目为模板")
    @PostMapping("/template")
    public Result<ProjectTemplateVO> createTemplate(@Valid @RequestBody CreateTemplateRequest request) {
        ProjectTemplateVO data = new ProjectTemplateVO();
        data.setTemplateId(System.currentTimeMillis());
        data.setName(request.getName());
        data.setStageCount(request.getStages() != null ? request.getStages().size() : 0);
        data.setIsPublic(request.getIsPublic());
        return Result.success(data);
    }
}
