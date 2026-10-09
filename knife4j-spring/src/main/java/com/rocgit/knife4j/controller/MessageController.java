package com.rocgit.knife4j.controller;

import com.rocgit.knife4j.common.Result;
import com.rocgit.knife4j.dto.message.*;
import com.rocgit.knife4j.entity.message.*;
import com.rocgit.knife4j.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/message")
@Tag(name = "12.消息", description = "消息通知与订阅")
@SecurityRequirement(name = "Bearer")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Operation(summary = "消息列表", description = "获取当前用户的消息列表")
    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestAttribute("userId") String userId,
            @io.swagger.v3.oas.annotations.Parameter(description = "类型", schema = @io.swagger.v3.oas.annotations.media.Schema(allowableValues = {"system", "task", "mention", "audit"}))
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(messageService.list(Long.parseLong(userId), type, pageNum, pageSize));
    }

    @Operation(summary = "未读消息数", description = "获取未读消息数量（分类统计）")
    @GetMapping("/unread-count")
    public Result<UnreadCountVO> unreadCount(@RequestAttribute("userId") String userId) {
        return Result.success(messageService.unreadCount(Long.parseLong(userId)));
    }

    @Operation(summary = "消息详情", description = "查看消息详情，自动标记已读")
    @GetMapping("/{messageId}")
    public Result<MessageVO> detail(@RequestAttribute("userId") String userId,
                                    @PathVariable Long messageId) {
        return Result.success(messageService.detail(messageId, Long.parseLong(userId)));
    }

    @Operation(summary = "标记已读", description = "标记单条消息为已读")
    @PostMapping("/{messageId}/read")
    public Result<Void> markRead(@RequestAttribute("userId") String userId,
                                @PathVariable Long messageId) {
        messageService.markRead(messageId, Long.parseLong(userId));
        return Result.success("已标记已读", null);
    }

    @Operation(summary = "删除消息", description = "删除单条消息（仅限自己的）")
    @DeleteMapping("/{messageId}")
    public Result<Void> deleteMessage(@RequestAttribute("userId") String userId,
                                      @PathVariable Long messageId) {
        messageService.deleteMessage(messageId, Long.parseLong(userId));
        return Result.success("删除成功", null);
    }

    @Operation(summary = "全部已读", description = "将所有/指定类型的消息标记为已读")
    @PostMapping("/read-all")
    public Result<Void> readAll(@RequestAttribute("userId") String userId,
                                @RequestBody(required = false) ReadAllRequest request) {
        messageService.readAll(Long.parseLong(userId), request);
        return Result.success("已全部标记已读", null);
    }

    @Operation(summary = "订阅设置", description = "订阅/取消订阅某种类型的消息通知")
    @PostMapping("/subscribe")
    public Result<Void> subscribe(@RequestAttribute("userId") String userId,
                                  @Valid @RequestBody SubscribeRequest request) {
        messageService.subscribe(Long.parseLong(userId), request);
        return Result.success("订阅成功", null);
    }

    @Operation(summary = "我的订阅", description = "获取当前用户的订阅列表")
    @GetMapping("/subscription")
    public Result<SubscriptionVO> subscription(@RequestAttribute("userId") String userId) {
        return Result.success(messageService.getSubscription(Long.parseLong(userId)));
    }

    @Operation(summary = "更新订阅", description = "更新用户的订阅设置")
    @PutMapping("/subscription")
    public Result<Void> updateSubscription(@RequestAttribute("userId") String userId,
                                          @RequestBody UpdateSubscriptionRequest request) {
        messageService.updateSubscription(Long.parseLong(userId), request);
        return Result.success("订阅已更新", null);
    }
}
