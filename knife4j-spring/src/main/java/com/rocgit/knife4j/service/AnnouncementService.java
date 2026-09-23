package com.rocgit.knife4j.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rocgit.knife4j.entity.announcement.AnnouncementVO;
import com.rocgit.knife4j.mapper.AnnouncementMapper;
import com.rocgit.knife4j.mapper.AnnouncementReadMapper;
import com.rocgit.knife4j.model.Announcement;
import com.rocgit.knife4j.model.AnnouncementRead;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 系统公告服务
 */
@Service
public class AnnouncementService {

    @Autowired
    private AnnouncementMapper announcementMapper;
    @Autowired
    private AnnouncementReadMapper announcementReadMapper;

    /**
     * 公告列表（按 created_at 倒序分页），并标记当前用户是否已读
     */
    public List<AnnouncementVO> list(Integer pageNum, Integer pageSize, Long userId) {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<Announcement>()
                .orderByDesc(Announcement::getCreatedAt);

        int offset = (pageNum - 1) * pageSize;
        if (offset < 0) offset = 0;
        wrapper.last("LIMIT " + offset + "," + pageSize);
        List<Announcement> records = announcementMapper.selectList(wrapper);

        if (records.isEmpty()) {
            return new ArrayList<>();
        }

        // 查询当前用户已读的公告 id
        List<Long> ids = records.stream().map(Announcement::getId).collect(Collectors.toList());
        Set<Long> readIds = new HashSet<>();
        if (userId != null) {
            List<AnnouncementRead> reads = announcementReadMapper.selectList(
                    new LambdaQueryWrapper<AnnouncementRead>()
                            .eq(AnnouncementRead::getUserId, userId)
                            .in(AnnouncementRead::getAnnouncementId, ids));
            readIds = reads.stream().map(AnnouncementRead::getAnnouncementId).collect(Collectors.toSet());
        }
        final Set<Long> finalReadIds = readIds;

        return records.stream().map(a -> {
            AnnouncementVO vo = new AnnouncementVO();
            vo.setAnnouncementId(a.getId());
            vo.setTitle(a.getTitle());
            vo.setContent(a.getContent());
            vo.setLevel(a.getLevel());
            vo.setCreatedAt(a.getCreatedAt());
            vo.setIsRead(finalReadIds.contains(a.getId()));
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 标记公告已读（唯一键冲突则忽略）
     */
    @Transactional
    public void markRead(Long announcementId, Long userId) {
        Long exist = announcementReadMapper.selectCount(
                new LambdaQueryWrapper<AnnouncementRead>()
                        .eq(AnnouncementRead::getAnnouncementId, announcementId)
                        .eq(AnnouncementRead::getUserId, userId));
        if (exist != null && exist > 0) {
            return;
        }
        AnnouncementRead read = new AnnouncementRead();
        read.setAnnouncementId(announcementId);
        read.setUserId(userId);
        read.setReadAt(LocalDateTime.now());
        try {
            announcementReadMapper.insert(read);
        } catch (org.springframework.dao.DuplicateKeyException ignored) {
            // 并发下唯一键冲突，忽略
        }
    }
}
