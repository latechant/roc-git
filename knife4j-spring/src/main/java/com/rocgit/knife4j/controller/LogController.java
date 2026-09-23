package com.rocgit.knife4j.controller;

import com.rocgit.knife4j.common.Result;
import com.rocgit.knife4j.entity.log.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1/log")
@Tag(name = "11.日志", description = "操作日志查询")
@SecurityRequirement(name = "Bearer")
public class LogController {

    @Operation(summary = "操作日志列表", description = "查询项目的操作日志")
    @GetMapping("/list")
    public Result<List<OperationLogVO>> list(
            @Parameter(description = "项目ID", example = "1") @RequestParam Long projectId,
            @Parameter(description = "日志类型", schema = @io.swagger.v3.oas.annotations.media.Schema(allowableValues = {"file", "task", "stage", "member"}))
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        List<OperationLogVO> list = new ArrayList<>();
        OperationLogVO log = new OperationLogVO();
        log.setLogId(1L);
        log.setAction("file_upload");
        log.setUserId(10001L);
        log.setUserName("张三");
        log.setTargetType("file");
        log.setTargetId(101L);
        log.setTargetName("需求文档.docx");
        log.setDetail("上传了文件");
        log.setCreatedAt(LocalDateTime.now());
        log.setIp("192.168.1.1");
        list.add(log);
        return Result.success(list);
    }

    @Operation(summary = "日志详情", description = "查看单条日志详情")
    @GetMapping("/{logId}")
    public Result<OperationLogVO> detail(@PathVariable Long logId) {
        OperationLogVO data = new OperationLogVO();
        data.setLogId(logId);
        data.setAction("file_upload");
        data.setUserId(10001L);
        data.setUserName("张三");
        data.setTargetType("file");
        data.setTargetId(101L);
        data.setTargetName("需求文档.docx");
        data.setDetail("上传了文件");
        data.setCreatedAt(LocalDateTime.now());
        return Result.success(data);
    }

    @Operation(summary = "日志统计", description = "项目日志的统计信息")
    @GetMapping("/project/{projectId}/log-stats")
    public Result<LogStatsVO> logStats(@PathVariable Long projectId) {
        LogStatsVO data = new LogStatsVO();
        data.setTotalLogs(100L);
        data.setTodayLogs(15);
        data.setActiveUsers(5);
        data.setFileOperations(60);
        data.setTaskOperations(40);
        return Result.success(data);
    }
}
