package com.rocgit.knife4j.service;

import com.rocgit.knife4j.mapper.MessageMapper;
import com.rocgit.knife4j.mapper.OperationLogMapper;
import com.rocgit.knife4j.model.Message;
import com.rocgit.knife4j.model.OperationLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 通知服务：在业务动作完成后写入站内消息 + 操作日志
 */
@Service
public class NotifyService {

    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private OperationLogMapper operationLogMapper;

    /**
     * 发送站内消息
     *
     * @param userId    接收者用户ID
     * @param projectId 项目ID
     * @param type      消息类型（system/task/mention/audit）
     * @param title     标题
     * @param content   内容
     */
    public void sendMessage(Long userId, Long projectId, String type, String title, String content) {
        Message msg = new Message();
        msg.setUserId(userId);
        msg.setProjectId(projectId);
        msg.setType(type);
        msg.setTitle(title);
        msg.setContent(content);
        msg.setIsRead(0);
        msg.setCreatedAt(LocalDateTime.now());
        messageMapper.insert(msg);
    }

    /**
     * 记录操作日志
     *
     * @param projectId  项目ID
     * @param userId     操作者用户ID
     * @param action     动作（如 upload/approve/reject/create/complete/transfer）
     * @param targetType 目标类型（如 file/task/project）
     * @param targetId   目标ID
     * @param targetName 目标名称
     * @param detail     详情
     */
    public void logOperation(Long projectId, Long userId, String action, String targetType,
                             Long targetId, String targetName, String detail) {
        OperationLog log = new OperationLog();
        log.setProjectId(projectId);
        log.setUserId(userId);
        log.setAction(action);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setTargetName(targetName);
        log.setDetail(detail);
        log.setCreatedAt(LocalDateTime.now());
        operationLogMapper.insert(log);
    }
}
