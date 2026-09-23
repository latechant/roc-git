package com.rocgit.knife4j.controller;

import com.rocgit.knife4j.common.Result;
import com.rocgit.knife4j.entity.log.*;
import com.rocgit.knife4j.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/log")
@Tag(name = "11.日志", description = "操作日志查询")
@SecurityRequirement(name = "Bearer")
public class LogController {

    @Autowired
    private LogService logService;

    @Operation(summary = "操作日志列表", description = "查询项目的操作日志")
    @GetMapping("/list")
    public Result<List<OperationLogVO>> list(
            @Parameter(description = "项目ID", example = "1") @RequestParam Long projectId,
            @Parameter(description = "日志类型", schema = @io.swagger.v3.oas.annotations.media.Schema(allowableValues = {"file", "task", "stage", "member"}))
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(logService.list(projectId, type, pageNum, pageSize));
    }

    @Operation(summary = "日志详情", description = "查看单条日志详情")
    @GetMapping("/{logId}")
    public Result<OperationLogVO> detail(@PathVariable Long logId) {
        return Result.success(logService.detail(logId));
    }

    @Operation(summary = "日志统计", description = "项目日志的统计信息")
    @GetMapping("/project/{projectId}/log-stats")
    public Result<LogStatsVO> logStats(@PathVariable Long projectId) {
        return Result.success(logService.logStats(projectId));
    }
}
