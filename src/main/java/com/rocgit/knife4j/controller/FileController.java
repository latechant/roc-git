package com.rocgit.knife4j.controller;

import com.rocgit.knife4j.common.Result;
import com.rocgit.knife4j.dto.file.*;
import com.rocgit.knife4j.entity.file.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1/file")
@Tag(name = "05.文件", description = "文件上传、预览、审核等")
@SecurityRequirement(name = "Bearer")
public class FileController {

    @Operation(summary = "上传文件", description = "上传文件到项目指定阶段（multipart/form-data）")
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public Result<FileVO> upload(@RequestPart("file") MultipartFile file,
                                 @RequestParam(required = false) Long projectId,
                                 @RequestParam(required = false) Long stageId,
                                 @RequestParam(required = false) String remark) {
        FileVO data = new FileVO();
        data.setFileId(System.currentTimeMillis());
        data.setName(file.getOriginalFilename());
        data.setUrl("https://cdn.rocgit.com/files/" + file.getOriginalFilename());
        data.setSize(file.getSize());
        data.setType(getFileExt(file.getOriginalFilename()));
        data.setStatus("pending");
        data.setVersion(1);
        data.setProjectId(projectId);
        data.setStageId(stageId);
        data.setUploadedAt(LocalDateTime.now());
        FileVO.UploaderInfo u = new FileVO.UploaderInfo();
        u.setUserId(10001L);
        u.setNickname("张三");
        data.setUploader(u);
        return Result.success(data);
    }

    private String getFileExt(String name) {
        if (name == null) return "";
        int i = name.lastIndexOf('.');
        return i > 0 ? name.substring(i + 1) : "";
    }

    @Operation(summary = "文件列表", description = "分页查询项目下的文件，支持多条件筛选")
    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @Parameter(description = "项目ID", example = "1") @RequestParam Long projectId,
            @Parameter(description = "阶段ID", example = "2") @RequestParam(required = false) Long stageId,
            @Parameter(description = "文件类型", example = "docx") @RequestParam(required = false) String type,
            @Parameter(description = "上传者ID") @RequestParam(required = false) Long uploaderId,
            @Parameter(description = "文件名关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态", schema = @io.swagger.v3.oas.annotations.media.Schema(allowableValues = {"pending", "approved", "rejected"}))
            @RequestParam(required = false) String status,
            @Parameter(description = "开始日期 yyyy-MM-dd") @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期 yyyy-MM-dd") @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        Map<String, Object> data = new HashMap<>();
        data.put("total", 28);
        data.put("list", new ArrayList<>());
        return Result.success(data);
    }

    @Operation(summary = "文件详情", description = "获取文件元信息")
    @GetMapping("/{fileId}")
    public Result<FileVO> detail(@PathVariable Long fileId) {
        FileVO data = new FileVO();
        data.setFileId(fileId);
        data.setName("需求文档.docx");
        data.setUrl("https://cdn.rocgit.com/files/" + fileId);
        data.setSize(1024000L);
        data.setType("docx");
        data.setStatus("approved");
        data.setVersion(3);
        data.setProjectId(1L);
        data.setStageId(2L);
        data.setStageName("概要设计");
        FileVO.UploaderInfo u = new FileVO.UploaderInfo();
        u.setUserId(10001L);
        u.setNickname("张三");
        u.setAvatarUrl("https://cdn.rocgit.com/avatar/1.jpg");
        data.setUploader(u);
        data.setIsFavorite(false);
        data.setUploadedAt(LocalDateTime.now());
        return Result.success(data);
    }

    @Operation(summary = "删除文件", description = "删除文件（软删除）")
    @DeleteMapping("/{fileId}")
    public Result<Void> delete(@PathVariable Long fileId) {
        return Result.success("删除成功", null);
    }

    @Operation(summary = "文件预览", description = "获取文件预览地址（带水印）")
    @GetMapping("/{fileId}/preview")
    public Result<FilePreviewVO> preview(@PathVariable Long fileId) {
        FilePreviewVO data = new FilePreviewVO();
        data.setUrl("https://view.rocgit.com/preview/" + fileId);
        data.setType("pdf");
        data.setWatermark(true);
        return Result.success(data);
    }

    @Operation(summary = "文件下载", description = "获取文件下载链接")
    @GetMapping("/{fileId}/download")
    public Result<FileDownloadVO> download(@PathVariable Long fileId) {
        FileDownloadVO data = new FileDownloadVO();
        data.setDownloadUrl("https://cdn.rocgit.com/download/" + fileId);
        data.setExpiresAt(System.currentTimeMillis() + 3600_000);
        data.setFilename("file_" + fileId + ".pdf");
        return Result.success(data);
    }

    @Operation(summary = "移动文件", description = "将文件移到目标阶段")
    @PutMapping("/{fileId}/move")
    public Result<Void> move(@PathVariable Long fileId, @Valid @RequestBody MoveFileRequest request) {
        return Result.success("移动成功", null);
    }

    @Operation(summary = "重命名文件", description = "修改文件名称")
    @PutMapping("/{fileId}/rename")
    public Result<Void> rename(@PathVariable Long fileId, @Valid @RequestBody RenameFileRequest request) {
        return Result.success("重命名成功", null);
    }

    @Operation(summary = "收藏文件", description = "切换收藏状态（已收藏则取消，未收藏则添加）")
    @PostMapping("/{fileId}/favorite")
    public Result<Map<String, Object>> favorite(@PathVariable Long fileId) {
        Map<String, Object> data = new HashMap<>();
        data.put("isFavorite", true);
        return Result.success(data);
    }

    @Operation(summary = "审核通过", description = "审核通过挂单的文件")
    @PostMapping("/{fileId}/approve")
    public Result<Void> approve(@PathVariable Long fileId) {
        return Result.success("审核通过", null);
    }

    @Operation(summary = "审核驳回", description = "驳回挂单的文件，需提供原因")
    @PostMapping("/{fileId}/reject")
    public Result<Void> reject(@PathVariable Long fileId, @Valid @RequestBody RejectFileRequest request) {
        return Result.success("已驳回", null);
    }

    @Operation(summary = "重新提交", description = "被驳回后重新提交文件")
    @PostMapping(value = "/{fileId}/resubmit", consumes = "multipart/form-data")
    public Result<FileVO> resubmit(@PathVariable Long fileId,
                                   @RequestPart("file") MultipartFile file,
                                   @RequestParam(required = false) String remark) {
        FileVO data = new FileVO();
        data.setFileId(fileId);
        data.setVersion(4);
        data.setStatus("pending");
        return Result.success(data);
    }

    @Operation(summary = "审核日志", description = "文件的审核历史记录")
    @GetMapping("/{fileId}/audit-logs")
    public Result<Map<String, Object>> auditLogs(@PathVariable Long fileId) {
        AuditLogVO log = new AuditLogVO();
        log.setLogId(1L);
        log.setAction("approve");
        log.setReason(null);
        AuditLogVO.AuditorInfo a = new AuditLogVO.AuditorInfo();
        a.setUserId(10002L);
        a.setNickname("李四");
        log.setAuditor(a);
        log.setCreatedAt(LocalDateTime.now());
        Map<String, Object> data = new HashMap<>();
        data.put("total", 1);
        data.put("logs", Arrays.asList(log));
        return Result.success(data);
    }

    @Operation(summary = "下载记录", description = "文件的下载历史")
    @GetMapping("/{fileId}/download-records")
    public Result<List<DownloadRecordVO>> downloadRecords(@PathVariable Long fileId) {
        List<DownloadRecordVO> list = new ArrayList<>();
        DownloadRecordVO r = new DownloadRecordVO();
        r.setUserId(10003L);
        r.setNickname("王五");
        r.setIp("192.168.1.3");
        r.setDownloadedAt(LocalDateTime.now());
        r.setVersion(3);
        list.add(r);
        return Result.success(list);
    }

    @Operation(summary = "待审核列表", description = "当前用户的待审核文件列表")
    @GetMapping("/pending-audit")
    public Result<List<FileVO>> pendingAudit(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(new ArrayList<>());
    }

    @Operation(summary = "批量审核", description = "批量通过/驳回多个文件")
    @PostMapping("/batch-audit")
    public Result<Map<String, Object>> batchAudit(@Valid @RequestBody BatchAuditRequest request) {
        Map<String, Object> data = new HashMap<>();
        data.put("successCount", request.getFileIds().size());
        data.put("failCount", 0);
        return Result.success(data);
    }

    @Operation(summary = "批量移动", description = "批量移动文件到目标阶段")
    @PutMapping("/batch-move")
    public Result<Map<String, Object>> batchMove(@Valid @RequestBody BatchMoveRequest request) {
        Map<String, Object> data = new HashMap<>();
        data.put("successCount", request.getFileIds().size());
        return Result.success(data);
    }

    @Operation(summary = "批量删除", description = "批量删除文件")
    @DeleteMapping("/batch-delete")
    public Result<Map<String, Object>> batchDelete(@Valid @RequestBody BatchDeleteRequest request) {
        Map<String, Object> data = new HashMap<>();
        data.put("successCount", request.getFileIds().size());
        return Result.success(data);
    }

    @Operation(summary = "我的收藏", description = "当前用户收藏的文件列表")
    @GetMapping("/favorites")
    public Result<List<FileVO>> favorites(
            @Parameter(description = "项目ID") @RequestParam(required = false) Long projectId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(new ArrayList<>());
    }

    @Operation(summary = "我的上传", description = "当前用户上传的文件列表")
    @GetMapping("/my-uploads")
    public Result<List<FileVO>> myUploads(
            @RequestParam(required = false) Long projectId,
            @Parameter(schema = @io.swagger.v3.oas.annotations.media.Schema(allowableValues = {"pending", "approved", "rejected"}))
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(new ArrayList<>());
    }

    @Operation(summary = "类型统计", description = "项目下文件类型的分布统计")
    @GetMapping("/{projectId}/type-stats")
    public Result<FileTypeStatsVO> typeStats(@PathVariable Long projectId) {
        FileTypeStatsVO data = new FileTypeStatsVO();
        data.setDocument(10);
        data.setImage(8);
        data.setCode(6);
        data.setArchive(2);
        data.setOther(2);
        data.setTotal(28);
        return Result.success(data);
    }

    @Operation(summary = "导出文件清单", description = "导出项目所有文件清单为 Excel 或 CSV")
    @PostMapping("/{projectId}/export")
    public Result<Map<String, Object>> export(@PathVariable Long projectId, @RequestBody(required = false) ExportFileListRequest request) {
        Map<String, Object> data = new HashMap<>();
        data.put("exportUrl", "https://cdn.rocgit.com/export/" + projectId + "." + (request != null && request.getFormat() != null ? request.getFormat() : "xlsx"));
        return Result.success(data);
    }
}
