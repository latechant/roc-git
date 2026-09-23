package com.rocgit.knife4j.controller;

import com.rocgit.knife4j.common.Result;
import com.rocgit.knife4j.entity.announcement.AnnouncementVO;
import com.rocgit.knife4j.service.AnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/announcement")
@Tag(name = "13.公告", description = "系统公告与通知")
@SecurityRequirement(name = "Bearer")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @Operation(summary = "公告列表", description = "获取系统公告列表（按发布时间倒序）")
    @GetMapping("/list")
    public Result<List<AnnouncementVO>> list(
            @RequestAttribute("userId") String userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(announcementService.list(pageNum, pageSize, Long.parseLong(userId)));
    }

    @Operation(summary = "标记已读", description = "将公告标记为已读")
    @PostMapping("/{announcementId}/read")
    public Result<Void> markRead(@RequestAttribute("userId") String userId,
                                 @PathVariable Long announcementId) {
        announcementService.markRead(announcementId, Long.parseLong(userId));
        return Result.success("已读", null);
    }
}
