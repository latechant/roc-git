package com.rocgit.knife4j.controller;

import com.rocgit.knife4j.common.Result;
import com.rocgit.knife4j.dto.version.VersionRollbackRequest;
import com.rocgit.knife4j.entity.version.*;
import com.rocgit.knife4j.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

/**
 * 版本模块 Controller - 接入真实 MySQL
 */
@RestController
@RequestMapping("/api/v1/version")
@Tag(name = "06.版本", description = "文件版本管理")
@SecurityRequirement(name = "Bearer")
public class VersionController {

    @Autowired
    private FileService fileService;

    @Operation(summary = "版本列表", description = "获取文件的所有历史版本")
    @GetMapping("/list")
    public Result<List<FileVersionVO>> list(
            @Parameter(description = "文件ID", example = "1") @RequestParam Long fileId) {
        return Result.success(fileService.versionList(fileId));
    }

    @Operation(summary = "版本对比", description = "对比两个版本的差异")
    @GetMapping("/compare")
    public Result<VersionCompareVO> compare(
            @Parameter(description = "文件ID") @RequestParam Long fileId,
            @Parameter(description = "源版本号", example = "1") @RequestParam Integer fromVersion,
            @Parameter(description = "目标版本号", example = "3") @RequestParam Integer toVersion) {
        return Result.success(fileService.compare(fileId, fromVersion, toVersion));
    }

    @Operation(summary = "版本回溯", description = "回滚文件到指定版本")
    @PostMapping("/{fileId}/rollback")
    public Result<Map<String, Object>> rollback(@RequestAttribute("userId") String userId,
                                                @PathVariable Long fileId,
                                                @Valid @RequestBody VersionRollbackRequest request) {
        return Result.success(fileService.rollback(fileId, request.getTargetVersion(), Long.parseLong(userId)));
    }
}
