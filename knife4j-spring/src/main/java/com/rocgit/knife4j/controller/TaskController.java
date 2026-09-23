package com.rocgit.knife4j.controller;

import com.rocgit.knife4j.common.Result;
import com.rocgit.knife4j.dto.task.*;
import com.rocgit.knife4j.entity.task.TaskVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1/task")
@Tag(name = "07.任务", description = "任务分配与管理")
@SecurityRequirement(name = "Bearer")
public class TaskController {

    @Operation(summary = "创建任务", description = "在项目中创建任务并分配给成员")
    @PostMapping("/create")
    public Result<TaskVO> create(@Valid @RequestBody CreateTaskRequest request) {
        TaskVO data = new TaskVO();
        data.setTaskId(System.currentTimeMillis());
        data.setProjectId(request.getProjectId());
        data.setName(request.getName());
        data.setDescription(request.getDescription());
        data.setAssigneeId(request.getAssigneeId());
        data.setDeadline(request.getDeadline());
        data.setPriority(request.getPriority());
        data.setStatus("pending");
        data.setProgress(0);
        data.setCreatedAt(LocalDateTime.now());
        return Result.success(data);
    }

    @Operation(summary = "任务列表", description = "查询项目下的任务列表")
    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @Parameter(description = "项目ID", example = "1") @RequestParam Long projectId,
            @Parameter(description = "按负责人筛选") @RequestParam(required = false) Long assigneeId,
            @Parameter(schema = @io.swagger.v3.oas.annotations.media.Schema(allowableValues = {"pending", "in_progress", "completed"}))
            @RequestParam(required = false) String status) {
        Map<String, Object> data = new HashMap<>();
        data.put("total", 20);
        data.put("tasks", new ArrayList<>());
        return Result.success(data);
    }

    @Operation(summary = "我的任务", description = "获取当前用户负责的任务")
    @GetMapping("/my-tasks")
    public Result<Map<String, Object>> myTasks(
            @Parameter(schema = @io.swagger.v3.oas.annotations.media.Schema(allowableValues = {"pending", "in_progress", "completed"}))
            @RequestParam(required = false) String status,
            @Parameter(description = "只看已过期任务") @RequestParam(required = false) Boolean overdue,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        TaskVO t = new TaskVO();
        t.setTaskId(1L);
        t.setName("完成登录模块");
        t.setPriority("high");
        t.setStatus("in_progress");
        t.setDeadline(LocalDateTime.now().plusDays(7));
        Map<String, Object> data = new HashMap<>();
        data.put("total", 5);
        data.put("tasks", Arrays.asList(t));
        return Result.success(data);
    }

    @Operation(summary = "即将到期", description = "一周内即将到期的任务")
    @GetMapping("/due-soon")
    public Result<List<TaskVO>> dueSoon(@Parameter(description = "天数", example = "7") @RequestParam(defaultValue = "7") Integer days) {
        return Result.success(new ArrayList<>());
    }

    @Operation(summary = "任务详情", description = "获取任务详细信息")
    @GetMapping("/{taskId}")
    public Result<TaskVO> detail(@PathVariable Long taskId) {
        TaskVO data = new TaskVO();
        data.setTaskId(taskId);
        data.setProjectId(1L);
        data.setProjectName("课程设计项目");
        data.setName("完成登录模块");
        data.setDescription("实现微信扫码登录功能");
        data.setAssigneeId(10001L);
        data.setAssigneeName("张三");
        data.setDeadline(LocalDateTime.now().plusDays(7));
        data.setPriority("high");
        data.setStatus("in_progress");
        data.setProgress(50);
        data.setCreatedAt(LocalDateTime.now().minusDays(3));
        return Result.success(data);
    }

    @Operation(summary = "更新任务", description = "修改任务信息")
    @PutMapping("/{taskId}")
    public Result<Void> update(@PathVariable Long taskId, @Valid @RequestBody UpdateTaskRequest request) {
        return Result.success("更新成功", null);
    }

    @Operation(summary = "删除任务", description = "删除任务")
    @DeleteMapping("/{taskId}")
    public Result<Void> delete(@PathVariable Long taskId) {
        return Result.success("删除成功", null);
    }

    @Operation(summary = "完成任务", description = "将任务标记为已完成，可上传凭证文件")
    @PostMapping("/{taskId}/complete")
    public Result<Void> complete(@PathVariable Long taskId, @RequestBody(required = false) @Valid CompleteTaskRequest request) {
        return Result.success("任务已完成", null);
    }

    @Operation(summary = "放弃任务", description = "放弃当前任务，需说明原因")
    @PostMapping("/{taskId}/abandon")
    public Result<Void> abandon(@PathVariable Long taskId, @RequestBody(required = false) @Valid AbandonTaskRequest request) {
        return Result.success("任务已放弃", null);
    }

    @Operation(summary = "转交任务", description = "把任务转交给其他人")
    @PostMapping("/{taskId}/transfer")
    public Result<Void> transfer(@PathVariable Long taskId, @Valid @RequestBody TransferTaskRequest request) {
        return Result.success("转交成功", null);
    }

    @Operation(summary = "更新进度", description = "更新任务完成进度 0-100")
    @PostMapping("/{taskId}/progress")
    public Result<Void> updateProgress(@PathVariable Long taskId, @Valid @RequestBody TaskProgressRequest request) {
        return Result.success("进度已更新", null);
    }
}
