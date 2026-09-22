package com.rocgit.knife4j.controller;

import com.rocgit.knife4j.common.Result;
import com.rocgit.knife4j.dto.version.VersionRollbackRequest;
import com.rocgit.knife4j.entity.version.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1/version")
@Tag(name = "06.版本", description = "文件版本管理")
@SecurityRequirement(name = "Bearer")
public class VersionController {

    @Operation(summary = "版本列表", description = "获取文件的所有历史版本")
    @GetMapping("/list")
    public Result<List<FileVersionVO>> list(
            @Parameter(description = "文件ID", example = "1") @RequestParam Long fileId) {
        List<FileVersionVO> list = new ArrayList<>();
        for (int v = 1; v <= 5; v++) {
            FileVersionVO ver = new FileVersionVO();
            ver.setVersion(v);
            ver.setComment("第" + v + "次修改");
            ver.setUploaderId(10001L);
            ver.setUploaderName("用户" + v);
            ver.setSize(1024L * v);
            ver.setCreatedAt(LocalDateTime.now().minusDays(v));
            list.add(ver);
        }
        return Result.success(list);
    }

    @Operation(summary = "版本对比", description = "对比两个版本的差异")
    @GetMapping("/compare")
    public Result<VersionCompareVO> compare(
            @Parameter(description = "文件ID") @RequestParam Long fileId,
            @Parameter(description = "源版本号", example = "1") @RequestParam Integer fromVersion,
            @Parameter(description = "目标版本号", example = "3") @RequestParam Integer toVersion) {
        VersionCompareVO data = new VersionCompareVO();
        data.setDiffUrl("https://cdn.rocgit.com/diff/" + fileId);
        data.setAdditions(12);
        data.setDeletions(5);
        return Result.success(data);
    }

    @Operation(summary = "版本回溯", description = "回滚文件到指定版本")
    @PostMapping("/{fileId}/rollback")
    public Result<Map<String, Object>> rollback(@PathVariable Long fileId,
                                              @Valid @RequestBody VersionRollbackRequest request) {
        Map<String, Object> data = new HashMap<>();
        data.put("newVersionId", "v" + System.currentTimeMillis());
        data.put("rollbackToVersion", request.getTargetVersion());
        return Result.success(data);
    }
}
