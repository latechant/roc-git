package com.rocgit.knife4j.controller;

import com.rocgit.knife4j.common.Result;
import com.rocgit.knife4j.dto.message.*;
import com.rocgit.knife4j.entity.message.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1/message")
@Tag(name = "12.消息", description = "消息通知与订阅")
@SecurityRequirement(name = "Bearer")
public class MessageController {

    @Operation(summary = "消息列表", description = "获取当前用户的消息列表")
    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @io.swagger.v3.oas.annotations.Parameter(description = "类型", schema = @io.swagger.v3.oas.annotations.media.Schema(allowableValues = {"system", "task", "mention", "audit"}))
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        Map<String, Object> data = new HashMap<>();
        data.put("total", 30);
        data.put("list", new ArrayList<>());
        return Result.success(data);
    }

    @Operation(summary = "未读消息数", description = "获取未读消息数量（分类统计）")
    @GetMapping("/unread-count")
    public Result<UnreadCountVO> unreadCount() {
        UnreadCountVO data = new UnreadCountVO();
        data.setTotal(5);
        data.setSystem(1);
        data.setTask(2);
        data.setMention(2);
        data.setAudit(0);
        return Result.success(data);
    }

    @Operation(summary = "消息详情", description = "查看消息详情")
    @GetMapping("/{messageId}")
    public Result<MessageVO> detail(@PathVariable Long messageId) {
        MessageVO data = new MessageVO();
        data.setMessageId(messageId);
        data.setType("task");
        data.setTitle("您有一个新的任务");
        data.setContent("李四给您分配了新任务：编写测试用例");
        data.setRead(false);
        data.setLink("/task/123");
        data.setCreatedAt(LocalDateTime.now());
        return Result.success(data);
    }

    @Operation(summary = "标记已读", description = "标记单条消息为已读")
    @PostMapping("/{messageId}/read")
    public Result<Void> markRead(@PathVariable Long messageId) {
        return Result.success("已标记已读", null);
    }

    @Operation(summary = "全部已读", description = "将所有/指定类型的消息标记为已读")
    @PostMapping("/read-all")
    public Result<Void> readAll(@RequestBody(required = false) ReadAllRequest request) {
        return Result.success("已全部标记已读", null);
    }

    @Operation(summary = "订阅设置", description = "订阅/取消订阅某种类型的消息通知")
    @PostMapping("/subscribe")
    public Result<Void> subscribe(@Valid @RequestBody SubscribeRequest request) {
        return Result.success("订阅成功", null);
    }

    @Operation(summary = "我的订阅", description = "获取当前用户的订阅列表")
    @GetMapping("/subscription")
    public Result<SubscriptionVO> subscription() {
        SubscriptionVO data = new SubscriptionVO();
        data.setAuditNotify(true);
        data.setTaskRemind(true);
        data.setMemberNotify(false);
        data.setStageNotify(true);
        data.setMentionNotify(true);
        return Result.success(data);
    }

    @Operation(summary = "更新订阅", description = "更新用户的订阅设置")
    @PutMapping("/subscription")
    public Result<Void> updateSubscription(@RequestBody UpdateSubscriptionRequest request) {
        return Result.success("订阅已更新", null);
    }
}
