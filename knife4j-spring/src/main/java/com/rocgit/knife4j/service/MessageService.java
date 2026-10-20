package com.rocgit.knife4j.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rocgit.knife4j.common.BusinessException;
import com.rocgit.knife4j.common.ResultCode;
import com.rocgit.knife4j.dto.message.ReadAllRequest;
import com.rocgit.knife4j.dto.message.SubscribeRequest;
import com.rocgit.knife4j.dto.message.UpdateSubscriptionRequest;
import com.rocgit.knife4j.entity.message.MessageVO;
import com.rocgit.knife4j.entity.message.SubscriptionVO;
import com.rocgit.knife4j.entity.message.UnreadCountVO;
import com.rocgit.knife4j.mapper.MessageMapper;
import com.rocgit.knife4j.mapper.UserSubscriptionMapper;
import com.rocgit.knife4j.model.Message;
import com.rocgit.knife4j.model.UserSubscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 消息通知与订阅服务
 */
@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private UserSubscriptionMapper userSubscriptionMapper;

    private MessageVO toVO(Message m) {
        MessageVO vo = new MessageVO();
        vo.setMessageId(m.getId());
        vo.setType(m.getType());
        vo.setTitle(m.getTitle());
        vo.setContent(m.getContent());
        vo.setRead(m.getIsRead() != null && m.getIsRead() == 1);
        vo.setProjectId(m.getProjectId());
        vo.setLink(m.getLink());
        vo.setCreatedAt(m.getCreatedAt());
        return vo;
    }

    /** 消息分页列表，可按 type 过滤 */
    public Map<String, Object> list(Long userId, String type, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<Message>()
                .eq(Message::getUserId, userId)
                .eq(type != null && !type.isEmpty(), Message::getType, type)
                .orderByDesc(Message::getCreatedAt);
        long total = messageMapper.selectCount(wrapper);
        int offset = (pageNum - 1) * pageSize;
        if (offset < 0) offset = 0;
        wrapper.last("LIMIT " + offset + "," + pageSize);
        List<Message> records = messageMapper.selectList(wrapper);

        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("list", records.stream().map(this::toVO).collect(Collectors.toList()));
        return data;
    }

    /** 未读消息数（按类型分组） */
    public UnreadCountVO unreadCount(Long userId) {
        long total = messageMapper.selectCount(
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getUserId, userId)
                        .eq(Message::getIsRead, 0));
        long system = messageMapper.selectCount(
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getUserId, userId)
                        .eq(Message::getIsRead, 0)
                        .eq(Message::getType, "system"));
        long task = messageMapper.selectCount(
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getUserId, userId)
                        .eq(Message::getIsRead, 0)
                        .eq(Message::getType, "task"));
        long mention = messageMapper.selectCount(
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getUserId, userId)
                        .eq(Message::getIsRead, 0)
                        .eq(Message::getType, "mention"));
        long audit = messageMapper.selectCount(
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getUserId, userId)
                        .eq(Message::getIsRead, 0)
                        .eq(Message::getType, "audit"));

        UnreadCountVO vo = new UnreadCountVO();
        vo.setTotal((int) total);
        vo.setSystem((int) system);
        vo.setTask((int) task);
        vo.setMention((int) mention);
        vo.setAudit((int) audit);
        return vo;
    }

    /** 消息详情，自动标记已读 */
    @Transactional
    public MessageVO detail(Long messageId, Long userId) {
        Message m = messageMapper.selectById(messageId);
        if (m == null || !m.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.NOT_FOUND, "消息不存在");
        }
        if (m.getIsRead() == null || m.getIsRead() == 0) {
            Message upd = new Message();
            upd.setId(messageId);
            upd.setIsRead(1);
            messageMapper.updateById(upd);
            m.setIsRead(1);
        }
        return toVO(m);
    }

    /** 标记单条已读 */
    @Transactional
    public void markRead(Long messageId, Long userId) {
        Message upd = new Message();
        upd.setId(messageId);
        upd.setIsRead(1);
        messageMapper.update(upd,
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getId, messageId)
                        .eq(Message::getUserId, userId));
    }

    /** 全部/按类型已读 */
    @Transactional
    public void readAll(Long userId, ReadAllRequest request) {
        Message upd = new Message();
        upd.setIsRead(1);
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<Message>()
                .eq(Message::getUserId, userId)
                .eq(Message::getIsRead, 0);
        if (request != null && request.getType() != null && !"all".equals(request.getType())) {
            wrapper.eq(Message::getType, request.getType());
        }
        messageMapper.update(upd, wrapper);
    }

    /** 删除消息（仅限自己的） */
    @Transactional
    public void deleteMessage(Long messageId, Long userId) {
        int affected = messageMapper.delete(
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getId, messageId)
                        .eq(Message::getUserId, userId));
        if (affected == 0) {
            throw new BusinessException(ResultCode.NOT_FOUND, "消息不存在");
        }
    }

    /** 单类型订阅开关：插入或更新 user_subscription */
    @Transactional
    public void subscribe(Long userId, SubscribeRequest request) {
        UserSubscription sub = userSubscriptionMapper.selectOne(
                new LambdaQueryWrapper<UserSubscription>().eq(UserSubscription::getUserId, userId));
        boolean enabled = !Boolean.FALSE.equals(request.getEnabled());
        int val = enabled ? 1 : 0;
        if (sub == null) {
            sub = new UserSubscription();
            sub.setUserId(userId);
            applyField(sub, request.getType(), val);
            userSubscriptionMapper.insert(sub);
        } else {
            applyField(sub, request.getType(), val);
            userSubscriptionMapper.updateById(sub);
        }
    }

    private void applyField(UserSubscription sub, String type, int val) {
        if (type == null) return;
        switch (type) {
            case "audit":
                sub.setAuditNotify(val);
                break;
            case "task":
                sub.setTaskRemind(val);
                break;
            case "member":
                sub.setMemberNotify(val);
                break;
            case "stage":
                sub.setStageNotify(val);
                break;
            case "mention":
                sub.setMentionNotify(val);
                break;
            default:
                break;
        }
    }

    private boolean isOn(Integer v) {
        return v != null && v == 1;
    }

    /** 查询订阅，不存在则创建默认全开启 */
    @Transactional
    public SubscriptionVO getSubscription(Long userId) {
        UserSubscription sub = userSubscriptionMapper.selectOne(
                new LambdaQueryWrapper<UserSubscription>().eq(UserSubscription::getUserId, userId));
        if (sub == null) {
            sub = new UserSubscription();
            sub.setUserId(userId);
            sub.setAuditNotify(1);
            sub.setTaskRemind(1);
            sub.setMemberNotify(1);
            sub.setStageNotify(1);
            sub.setMentionNotify(1);
            userSubscriptionMapper.insert(sub);
        }
        SubscriptionVO vo = new SubscriptionVO();
        vo.setAuditNotify(isOn(sub.getAuditNotify()));
        vo.setTaskRemind(isOn(sub.getTaskRemind()));
        vo.setMemberNotify(isOn(sub.getMemberNotify()));
        vo.setStageNotify(isOn(sub.getStageNotify()));
        vo.setMentionNotify(isOn(sub.getMentionNotify()));
        return vo;
    }

    /** 更新订阅（仅更新非空字段） */
    @Transactional
    public void updateSubscription(Long userId, UpdateSubscriptionRequest request) {
        UserSubscription sub = userSubscriptionMapper.selectOne(
                new LambdaQueryWrapper<UserSubscription>().eq(UserSubscription::getUserId, userId));
        if (sub == null) {
            sub = new UserSubscription();
            sub.setUserId(userId);
        }
        if (request.getAuditNotify() != null) sub.setAuditNotify(request.getAuditNotify() ? 1 : 0);
        if (request.getTaskRemind() != null) sub.setTaskRemind(request.getTaskRemind() ? 1 : 0);
        if (request.getMemberNotify() != null) sub.setMemberNotify(request.getMemberNotify() ? 1 : 0);
        if (request.getStageNotify() != null) sub.setStageNotify(request.getStageNotify() ? 1 : 0);
        if (request.getMentionNotify() != null) sub.setMentionNotify(request.getMentionNotify() ? 1 : 0);
        if (sub.getId() == null) {
            userSubscriptionMapper.insert(sub);
        } else {
            userSubscriptionMapper.updateById(sub);
        }
    }
}
