package com.rocgit.knife4j.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rocgit.knife4j.common.BusinessException;
import com.rocgit.knife4j.common.ResultCode;
import com.rocgit.knife4j.dto.file.BatchAuditRequest;
import com.rocgit.knife4j.entity.file.*;
import com.rocgit.knife4j.entity.version.FileVersionVO;
import com.rocgit.knife4j.entity.version.VersionCompareVO;
import com.rocgit.knife4j.mapper.FileMapper;
import com.rocgit.knife4j.mapper.FileReviewMapper;
import com.rocgit.knife4j.mapper.FileVersionMapper;
import com.rocgit.knife4j.mapper.StageMapper;
import com.rocgit.knife4j.mapper.UserMapper;
import com.rocgit.knife4j.model.File;
import com.rocgit.knife4j.model.FileReview;
import com.rocgit.knife4j.model.FileVersion;
import com.rocgit.knife4j.model.Stage;
import com.rocgit.knife4j.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 文件服务（含文件版本逻辑）- 接入真实 MySQL
 *
 * 课程项目无真实 OSS：文件只存元信息到 DB，url 为伪路径。
 * File 表逻辑删除由 MyBatis-Plus 全局配置自动处理，无需手动加 deleted=0。
 */
@Service
public class FileService {

    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private FileVersionMapper fileVersionMapper;
    @Autowired
    private FileReviewMapper fileReviewMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private StageMapper stageMapper;
    @Autowired
    private NotifyService notifyService;

    @Value("${rocgit.file.storage-dir:/opt/roc-git/files}")
    private String storageDir;

    // 文件类型分类集合（用于 type-stats）
    private static final Set<String> DOC_TYPES = new HashSet<>(Arrays.asList(
            "doc", "docx", "pdf", "txt", "md", "ppt", "pptx", "xls", "xlsx", "csv"));
    private static final Set<String> IMAGE_TYPES = new HashSet<>(Arrays.asList(
            "png", "jpg", "jpeg", "gif", "bmp", "svg", "webp"));
    private static final Set<String> CODE_TYPES = new HashSet<>(Arrays.asList(
            "java", "py", "js", "ts", "html", "css", "c", "cpp", "h", "go", "sh", "json", "xml", "sql", "vue"));
    private static final Set<String> ARCHIVE_TYPES = new HashSet<>(Arrays.asList(
            "zip", "rar", "7z", "tar", "gz", "jar"));

    // ===================== 文件上传 / 详情 =====================

    /**
     * 上传文件：插入 file + file_version(version=1)
     */
    @Transactional
    public FileVO upload(MultipartFile file, Long projectId, Long stageId, Long uploaderId, String remark) {
        String originalName = file.getOriginalFilename();
        String ext = getExt(originalName);
        String uuidDir = UUID.randomUUID().toString().replace("-", "");
        String path = "/files/" + uuidDir + "/" + originalName;

        // 物理写盘
        saveToDisk(file, uuidDir, originalName);

        File f = new File();
        f.setProjectId(projectId);
        f.setStageId(stageId);
        f.setName(originalName);
        f.setUrl(path);
        f.setSize(file.getSize());
        f.setType(ext);
        f.setUploaderId(uploaderId);
        f.setStatus("pending");
        f.setVersion(1);
        f.setIsLatest(1);
        f.setIsFavorite(0);
        fileMapper.insert(f);

        FileVersion v = new FileVersion();
        v.setFileId(f.getId());
        v.setVersion(1);
        v.setUrl(path);
        v.setSize(file.getSize());
        v.setUploaderId(uploaderId);
        v.setComment(remark);
        fileVersionMapper.insert(v);

        notifyService.logOperation(projectId, uploaderId, "upload", "file", f.getId(), originalName, "上传文件 v1");
        return toVO(fileMapper.selectById(f.getId()), new HashMap<>(), new HashMap<>());
    }

    /**
     * 多条件分页查询文件列表
     */
    public Map<String, Object> list(Long projectId, Long stageId, String type, Long uploaderId,
                                     String keyword, String status, String startDate, String endDate,
                                     Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<File> wrapper = buildListWrapper(projectId, stageId, type, uploaderId,
                keyword, status, startDate, endDate);

        long total = fileMapper.selectCount(wrapper);

        int pn = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int ps = pageSize == null || pageSize < 1 ? 20 : pageSize;
        int offset = (pn - 1) * ps;
        wrapper.last("LIMIT " + offset + "," + ps);
        List<File> files = fileMapper.selectList(wrapper);

        Map<Long, User> userCache = new HashMap<>();
        Map<Long, Stage> stageCache = new HashMap<>();
        List<FileVO> list = new ArrayList<>();
        for (File f : files) {
            list.add(toVO(f, userCache, stageCache));
        }

        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("list", list);
        return data;
    }

    /**
     * 文件详情
     */
    public FileVO detail(Long fileId) {
        File f = mustGetFile(fileId);
        return toVO(f, new HashMap<>(), new HashMap<>());
    }

    /**
     * 逻辑删除（deleteById 由全局逻辑删除配置自动置 deleted=1）
     */
    public void delete(Long fileId) {
        mustGetFile(fileId);
        fileMapper.deleteById(fileId);
    }

    /**
     * 预览
     */
    public FilePreviewVO preview(Long fileId) {
        File f = mustGetFile(fileId);
        FilePreviewVO vo = new FilePreviewVO();
        vo.setUrl(f.getUrl());
        vo.setType(f.getType());
        vo.setWatermark(true);
        return vo;
    }

    /**
     * 下载
     */
    public FileDownloadVO download(Long fileId) {
        File f = mustGetFile(fileId);
        FileDownloadVO vo = new FileDownloadVO();
        vo.setDownloadUrl(f.getUrl());
        vo.setFilename(f.getName());
        vo.setExpiresAt(System.currentTimeMillis() + 3600_000L);
        return vo;
    }

    /**
     * 移动阶段
     */
    public void move(Long fileId, Long targetStageId) {
        File f = mustGetFile(fileId);
        f.setStageId(targetStageId);
        fileMapper.updateById(f);
    }

    /**
     * 重命名
     */
    public void rename(Long fileId, String name) {
        File f = mustGetFile(fileId);
        f.setName(name);
        fileMapper.updateById(f);
    }

    /**
     * 切换收藏，切换后返回是否已收藏
     */
    public boolean toggleFavorite(Long fileId) {
        File f = mustGetFile(fileId);
        int cur = f.getIsFavorite() == null ? 0 : f.getIsFavorite();
        int next = cur == 1 ? 0 : 1;
        f.setIsFavorite(next);
        fileMapper.updateById(f);
        return next == 1;
    }

    // ===================== 审核 =====================

    /**
     * 审核通过
     */
    @Transactional
    public void approve(Long fileId, Long reviewerId) {
        File f = mustGetFile(fileId);
        f.setStatus("approved");
        f.setRejectReason(null);
        fileMapper.updateById(f);

        FileReview r = new FileReview();
        r.setFileId(fileId);
        r.setReviewerId(reviewerId);
        r.setAction("approved");
        r.setReviewedAt(LocalDateTime.now());
        fileReviewMapper.insert(r);

        notifyService.sendMessage(f.getUploaderId(), f.getProjectId(), "audit", "文件审核通过",
                "您的文件「" + f.getName() + "」已审核通过");
        notifyService.logOperation(f.getProjectId(), reviewerId, "approve", "file", fileId, f.getName(), "审核通过");
    }

    /**
     * 审核驳回
     */
    @Transactional
    public void reject(Long fileId, Long reviewerId, String reason) {
        File f = mustGetFile(fileId);
        f.setStatus("rejected");
        f.setRejectReason(reason);
        fileMapper.updateById(f);

        FileReview r = new FileReview();
        r.setFileId(fileId);
        r.setReviewerId(reviewerId);
        r.setAction("rejected");
        r.setReason(reason);
        r.setReviewedAt(LocalDateTime.now());
        fileReviewMapper.insert(r);

        notifyService.sendMessage(f.getUploaderId(), f.getProjectId(), "audit", "文件审核驳回",
                "您的文件「" + f.getName() + "」被驳回，原因：" + reason);
        notifyService.logOperation(f.getProjectId(), reviewerId, "reject", "file", fileId, f.getName(), "审核驳回：" + reason);
    }

    /**
     * 重新提交：version+1，更新 file 元信息，旧版本快照随新版本插入
     */
    @Transactional
    public FileVO resubmit(Long fileId, MultipartFile file, Long uploaderId, String remark) {
        File f = mustGetFile(fileId);
        String originalName = file.getOriginalFilename();
        String ext = getExt(originalName);
        String uuidDir = UUID.randomUUID().toString().replace("-", "");
        String path = "/files/" + uuidDir + "/" + originalName;

        // 物理写盘
        saveToDisk(file, uuidDir, originalName);

        int newVersion = (f.getVersion() == null ? 1 : f.getVersion()) + 1;
        f.setUrl(path);
        f.setSize(file.getSize());
        f.setType(ext);
        f.setStatus("pending");
        f.setIsLatest(1);
        f.setVersion(newVersion);
        f.setRejectReason(null);
        fileMapper.updateById(f);

        FileVersion v = new FileVersion();
        v.setFileId(fileId);
        v.setVersion(newVersion);
        v.setUrl(path);
        v.setSize(file.getSize());
        v.setUploaderId(uploaderId);
        v.setComment(remark);
        fileVersionMapper.insert(v);

        notifyService.logOperation(f.getProjectId(), uploaderId, "resubmit", "file", fileId, originalName, "重新提交 v" + newVersion);
        return toVO(fileMapper.selectById(fileId), new HashMap<>(), new HashMap<>());
    }

    /**
     * 审核日志
     */
    public Map<String, Object> auditLogs(Long fileId) {
        List<FileReview> reviews = fileReviewMapper.selectList(
                new LambdaQueryWrapper<FileReview>()
                        .eq(FileReview::getFileId, fileId)
                        .orderByDesc(FileReview::getReviewedAt));

        Map<Long, User> userCache = new HashMap<>();
        List<AuditLogVO> logs = new ArrayList<>();
        for (FileReview r : reviews) {
            AuditLogVO vo = new AuditLogVO();
            vo.setLogId(r.getId());
            vo.setAction(r.getAction());
            vo.setReason(r.getReason());
            AuditLogVO.AuditorInfo auditor = new AuditLogVO.AuditorInfo();
            if (r.getReviewerId() != null) {
                User u = userCache.computeIfAbsent(r.getReviewerId(), k -> userMapper.selectById(k));
                if (u != null) {
                    auditor.setUserId(u.getId());
                    auditor.setNickname(u.getNickname());
                    auditor.setAvatarUrl(u.getAvatarUrl());
                }
            }
            vo.setAuditor(auditor);
            vo.setCreatedAt(r.getReviewedAt());
            logs.add(vo);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("total", logs.size());
        data.put("logs", logs);
        return data;
    }

    /**
     * 下载记录：schema 无 download_record 表，返回空列表
     */
    public List<DownloadRecordVO> downloadRecords(Long fileId) {
        return new ArrayList<>();
    }

    // ===================== 审核 / 收藏 / 我的 =====================

    /**
     * 待审核文件（status=pending，分页）
     */
    public Map<String, Object> pendingAudit(Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<File> wrapper = new LambdaQueryWrapper<File>()
                .eq(File::getStatus, "pending")
                .orderByDesc(File::getCreatedAt);
        return pageQuery(wrapper, pageNum, pageSize);
    }

    /**
     * 批量审核
     */
    @Transactional
    public Map<String, Object> batchAudit(BatchAuditRequest request, Long reviewerId) {
        boolean approve = "approve".equalsIgnoreCase(request.getAction());
        String newStatus = approve ? "approved" : "rejected";
        String action = approve ? "approved" : "rejected";

        int success = 0;
        int fail = 0;
        for (Long fileId : request.getFileIds()) {
            File f = fileMapper.selectById(fileId);
            if (f == null) {
                fail++;
                continue;
            }
            f.setStatus(newStatus);
            if (approve) {
                f.setRejectReason(null);
            } else {
                f.setRejectReason(request.getReason());
            }
            fileMapper.updateById(f);

            FileReview r = new FileReview();
            r.setFileId(fileId);
            r.setReviewerId(reviewerId);
            r.setAction(action);
            r.setReason(request.getReason());
            r.setReviewedAt(LocalDateTime.now());
            fileReviewMapper.insert(r);

            notifyService.sendMessage(f.getUploaderId(), f.getProjectId(), "audit",
                    approve ? "文件审核通过" : "文件审核驳回",
                    "您的文件「" + f.getName() + "」" + (approve ? "已审核通过" : "被驳回，原因：" + request.getReason()));
            notifyService.logOperation(f.getProjectId(), reviewerId, action, "file", fileId, f.getName(),
                    approve ? "批量审核通过" : "批量审核驳回：" + request.getReason());
            success++;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("successCount", success);
        data.put("failCount", fail);
        return data;
    }

    /**
     * 批量移动阶段
     */
    @Transactional
    public Map<String, Object> batchMove(List<Long> fileIds, Long targetStageId) {
        int success = 0;
        for (Long fileId : fileIds) {
            File f = fileMapper.selectById(fileId);
            if (f == null) {
                continue;
            }
            f.setStageId(targetStageId);
            fileMapper.updateById(f);
            success++;
        }
        Map<String, Object> data = new HashMap<>();
        data.put("successCount", success);
        return data;
    }

    /**
     * 批量逻辑删除
     */
    @Transactional
    public Map<String, Object> batchDelete(List<Long> fileIds) {
        int success = 0;
        for (Long fileId : fileIds) {
            if (fileMapper.selectById(fileId) == null) {
                continue;
            }
            fileMapper.deleteById(fileId);
            success++;
        }
        Map<String, Object> data = new HashMap<>();
        data.put("successCount", success);
        return data;
    }

    /**
     * 当前用户收藏的文件
     */
    public Map<String, Object> favorites(Long userId, Long projectId, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<File> wrapper = new LambdaQueryWrapper<File>()
                .eq(File::getIsFavorite, 1)
                .eq(projectId != null, File::getProjectId, projectId)
                .orderByDesc(File::getCreatedAt);
        return pageQuery(wrapper, pageNum, pageSize);
    }

    /**
     * 当前用户上传的文件（可按 status 过滤）
     */
    public Map<String, Object> myUploads(Long userId, Long projectId, String status,
                                        Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<File> wrapper = new LambdaQueryWrapper<File>()
                .eq(File::getUploaderId, userId)
                .eq(projectId != null, File::getProjectId, projectId)
                .eq(status != null && !status.isEmpty(), File::getStatus, status)
                .orderByDesc(File::getCreatedAt);
        return pageQuery(wrapper, pageNum, pageSize);
    }

    /**
     * 项目下文件类型分布统计
     */
    public FileTypeStatsVO typeStats(Long projectId) {
        List<File> files = fileMapper.selectList(
                new LambdaQueryWrapper<File>().eq(File::getProjectId, projectId));

        int document = 0, image = 0, code = 0, archive = 0, other = 0;
        for (File f : files) {
            String t = f.getType() == null ? "" : f.getType().toLowerCase();
            if (DOC_TYPES.contains(t)) {
                document++;
            } else if (IMAGE_TYPES.contains(t)) {
                image++;
            } else if (CODE_TYPES.contains(t)) {
                code++;
            } else if (ARCHIVE_TYPES.contains(t)) {
                archive++;
            } else {
                other++;
            }
        }

        FileTypeStatsVO vo = new FileTypeStatsVO();
        vo.setDocument(document);
        vo.setImage(image);
        vo.setCode(code);
        vo.setArchive(archive);
        vo.setOther(other);
        vo.setTotal(files.size());
        return vo;
    }

    /**
     * 导出文件清单（演示，返回伪 URL）
     */
    public String exportUrl(Long projectId, String format) {
        String fmt = (format == null || format.isEmpty()) ? "xlsx" : format;
        return "/files/export/" + projectId + "." + fmt;
    }

    // ===================== 版本 =====================

    /**
     * 文件版本列表
     */
    public List<FileVersionVO> versionList(Long fileId) {
        List<FileVersion> versions = fileVersionMapper.selectList(
                new LambdaQueryWrapper<FileVersion>()
                        .eq(FileVersion::getFileId, fileId)
                        .orderByDesc(FileVersion::getVersion));

        Map<Long, User> userCache = new HashMap<>();
        List<FileVersionVO> list = new ArrayList<>();
        for (FileVersion v : versions) {
            FileVersionVO vo = new FileVersionVO();
            vo.setVersion(v.getVersion());
            vo.setComment(v.getComment());
            vo.setUploaderId(v.getUploaderId());
            if (v.getUploaderId() != null) {
                User u = userCache.computeIfAbsent(v.getUploaderId(), k -> userMapper.selectById(k));
                if (u != null) {
                    vo.setUploaderName(u.getNickname());
                }
            }
            vo.setSize(v.getSize());
            vo.setCreatedAt(v.getCreatedAt());
            vo.setUrl(v.getUrl());
            list.add(vo);
        }
        return list;
    }

    /**
     * 版本对比：diffUrl 为伪 URL，additions/deletions 用 size 差估算
     */
    public VersionCompareVO compare(Long fileId, Integer fromVersion, Integer toVersion) {
        FileVersion from = fileVersionMapper.selectOne(
                new LambdaQueryWrapper<FileVersion>()
                        .eq(FileVersion::getFileId, fileId)
                        .eq(FileVersion::getVersion, fromVersion));
        FileVersion to = fileVersionMapper.selectOne(
                new LambdaQueryWrapper<FileVersion>()
                        .eq(FileVersion::getFileId, fileId)
                        .eq(FileVersion::getVersion, toVersion));

        long fromSize = from == null || from.getSize() == null ? 0L : from.getSize();
        long toSize = to == null || to.getSize() == null ? 0L : to.getSize();
        long diff = toSize - fromSize;
        int additions = diff > 0 ? (int) diff : 0;
        int deletions = diff < 0 ? (int) -diff : 0;

        VersionCompareVO vo = new VersionCompareVO();
        vo.setDiffUrl("/files/diff/" + fileId + "/" + fromVersion + "_" + toVersion);
        vo.setAdditions(additions);
        vo.setDeletions(deletions);
        return vo;
    }

    /**
     * 版本回滚：将 file 回滚到 targetVersion 的 url/size，并产生新版本记录
     */
    @Transactional
    public Map<String, Object> rollback(Long fileId, Integer targetVersion, Long operatorId) {
        File f = mustGetFile(fileId);
        FileVersion target = fileVersionMapper.selectOne(
                new LambdaQueryWrapper<FileVersion>()
                        .eq(FileVersion::getFileId, fileId)
                        .eq(FileVersion::getVersion, targetVersion));
        if (target == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "目标版本不存在");
        }

        int newVersion = (f.getVersion() == null ? 1 : f.getVersion()) + 1;
        f.setUrl(target.getUrl());
        f.setSize(target.getSize());
        f.setVersion(newVersion);
        f.setIsLatest(1);
        f.setStatus("approved");
        fileMapper.updateById(f);

        FileVersion nv = new FileVersion();
        nv.setFileId(fileId);
        nv.setVersion(newVersion);
        nv.setUrl(target.getUrl());
        nv.setSize(target.getSize());
        nv.setUploaderId(operatorId);
        nv.setComment("回滚到 v" + targetVersion);
        fileVersionMapper.insert(nv);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("newVersion", newVersion);
        data.put("rollbackToVersion", targetVersion);
        return data;
    }

    // ===================== 私有辅助 =====================

    /**
     * 将上传文件物理写入磁盘：{storageDir}/{uuidDir}/{originalName}
     */
    private void saveToDisk(MultipartFile file, String uuidDir, String originalName) {
        java.io.File dir = new java.io.File(storageDir, uuidDir);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new BusinessException(ResultCode.FAIL, "创建文件目录失败: " + dir.getAbsolutePath());
        }
        java.io.File dest = new java.io.File(dir, originalName);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new BusinessException(ResultCode.FAIL, "文件写入磁盘失败: " + e.getMessage());
        }
    }

    private File mustGetFile(Long fileId) {
        File f = fileMapper.selectById(fileId);
        if (f == null) {
            throw new BusinessException(ResultCode.FILE_NOT_FOUND);
        }
        return f;
    }

    /**
     * 多条件分页查询，返回 {total, list:List<FileVO>}
     */
    private Map<String, Object> pageQuery(LambdaQueryWrapper<File> wrapper, Integer pageNum, Integer pageSize) {
        long total = fileMapper.selectCount(wrapper);

        int pn = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int ps = pageSize == null || pageSize < 1 ? 20 : pageSize;
        int offset = (pn - 1) * ps;
        wrapper.last("LIMIT " + offset + "," + ps);
        List<File> files = fileMapper.selectList(wrapper);

        Map<Long, User> userCache = new HashMap<>();
        Map<Long, Stage> stageCache = new HashMap<>();
        List<FileVO> list = new ArrayList<>();
        for (File f : files) {
            list.add(toVO(f, userCache, stageCache));
        }

        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("list", list);
        return data;
    }

    private LambdaQueryWrapper<File> buildListWrapper(Long projectId, Long stageId, String type, Long uploaderId,
                                                       String keyword, String status, String startDate, String endDate) {
        LambdaQueryWrapper<File> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(projectId != null, File::getProjectId, projectId);
        wrapper.eq(stageId != null, File::getStageId, stageId);
        wrapper.eq(type != null && !type.isEmpty(), File::getType, type);
        wrapper.eq(uploaderId != null, File::getUploaderId, uploaderId);
        wrapper.like(keyword != null && !keyword.isEmpty(), File::getName, keyword);
        wrapper.eq(status != null && !status.isEmpty(), File::getStatus, status);
        if (startDate != null && !startDate.isEmpty()) {
            try {
                wrapper.ge(File::getCreatedAt, LocalDate.parse(startDate).atStartOfDay());
            } catch (Exception ignored) {
            }
        }
        if (endDate != null && !endDate.isEmpty()) {
            try {
                wrapper.le(File::getCreatedAt, LocalDate.parse(endDate).atTime(23, 59, 59));
            } catch (Exception ignored) {
            }
        }
        wrapper.orderByDesc(File::getCreatedAt);
        return wrapper;
    }

    private FileVO toVO(File f, Map<Long, User> userCache, Map<Long, Stage> stageCache) {
        FileVO vo = new FileVO();
        vo.setFileId(f.getId());
        vo.setName(f.getName());
        vo.setUrl(f.getUrl());
        vo.setSize(f.getSize());
        vo.setType(f.getType());
        vo.setStatus(f.getStatus());
        vo.setVersion(f.getVersion());
        vo.setProjectId(f.getProjectId());
        vo.setStageId(f.getStageId());
        vo.setIsFavorite(f.getIsFavorite() != null && f.getIsFavorite() == 1);
        vo.setUploadedAt(f.getCreatedAt());
        vo.setUpdatedAt(f.getUpdatedAt());

        if (f.getUploaderId() != null) {
            User u = userCache.computeIfAbsent(f.getUploaderId(), k -> userMapper.selectById(k));
            if (u != null) {
                FileVO.UploaderInfo uploader = new FileVO.UploaderInfo();
                uploader.setUserId(u.getId());
                uploader.setNickname(u.getNickname());
                uploader.setAvatarUrl(u.getAvatarUrl());
                vo.setUploader(uploader);
            }
        }
        if (f.getStageId() != null) {
            Stage stage = stageCache.computeIfAbsent(f.getStageId(), k -> stageMapper.selectById(k));
            if (stage != null) {
                vo.setStageName(stage.getName());
            }
        }
        return vo;
    }

    private String getExt(String name) {
        if (name == null) {
            return "";
        }
        int i = name.lastIndexOf('.');
        return i > 0 ? name.substring(i + 1) : "";
    }
}
