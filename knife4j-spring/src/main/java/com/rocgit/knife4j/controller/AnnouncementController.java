package com.rocgit.knife4j.controller;

import com.rocgit.knife4j.common.Result;
import com.rocgit.knife4j.entity.announcement.AnnouncementVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1/announcement")
@Tag(name = "13.公告", description = "系统公告与通知")
@SecurityRequirement(name = "Bearer")
public class AnnouncementController {

    @Operation(summary = "公告列表", description = "获取系统公告列表")
    @GetMapping("/list")
    public Result<List<AnnouncementVO>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        List<AnnouncementVO> list = new ArrayList<>();
        AnnouncementVO a = new AnnouncementVO();
        a.setAnnouncementId(1L);
        a.setTitle("系统升级公告");
        a.setContent("系统将于本周日凌晨 2:00-4:00 进行升级维护...");
        a.setLevel("normal");
        a.setCreatedAt(LocalDateTime.now());
        list.add(a);
        return Result.success(list);
    }

    @Operation(summary = "标记已读", description = "将公告标记为已读")
    @PostMapping("/{announcementId}/read")
    public Result<Void> markRead(@PathVariable Long announcementId) {
        return Result.success("已读", null);
    }
}
