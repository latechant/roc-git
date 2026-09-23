package com.rocgit.knife4j.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rocgit.knife4j.common.BusinessException;
import com.rocgit.knife4j.common.ResultCode;
import com.rocgit.knife4j.dto.discussion.CreateDiscussionRequest;
import com.rocgit.knife4j.dto.discussion.UpdateDiscussionRequest;
import com.rocgit.knife4j.entity.discussion.DiscussionVO;
import com.rocgit.knife4j.mapper.DiscussionMapper;
import com.rocgit.knife4j.mapper.UserMapper;
import com.rocgit.knife4j.model.Discussion;
import com.rocgit.knife4j.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 研讨记录服务 - 接入真实 MySQL
 */
@Service
public class DiscussionService {

    @Autowired
    private DiscussionMapper discussionMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 创建研讨记录
     */
    @Transactional
    public DiscussionVO create(CreateDiscussionRequest request, Long userId) {
        Discussion discussion = new Discussion();
        discussion.setProjectId(request.getProjectId());
        discussion.setTopic(request.getTopic());
        discussion.setContent(request.getContent());
        discussion.setMeetingTime(request.getMeetingTime());
        discussion.setAttendees(joinAttendees(request.getAttendees()));
        discussion.setCreatedBy(userId);
        discussion.setCreatedAt(LocalDateTime.now());
        discussionMapper.insert(discussion);
        return toVO(discussion);
    }

    /**
     * 按项目分页查询研讨记录
     */
    public List<DiscussionVO> listByProject(Long projectId, Integer pageNum, Integer pageSize) {
        Page<Discussion> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Discussion> wrapper = new LambdaQueryWrapper<Discussion>()
                .eq(Discussion::getProjectId, projectId)
                .orderByDesc(Discussion::getCreatedAt);
        List<Discussion> records = discussionMapper.selectPage(page, wrapper).getRecords();
        return records.stream().map(this::toVO).collect(Collectors.toList());
    }

    /**
     * 查询研讨详情
     */
    public DiscussionVO getById(Long discussionId) {
        Discussion discussion = discussionMapper.selectById(discussionId);
        if (discussion == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "研讨记录不存在");
        }
        return toVO(discussion);
    }

    /**
     * 更新研讨记录
     */
    @Transactional
    public void update(Long discussionId, UpdateDiscussionRequest request) {
        Discussion exist = discussionMapper.selectById(discussionId);
        if (exist == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "研讨记录不存在");
        }
        Discussion update = new Discussion();
        update.setId(discussionId);
        if (request.getTopic() != null) update.setTopic(request.getTopic());
        if (request.getContent() != null) update.setContent(request.getContent());
        if (request.getMeetingTime() != null) update.setMeetingTime(request.getMeetingTime());
        if (request.getAttendees() != null) update.setAttendees(joinAttendees(request.getAttendees()));
        discussionMapper.updateById(update);
    }

    /**
     * 删除研讨记录
     */
    @Transactional
    public void delete(Long discussionId) {
        discussionMapper.deleteById(discussionId);
    }

    /**
     * 将参会人 ID 列表拼接为逗号分隔字符串（VARCHAR(500)）
     */
    private String joinAttendees(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return null;
        }
        return userIds.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    /**
     * 实体转 VO，attendees 字符串解析为参会人列表
     */
    private DiscussionVO toVO(Discussion discussion) {
        DiscussionVO vo = new DiscussionVO();
        vo.setDiscussionId(discussion.getId());
        vo.setProjectId(discussion.getProjectId());
        vo.setTopic(discussion.getTopic());
        vo.setContent(discussion.getContent());
        vo.setMeetingTime(discussion.getMeetingTime());
        vo.setCreatedAt(discussion.getCreatedAt());
        vo.setAttendees(buildAttendeeList(discussion.getAttendees()));
        return vo;
    }

    private List<DiscussionVO.Attendee> buildAttendeeList(String attendees) {
        List<DiscussionVO.Attendee> result = new ArrayList<>();
        if (!StringUtils.hasText(attendees)) {
            return result;
        }
        List<Long> userIds = new ArrayList<>();
        for (String s : attendees.split(",")) {
            String t = s.trim();
            if (!t.isEmpty()) {
                try {
                    userIds.add(Long.parseLong(t));
                } catch (NumberFormatException ignored) {
                    // 忽略非法片段
                }
            }
        }
        if (userIds.isEmpty()) {
            return result;
        }
        Map<Long, User> userMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));
        for (Long uid : userIds) {
            User user = userMap.get(uid);
            if (user == null) {
                continue;
            }
            DiscussionVO.Attendee attendee = new DiscussionVO.Attendee();
            attendee.setUserId(user.getId());
            attendee.setNickname(user.getNickname());
            attendee.setAvatarUrl(user.getAvatarUrl());
            result.add(attendee);
        }
        return result;
    }
}
