package com.rocgit.knife4j.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rocgit.knife4j.common.BusinessException;
import com.rocgit.knife4j.common.ResultCode;
import com.rocgit.knife4j.dto.share.ShareCreateRequest;
import com.rocgit.knife4j.dto.share.ShareUpdateRequest;
import com.rocgit.knife4j.dto.share.ShareValidateRequest;
import com.rocgit.knife4j.entity.share.AccessLogVO;
import com.rocgit.knife4j.entity.share.ShareStatsVO;
import com.rocgit.knife4j.entity.share.ShareVO;
import com.rocgit.knife4j.mapper.*;
import com.rocgit.knife4j.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 结项分享服务
 */
@Service
public class ShareService {

    @Autowired
    private ShareMapper shareMapper;
    @Autowired
    private ShareAccessLogMapper shareAccessLogMapper;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private GroupMemberMapper groupMemberMapper;
    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private DiscussionMapper discussionMapper;
    @Autowired
    private DecisionMapper decisionMapper;
    @Autowired
    private OperationLogMapper operationLogMapper;
    @Autowired
    private UserMapper userMapper;

    private static final String SHARE_BASE_URL = "https://share.rocgit.com/";
    private static final String QR_BASE_URL = "https://cdn.rocgit.com/qr/";

    /** 生成 12 位随机 share_token，并保证唯一 */
    private String generateUniqueToken() {
        String token;
        do {
            token = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        } while (shareMapper.selectCount(new LambdaQueryWrapper<Share>().eq(Share::getShareToken, token)) > 0);
        return token;
    }

    private ShareVO toVO(Share share) {
        ShareVO vo = new ShareVO();
        vo.setShareId(share.getId());
        vo.setShareToken(share.getShareToken());
        vo.setShareUrl(share.getShareUrl());
        vo.setQrCodeUrl(share.getQrCodeUrl());
        vo.setPermissionLevel(share.getPermissionLevel());
        vo.setViewCount(share.getViewCount() == null ? 0 : share.getViewCount());
        vo.setExpiresAt(share.getExpiresAt());
        vo.setCreatedAt(share.getCreatedAt());
        vo.setNeedPassword(share.getNeedPassword() != null && share.getNeedPassword() == 1);
        return vo;
    }

    private Share requireShare(Long shareId) {
        Share share = shareMapper.selectById(shareId);
        if (share == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "分享不存在");
        }
        return share;
    }

    /** 访客按 token 解析分享，校验存在性与过期时间 */
    private Share resolveShareByToken(String shareToken) {
        Share share = shareMapper.selectOne(
                new LambdaQueryWrapper<Share>().eq(Share::getShareToken, shareToken));
        if (share == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "分享不存在");
        }
        if (share.getExpiresAt() != null && share.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ResultCode.SHARE_LINK_EXPIRED);
        }
        return share;
    }

    // ================= 管理端（需鉴权） =================

    @Transactional
    public ShareVO create(ShareCreateRequest request) {
        String token = generateUniqueToken();
        Share share = new Share();
        share.setProjectId(request.getProjectId());
        share.setShareToken(token);
        share.setShareUrl(SHARE_BASE_URL + token);
        share.setQrCodeUrl(QR_BASE_URL + token + ".png");
        share.setPermissionLevel(request.getPermissionLevel());
        share.setViewCount(0);
        share.setNeedPassword(Boolean.TRUE.equals(request.getNeedPassword()) ? 1 : 0);
        share.setExpiresAt(request.getExpiresAt());
        share.setCreatedAt(LocalDateTime.now());
        shareMapper.insert(share);
        return toVO(share);
    }

    public List<ShareVO> listByProject(Long projectId) {
        List<Share> list = shareMapper.selectList(
                new LambdaQueryWrapper<Share>()
                        .eq(Share::getProjectId, projectId)
                        .orderByDesc(Share::getCreatedAt));
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    public ShareVO detail(Long shareId) {
        return toVO(requireShare(shareId));
    }

    @Transactional
    public void update(Long shareId, ShareUpdateRequest request) {
        Share share = requireShare(shareId);
        share.setPermissionLevel(request.getPermissionLevel());
        if (request.getExpiresAt() != null) {
            share.setExpiresAt(request.getExpiresAt());
        }
        shareMapper.updateById(share);
    }

    @Transactional
    public void delete(Long shareId) {
        requireShare(shareId);
        shareMapper.deleteById(shareId);
    }

    @Transactional
    public ShareVO regenerate(Long shareId) {
        Share share = requireShare(shareId);
        String token = generateUniqueToken();
        share.setShareToken(token);
        share.setShareUrl(SHARE_BASE_URL + token);
        share.setQrCodeUrl(QR_BASE_URL + token + ".png");
        shareMapper.updateById(share);
        return toVO(share);
    }

    public ShareStatsVO stats(Long shareId) {
        Share share = requireShare(shareId);

        Long uniqueCount = shareAccessLogMapper.selectObjs(
                new QueryWrapper<ShareAccessLog>().select("DISTINCT visitor_id")
                        .eq("share_id", shareId)).stream().count();
        Long downloadCount = shareAccessLogMapper.selectCount(
                new LambdaQueryWrapper<ShareAccessLog>()
                        .eq(ShareAccessLog::getShareId, shareId)
                        .eq(ShareAccessLog::getAction, "download"));

        ShareStatsVO vo = new ShareStatsVO();
        vo.setTotalViews(share.getViewCount() == null ? 0 : share.getViewCount());
        vo.setUniqueVisitors(uniqueCount == null ? 0 : Math.toIntExact(uniqueCount));
        vo.setDownloadCount(downloadCount == null ? 0 : Math.toIntExact(downloadCount));
        vo.setViewTrend(Collections.emptyList());
        return vo;
    }

    public Map<String, Object> accessLogs(Long shareId, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<ShareAccessLog> wrapper = new LambdaQueryWrapper<ShareAccessLog>()
                .eq(ShareAccessLog::getShareId, shareId)
                .orderByDesc(ShareAccessLog::getAccessTime);
        long total = shareAccessLogMapper.selectCount(wrapper);
        int offset = (pageNum - 1) * pageSize;
        if (offset < 0) offset = 0;
        wrapper.last("LIMIT " + offset + "," + pageSize);
        List<ShareAccessLog> records = shareAccessLogMapper.selectList(wrapper);

        List<AccessLogVO> logs = records.stream().map(l -> {
            AccessLogVO vo = new AccessLogVO();
            vo.setVisitorId(l.getVisitorId());
            vo.setAccessTime(l.getAccessTime());
            vo.setIp(l.getIp());
            vo.setDevice(l.getDevice());
            vo.setAction(l.getAction());
            vo.setFileId(l.getFileId());
            return vo;
        }).collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("logs", logs);
        return data;
    }

    public Map<String, Object> poster(Long shareId) {
        requireShare(shareId);
        Map<String, Object> data = new HashMap<>();
        data.put("posterUrl", "https://cdn.rocgit.com/poster/" + shareId + ".png");
        return data;
    }

    public Map<String, Object> validate(ShareValidateRequest request) {
        Share share = shareMapper.selectOne(
                new LambdaQueryWrapper<Share>().eq(Share::getShareToken, request.getShareToken()));
        Map<String, Object> data = new HashMap<>();
        if (share == null) {
            data.put("valid", false);
            return data;
        }
        if (share.getExpiresAt() != null && share.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ResultCode.SHARE_LINK_EXPIRED);
        }
        Project project = projectMapper.selectById(share.getProjectId());
        data.put("valid", true);
        data.put("projectName", project == null ? null : project.getName());
        data.put("permissionLevel", share.getPermissionLevel());
        return data;
    }

    // ================= 访客端（无需鉴权） =================

    @Transactional
    public Map<String, Object> viewByToken(String shareToken) {
        Share share = resolveShareByToken(shareToken);

        // 浏览数 +1
        share.setViewCount((share.getViewCount() == null ? 0 : share.getViewCount()) + 1);
        shareMapper.updateById(share);

        // 记录访问日志
        ShareAccessLog accessLog = new ShareAccessLog();
        accessLog.setShareId(share.getId());
        accessLog.setAction("view");
        accessLog.setAccessTime(LocalDateTime.now());
        shareAccessLogMapper.insert(accessLog);

        Project project = projectMapper.selectById(share.getProjectId());
        Map<String, Object> data = new HashMap<>();
        data.put("projectName", project == null ? null : project.getName());
        String groupName = null;
        if (project != null && project.getGroupId() != null) {
            Group group = groupMapper.selectById(project.getGroupId());
            groupName = group == null ? null : group.getName();
        }
        data.put("groupName", groupName);
        return data;
    }

    public Map<String, Object> overview(String shareToken) {
        Share share = resolveShareByToken(shareToken);
        Project project = projectMapper.selectById(share.getProjectId());
        Long projectId = share.getProjectId();

        Long memberCount = 0L;
        if (project != null && project.getGroupId() != null) {
            memberCount = groupMemberMapper.selectCount(
                    new LambdaQueryWrapper<GroupMember>()
                            .eq(GroupMember::getGroupId, project.getGroupId())
                            .eq(GroupMember::getStatus, "active"));
        }
        Long fileCount = fileMapper.selectCount(
                new LambdaQueryWrapper<File>().eq(File::getProjectId, projectId).eq(File::getDeleted, 0));
        Long discussionCount = discussionMapper.selectCount(
                new LambdaQueryWrapper<Discussion>().eq(Discussion::getProjectId, projectId));
        Long decisionCount = decisionMapper.selectCount(
                new LambdaQueryWrapper<Decision>().eq(Decision::getProjectId, projectId));

        Map<String, Object> data = new HashMap<>();
        data.put("memberCount", memberCount == null ? 0 : Math.toIntExact(memberCount));
        data.put("fileCount", fileCount == null ? 0 : Math.toIntExact(fileCount));
        data.put("discussionCount", discussionCount == null ? 0 : Math.toIntExact(discussionCount));
        data.put("decisionCount", decisionCount == null ? 0 : Math.toIntExact(decisionCount));
        return data;
    }

    public List<Map<String, Object>> timeline(String shareToken) {
        Share share = resolveShareByToken(shareToken);
        List<OperationLog> logs = operationLogMapper.selectList(
                new LambdaQueryWrapper<OperationLog>()
                        .eq(OperationLog::getProjectId, share.getProjectId())
                        .orderByDesc(OperationLog::getCreatedAt)
                        .last("LIMIT 100"));
        List<Map<String, Object>> list = new ArrayList<>();
        for (OperationLog log : logs) {
            Map<String, Object> item = new HashMap<>();
            item.put("logId", log.getId());
            item.put("action", log.getAction());
            item.put("targetType", log.getTargetType());
            item.put("targetId", log.getTargetId());
            item.put("detail", log.getDetail());
            item.put("createdAt", log.getCreatedAt());
            if (log.getUserId() != null) {
                User user = userMapper.selectById(log.getUserId());
                item.put("userName", user == null ? null : user.getNickname());
            }
            list.add(item);
        }
        return list;
    }

    public Map<String, Object> files(String shareToken) {
        Share share = resolveShareByToken(shareToken);
        LambdaQueryWrapper<File> wrapper = new LambdaQueryWrapper<File>()
                .eq(File::getProjectId, share.getProjectId())
                .eq(File::getStatus, "approved")
                .eq(File::getDeleted, 0)
                .orderByDesc(File::getCreatedAt);
        long total = fileMapper.selectCount(wrapper);
        List<File> records = fileMapper.selectList(wrapper);
        List<Map<String, Object>> list = records.stream().map(f -> {
            Map<String, Object> item = new HashMap<>();
            item.put("fileId", f.getId());
            item.put("name", f.getName());
            item.put("type", f.getType());
            item.put("size", f.getSize());
            item.put("createdAt", f.getCreatedAt());
            return item;
        }).collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("list", list);
        return data;
    }

    public Map<String, Object> filePreview(String shareToken, Long fileId) {
        resolveShareByToken(shareToken);
        File file = fileMapper.selectById(fileId);
        Map<String, Object> data = new HashMap<>();
        data.put("previewUrl", file == null ? null : file.getUrl());
        data.put("type", file == null ? "file" : file.getType());
        return data;
    }

    @Transactional
    public Map<String, Object> fileDownload(String shareToken, Long fileId) {
        Share share = resolveShareByToken(shareToken);
        ShareAccessLog accessLog = new ShareAccessLog();
        accessLog.setShareId(share.getId());
        accessLog.setAction("download");
        accessLog.setFileId(fileId);
        accessLog.setAccessTime(LocalDateTime.now());
        shareAccessLogMapper.insert(accessLog);

        Map<String, Object> data = new HashMap<>();
        File file = fileMapper.selectById(fileId);
        data.put("downloadUrl", file == null ? null : file.getUrl());
        return data;
    }

    public List<Map<String, Object>> discussions(String shareToken) {
        Share share = resolveShareByToken(shareToken);
        List<Discussion> list = discussionMapper.selectList(
                new LambdaQueryWrapper<Discussion>()
                        .eq(Discussion::getProjectId, share.getProjectId())
                        .orderByDesc(Discussion::getCreatedAt));
        return list.stream().map(d -> {
            Map<String, Object> item = new HashMap<>();
            item.put("discussionId", d.getId());
            item.put("topic", d.getTopic());
            item.put("content", d.getContent());
            item.put("meetingTime", d.getMeetingTime());
            item.put("createdAt", d.getCreatedAt());
            return item;
        }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> decisions(String shareToken) {
        Share share = resolveShareByToken(shareToken);
        List<Decision> list = decisionMapper.selectList(
                new LambdaQueryWrapper<Decision>()
                        .eq(Decision::getProjectId, share.getProjectId())
                        .orderByDesc(Decision::getCreatedAt));
        return list.stream().map(d -> {
            Map<String, Object> item = new HashMap<>();
            item.put("decisionId", d.getId());
            item.put("title", d.getTitle());
            item.put("content", d.getContent());
            item.put("createdAt", d.getCreatedAt());
            return item;
        }).collect(Collectors.toList());
    }
}
