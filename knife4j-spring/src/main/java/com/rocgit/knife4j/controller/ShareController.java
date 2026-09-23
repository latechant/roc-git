package com.rocgit.knife4j.controller;

import com.rocgit.knife4j.common.Result;
import com.rocgit.knife4j.dto.share.*;
import com.rocgit.knife4j.entity.share.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1/share")
@Tag(name = "14.分享", description = "结项分享相关接口")
@SecurityRequirement(name = "Bearer")
public class ShareController {

    @Operation(summary = "创建分享链接", description = "为项目结项创建对外分享链接")
    @PostMapping("/create")
    public Result<ShareVO> create(@Valid @RequestBody ShareCreateRequest request) {
        ShareVO data = new ShareVO();
        data.setShareId(System.currentTimeMillis());
        data.setShareToken("tk" + System.currentTimeMillis());
        data.setShareUrl("https://share.rocgit.com/tk" + System.currentTimeMillis());
        data.setQrCodeUrl("https://cdn.rocgit.com/qr/tk" + System.currentTimeMillis() + ".png");
        data.setPermissionLevel(request.getPermissionLevel());
        data.setViewCount(0);
        data.setExpiresAt(request.getExpiresAt());
        data.setNeedPassword(request.getNeedPassword());
        data.setCreatedAt(LocalDateTime.now());
        return Result.success(data);
    }

    @Operation(summary = "分享列表", description = "我创建的分享列表")
    @GetMapping("/list")
    public Result<List<ShareVO>> list(
            @Parameter(description = "项目ID", example = "1") @RequestParam Long projectId) {
        return Result.success(new ArrayList<>());
    }

    @Operation(summary = "分享详情", description = "分享链接的详细信息")
    @GetMapping("/{shareId}")
    public Result<ShareVO> detail(@PathVariable Long shareId) {
        ShareVO data = new ShareVO();
        data.setShareId(shareId);
        data.setShareToken("tk123456");
        data.setShareUrl("https://share.rocgit.com/tk123456");
        data.setViewCount(256);
        data.setExpiresAt(LocalDateTime.now().plusYears(1));
        data.setCreatedAt(LocalDateTime.now());
        return Result.success(data);
    }

    @Operation(summary = "更新分享权限", description = "更新分享链接的权限和有效期")
    @PutMapping("/{shareId}")
    public Result<Void> update(@PathVariable Long shareId, @Valid @RequestBody ShareUpdateRequest request) {
        return Result.success("更新成功", null);
    }

    @Operation(summary = "取消分享", description = "删除分享链接")
    @DeleteMapping("/{shareId}")
    public Result<Void> cancel(@PathVariable Long shareId) {
        return Result.success("已取消分享", null);
    }

    @Operation(summary = "重新生成分享链接", description = "旧链接失效，生成新的分享链接")
    @PostMapping("/{shareId}/regenerate")
    public Result<ShareVO> regenerate(@PathVariable Long shareId) {
        ShareVO data = new ShareVO();
        data.setShareToken("tk" + System.currentTimeMillis());
        data.setShareUrl("https://share.rocgit.com/tk" + System.currentTimeMillis());
        data.setExpiresAt(LocalDateTime.now().plusYears(1));
        return Result.success(data);
    }

    @Operation(summary = "分享统计", description = "分享链接的访问统计")
    @GetMapping("/{shareId}/stats")
    public Result<ShareStatsVO> stats(@PathVariable Long shareId) {
        ShareStatsVO data = new ShareStatsVO();
        data.setTotalViews(256);
        data.setUniqueVisitors(128);
        data.setDownloadCount(45);
        return Result.success(data);
    }

    @Operation(summary = "访问日志", description = "分享链接的访问日志")
    @GetMapping("/{shareId}/access-logs")
    public Result<Map<String, Object>> accessLogs(
            @PathVariable Long shareId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        Map<String, Object> data = new HashMap<>();
        data.put("total", 10);
        data.put("logs", new ArrayList<>());
        return Result.success(data);
    }

    @Operation(summary = "生成分享海报", description = "为分享链接生成分享海报图片")
    @PostMapping("/{shareId}/poster")
    public Result<Map<String, Object>> poster(@PathVariable Long shareId) {
        Map<String, Object> data = new HashMap<>();
        data.put("posterUrl", "https://cdn.rocgit.com/poster/" + shareId + ".png");
        return Result.success(data);
    }

    @Operation(summary = "校验分享令牌", description = "访客校验分享链接是否有效")
    @PostMapping("/validate")
    public Result<Map<String, Object>> validate(@Valid @RequestBody ShareValidateRequest request) {
        Map<String, Object> data = new HashMap<>();
        data.put("valid", true);
        data.put("projectName", "课程设计第十二组");
        data.put("permissionLevel", "flow_preview");
        return Result.success(data);
    }

    // ===== 访客无需鉴权接口 =====
    @Operation(summary = "访客查看分享", description = "通过分享令牌查看项目（无需登录）")
    @GetMapping("/{shareToken}")
    public Result<Map<String, Object>> viewByToken(@PathVariable String shareToken) {
        Map<String, Object> data = new HashMap<>();
        data.put("projectName", "课程设计第十二组");
        data.put("groupName", "软工一班第十二组");
        return Result.success(data);
    }

    @Operation(summary = "项目概览", description = "分享页的项目概览数据")
    @GetMapping("/{shareToken}/overview")
    public Result<Map<String, Object>> overview(@PathVariable String shareToken) {
        Map<String, Object> data = new HashMap<>();
        data.put("memberCount", 5);
        data.put("fileCount", 38);
        data.put("discussionCount", 12);
        data.put("decisionCount", 8);
        return Result.success(data);
    }

    @Operation(summary = "协作时间线", description = "项目的协作时间线")
    @GetMapping("/{shareToken}/timeline")
    public Result<List<Map<String, Object>>> timeline(@PathVariable String shareToken) {
        return Result.success(new ArrayList<>());
    }

    @Operation(summary = "文件列表", description = "分享页的文件列表")
    @GetMapping("/{shareToken}/files")
    public Result<Map<String, Object>> files(@PathVariable String shareToken) {
        Map<String, Object> data = new HashMap<>();
        data.put("total", 38);
        data.put("list", new ArrayList<>());
        return Result.success(data);
    }

    @Operation(summary = "文件预览", description = "分享页的文件预览")
    @GetMapping("/{shareToken}/file/{fileId}/preview")
    public Result<Map<String, Object>> filePreview(@PathVariable String shareToken, @PathVariable Long fileId) {
        Map<String, Object> data = new HashMap<>();
        data.put("previewUrl", "https://view.rocgit.com/preview/" + fileId);
        data.put("type", "pdf");
        return Result.success(data);
    }

    @Operation(summary = "文件下载", description = "分享页的文件下载")
    @GetMapping("/{shareToken}/file/{fileId}/download")
    public Result<Map<String, Object>> fileDownload(@PathVariable String shareToken, @PathVariable Long fileId) {
        Map<String, Object> data = new HashMap<>();
        data.put("downloadUrl", "https://cdn.rocgit.com/download/" + fileId);
        return Result.success(data);
    }

    @Operation(summary = "研讨列表", description = "分享页的研讨记录")
    @GetMapping("/{shareToken}/discussions")
    public Result<List<Map<String, Object>>> discussions(@PathVariable String shareToken) {
        return Result.success(new ArrayList<>());
    }

    @Operation(summary = "决策列表", description = "分享页的决策列表")
    @GetMapping("/{shareToken}/decisions")
    public Result<List<Map<String, Object>>> decisions(@PathVariable String shareToken) {
        return Result.success(new ArrayList<>());
    }
}
