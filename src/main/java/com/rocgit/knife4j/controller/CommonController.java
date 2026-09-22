package com.rocgit.knife4j.controller;

import com.rocgit.knife4j.common.Result;
import com.rocgit.knife4j.dto.common.FeedbackRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用模块 Controller - 完整 DTO 版本
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "01.通用", description = "通用接口（健康检查、配置、上传等）")
public class CommonController {

    @Operation(summary = "健康检查", description = "检查服务存活状态")
    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("version", "1.0.0");
        data.put("timestamp", System.currentTimeMillis());
        return Result.success(data);
    }

    @Operation(summary = "获取服务端配置", description = "获取小程序配置、版本号等")
    @GetMapping("/config")
    public Result<Map<String, Object>> config() {
        Map<String, Object> data = new HashMap<>();
        data.put("minVersion", "1.0.0");
        data.put("latestVersion", "1.0.0");
        data.put("maintenanceMode", false);
        data.put("announcement", "欢迎使用 ROC-git 协作平台");
        return Result.success(data);
    }

    @Operation(summary = "文件上传", description = "通用文件上传（头像/海报）")
    @PostMapping("/upload")
    public Result<Map<String, Object>> upload() {
        Map<String, Object> data = new HashMap<>();
        data.put("fileId", System.currentTimeMillis());
        data.put("url", "https://cdn.rocgit.com/files/" + System.currentTimeMillis() + ".png");
        return Result.success(data);
    }

    @Operation(summary = "获取上传凭证", description = "获取对象存储上传凭证（STS）")
    @PostMapping("/upload/credentials")
    public Result<Map<String, Object>> uploadCredentials() {
        Map<String, Object> data = new HashMap<>();
        data.put("accessKeyId", "AKIDxxxxxxxxxxxx");
        data.put("accessKeySecret", "xxxxxxxxxxxx");
        data.put("securityToken", "STS.xxxxxxxxxxxx");
        data.put("expiration", System.currentTimeMillis() + 3600_000);
        return Result.success(data);
    }

    @Operation(summary = "提交反馈", description = "用户提交问题反馈或建议")
    @PostMapping("/feedback")
    public Result<Void> feedback(@Valid @RequestBody FeedbackRequest request) {
        return Result.success("反馈已收到，感谢您的支持", null);
    }
}
