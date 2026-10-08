package com.rocgit.knife4j.controller;

import com.rocgit.knife4j.common.Result;
import com.rocgit.knife4j.dto.file.*;
import com.rocgit.knife4j.entity.file.*;
import com.rocgit.knife4j.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.*;

/**
 * 文件模块 Controller - 接入真实 MySQL
 *
 * 课程项目无真实 OSS：上传只把文件元信息写入 DB，url 为伪路径。
 */
@RestController
@RequestMapping("/api/v1/file")
@Tag(name = "05.文件", description = "文件上传、预览、审核等")
@SecurityRequirement(name = "Bearer")
public class FileController {

    @Autowired
    private FileService fileService;

    @Operation(summary = "上传文件", description = "上传文件到项目指定阶段（multipart/form-data），文件元信息落库")
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public Result<FileVO> upload(@RequestAttribute("userId") String userId,
                                 @RequestPart("file") MultipartFile file,
                                 @RequestParam(required = false) Long projectId,
                                 @RequestParam(required = false) Long stageId,
                                 @RequestParam(required = false) String remark) {
        FileVO data = fileService.upload(file, projectId, stageId, Long.parseLong(userId), remark);
        return Result.success(data);
    }

    @Operation(summary = "文件列表", description = "分页查询项目下的文件，支持多条件筛选")
    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @Parameter(description = "项目ID", example = "1") @RequestParam(required = false) Long projectId,
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
        Map<String, Object> data = fileService.list(projectId, stageId, type, uploaderId, keyword, status,
                startDate, endDate, pageNum, pageSize);
        return Result.success(data);
    }

    @Operation(summary = "文件详情", description = "获取文件元信息")
    @GetMapping("/{fileId}")
    public Result<FileVO> detail(@PathVariable Long fileId) {
        return Result.success(fileService.detail(fileId));
    }

    @Operation(summary = "删除文件", description = "删除文件（软删除）")
    @DeleteMapping("/{fileId}")
    public Result<Void> delete(@PathVariable Long fileId) {
        fileService.delete(fileId);
        return Result.success("删除成功", null);
    }

    @Operation(summary = "文件预览", description = "获取文件预览地址（带水印）")
    @GetMapping("/{fileId}/preview")
    public Result<FilePreviewVO> preview(@PathVariable Long fileId) {
        return Result.success(fileService.preview(fileId));
    }

    @Operation(summary = "文件下载", description = "获取文件下载链接")
    @GetMapping("/{fileId}/download")
    public Result<FileDownloadVO> download(@PathVariable Long fileId) {
        return Result.success(fileService.download(fileId));
    }

    @Operation(summary = "移动文件", description = "将文件移到目标阶段")
    @PutMapping("/{fileId}/move")
    public Result<Void> move(@PathVariable Long fileId, @Valid @RequestBody MoveFileRequest request) {
        fileService.move(fileId, request.getTargetStageId());
        return Result.success("移动成功", null);
    }

    @Operation(summary = "重命名文件", description = "修改文件名称")
    @PutMapping("/{fileId}/rename")
    public Result<Void> rename(@PathVariable Long fileId, @Valid @RequestBody RenameFileRequest request) {
        fileService.rename(fileId, request.getName());
        return Result.success("重命名成功", null);
    }

    @Operation(summary = "收藏文件", description = "切换收藏状态（已收藏则取消，未收藏则添加）")
    @PostMapping("/{fileId}/favorite")
    public Result<Map<String, Object>> favorite(@PathVariable Long fileId) {
        boolean isFavorite = fileService.toggleFavorite(fileId);
        Map<String, Object> data = new HashMap<>();
        data.put("isFavorite", isFavorite);
        return Result.success(data);
    }

    @Operation(summary = "审核通过", description = "审核通过挂单的文件")
    @PostMapping("/{fileId}/approve")
    public Result<Void> approve(@RequestAttribute("userId") String userId, @PathVariable Long fileId) {
        fileService.approve(fileId, Long.parseLong(userId));
        return Result.success("审核通过", null);
    }

    @Operation(summary = "审核驳回", description = "驳回挂单的文件，需提供原因")
    @PostMapping("/{fileId}/reject")
    public Result<Void> reject(@RequestAttribute("userId") String userId, @PathVariable Long fileId,
                               @Valid @RequestBody RejectFileRequest request) {
        fileService.reject(fileId, Long.parseLong(userId), request.getReason());
        return Result.success("已驳回", null);
    }

    @Operation(summary = "重新提交", description = "被驳回后重新提交文件")
    @PostMapping(value = "/{fileId}/resubmit", consumes = "multipart/form-data")
    public Result<FileVO> resubmit(@RequestAttribute("userId") String userId,
                                   @PathVariable Long fileId,
                                   @RequestPart("file") MultipartFile file,
                                   @RequestParam(required = false) String remark) {
        FileVO data = fileService.resubmit(fileId, file, Long.parseLong(userId), remark);
        return Result.success(data);
    }

    @Operation(summary = "审核日志", description = "文件的审核历史记录")
    @GetMapping("/{fileId}/audit-logs")
    public Result<Map<String, Object>> auditLogs(@PathVariable Long fileId) {
        return Result.success(fileService.auditLogs(fileId));
    }

    @Operation(summary = "下载记录", description = "文件的下载历史")
    @GetMapping("/{fileId}/download-records")
    public Result<List<DownloadRecordVO>> downloadRecords(@PathVariable Long fileId) {
        return Result.success(fileService.downloadRecords(fileId));
    }

    @Operation(summary = "待审核列表", description = "当前用户的待审核文件列表")
    @GetMapping("/pending-audit")
    public Result<Map<String, Object>> pendingAudit(
            @RequestAttribute("userId") String userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(fileService.pendingAudit(Long.parseLong(userId), pageNum, pageSize));
    }

    @Operation(summary = "批量审核", description = "批量通过/驳回多个文件")
    @PostMapping("/batch-audit")
    public Result<Map<String, Object>> batchAudit(@RequestAttribute("userId") String userId,
                                                  @Valid @RequestBody BatchAuditRequest request) {
        return Result.success(fileService.batchAudit(request, Long.parseLong(userId)));
    }

    @Operation(summary = "批量移动", description = "批量移动文件到目标阶段")
    @PutMapping("/batch-move")
    public Result<Map<String, Object>> batchMove(@Valid @RequestBody BatchMoveRequest request) {
        return Result.success(fileService.batchMove(request.getFileIds(), request.getTargetStageId()));
    }

    @Operation(summary = "批量删除", description = "批量删除文件")
    @DeleteMapping("/batch-delete")
    public Result<Map<String, Object>> batchDelete(@Valid @RequestBody BatchDeleteRequest request) {
        return Result.success(fileService.batchDelete(request.getFileIds()));
    }

    @Operation(summary = "我的收藏", description = "当前用户收藏的文件列表")
    @GetMapping("/favorites")
    public Result<Map<String, Object>> favorites(
            @RequestAttribute("userId") String userId,
            @Parameter(description = "项目ID") @RequestParam(required = false) Long projectId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(fileService.favorites(Long.parseLong(userId), projectId, pageNum, pageSize));
    }

    @Operation(summary = "我的上传", description = "当前用户上传的文件列表")
    @GetMapping("/my-uploads")
    public Result<Map<String, Object>> myUploads(
            @RequestAttribute("userId") String userId,
            @RequestParam(required = false) Long projectId,
            @Parameter(schema = @io.swagger.v3.oas.annotations.media.Schema(allowableValues = {"pending", "approved", "rejected"}))
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(fileService.myUploads(Long.parseLong(userId), projectId, status, pageNum, pageSize));
    }

    @Operation(summary = "类型统计", description = "项目下文件类型的分布统计")
    @GetMapping("/{projectId}/type-stats")
    public Result<FileTypeStatsVO> typeStats(@PathVariable Long projectId) {
        return Result.success(fileService.typeStats(projectId));
    }

    @Operation(summary = "导出文件清单", description = "导出项目所有文件清单为 Excel 或 CSV")
    @PostMapping("/{projectId}/export")
    public Result<Map<String, Object>> export(@PathVariable Long projectId,
                                             @RequestBody(required = false) ExportFileListRequest request) {
        String format = request != null && request.getFormat() != null ? request.getFormat() : "xlsx";
        Map<String, Object> data = new HashMap<>();
        data.put("exportUrl", fileService.exportUrl(projectId, format));
        return Result.success(data);
    }
}
