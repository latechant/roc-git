package com.rocgit.knife4j.controller;

import com.rocgit.knife4j.common.Result;
import com.rocgit.knife4j.dto.share.*;
import com.rocgit.knife4j.entity.share.*;
import com.rocgit.knife4j.service.ShareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/share")
@Tag(name = "14.分享", description = "结项分享相关接口")
@SecurityRequirement(name = "Bearer")
public class ShareController {

    @Autowired
    private ShareService shareService;

    @Operation(summary = "创建分享链接", description = "为项目结项创建对外分享链接")
    @PostMapping("/create")
    public Result<ShareVO> create(@Valid @RequestBody ShareCreateRequest request) {
        return Result.success(shareService.create(request));
    }

    @Operation(summary = "分享列表", description = "我创建的分享列表")
    @GetMapping("/list")
    public Result<List<ShareVO>> list(
            @Parameter(description = "项目ID", example = "1") @RequestParam Long projectId) {
        return Result.success(shareService.listByProject(projectId));
    }

    @Operation(summary = "分享详情", description = "分享链接的详细信息")
    @GetMapping("/{shareId:\\d+}")
    public Result<ShareVO> detail(@PathVariable Long shareId) {
        return Result.success(shareService.detail(shareId));
    }

    @Operation(summary = "更新分享权限", description = "更新分享链接的权限和有效期")
    @PutMapping("/{shareId}")
    public Result<Void> update(@PathVariable Long shareId, @Valid @RequestBody ShareUpdateRequest request) {
        shareService.update(shareId, request);
        return Result.success("更新成功", null);
    }

    @Operation(summary = "取消分享", description = "删除分享链接")
    @DeleteMapping("/{shareId}")
    public Result<Void> cancel(@PathVariable Long shareId) {
        shareService.delete(shareId);
        return Result.success("已取消分享", null);
    }

    @Operation(summary = "重新生成分享链接", description = "旧链接失效，生成新的分享链接")
    @PostMapping("/{shareId}/regenerate")
    public Result<ShareVO> regenerate(@PathVariable Long shareId) {
        return Result.success(shareService.regenerate(shareId));
    }

    @Operation(summary = "分享统计", description = "分享链接的访问统计")
    @GetMapping("/{shareId}/stats")
    public Result<ShareStatsVO> stats(@PathVariable Long shareId) {
        return Result.success(shareService.stats(shareId));
    }

    @Operation(summary = "访问日志", description = "分享链接的访问日志")
    @GetMapping("/{shareId}/access-logs")
    public Result<Map<String, Object>> accessLogs(
            @PathVariable Long shareId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(shareService.accessLogs(shareId, pageNum, pageSize));
    }

    @Operation(summary = "生成分享海报", description = "为分享链接生成分享海报图片")
    @PostMapping("/{shareId}/poster")
    public Result<Map<String, Object>> poster(@PathVariable Long shareId) {
        return Result.success(shareService.poster(shareId));
    }

    @Operation(summary = "校验分享令牌", description = "访客校验分享链接是否有效")
    @PostMapping("/validate")
    public Result<Map<String, Object>> validate(@Valid @RequestBody ShareValidateRequest request) {
        return Result.success(shareService.validate(request));
    }

    // ===== 访客无需鉴权接口（路径前缀 /p/，与白名单 /api/v1/share/p/** 对应） =====

    @Operation(summary = "访客查看分享", description = "通过分享令牌查看项目（无需登录）")
    @GetMapping("/p/{shareToken}")
    public Result<Map<String, Object>> viewByToken(@PathVariable String shareToken) {
        return Result.success(shareService.viewByToken(shareToken));
    }

    @Operation(summary = "项目概览", description = "分享页的项目概览数据")
    @GetMapping("/p/{shareToken}/overview")
    public Result<Map<String, Object>> overview(@PathVariable String shareToken) {
        return Result.success(shareService.overview(shareToken));
    }

    @Operation(summary = "协作时间线", description = "项目的协作时间线")
    @GetMapping("/p/{shareToken}/timeline")
    public Result<List<Map<String, Object>>> timeline(@PathVariable String shareToken) {
        return Result.success(shareService.timeline(shareToken));
    }

    @Operation(summary = "文件列表", description = "分享页的文件列表")
    @GetMapping("/p/{shareToken}/files")
    public Result<Map<String, Object>> files(@PathVariable String shareToken) {
        return Result.success(shareService.files(shareToken));
    }

    @Operation(summary = "文件预览", description = "分享页的文件预览")
    @GetMapping("/p/{shareToken}/file/{fileId}/preview")
    public Result<Map<String, Object>> filePreview(@PathVariable String shareToken, @PathVariable Long fileId) {
        return Result.success(shareService.filePreview(shareToken, fileId));
    }

    @Operation(summary = "文件下载", description = "分享页的文件下载")
    @GetMapping("/p/{shareToken}/file/{fileId}/download")
    public Result<Map<String, Object>> fileDownload(@PathVariable String shareToken, @PathVariable Long fileId) {
        return Result.success(shareService.fileDownload(shareToken, fileId));
    }

    @Operation(summary = "研讨列表", description = "分享页的研讨记录")
    @GetMapping("/p/{shareToken}/discussions")
    public Result<List<Map<String, Object>>> discussions(@PathVariable String shareToken) {
        return Result.success(shareService.discussions(shareToken));
    }

    @Operation(summary = "决策列表", description = "分享页的决策列表")
    @GetMapping("/p/{shareToken}/decisions")
    public Result<List<Map<String, Object>>> decisions(@PathVariable String shareToken) {
        return Result.success(shareService.decisions(shareToken));
    }
}
