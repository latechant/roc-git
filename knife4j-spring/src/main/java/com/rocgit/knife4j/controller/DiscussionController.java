package com.rocgit.knife4j.controller;

import com.rocgit.knife4j.common.Result;
import com.rocgit.knife4j.dto.discussion.*;
import com.rocgit.knife4j.entity.discussion.DiscussionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1/discussion")
@Tag(name = "08.研讨", description = "研讨记录管理")
@SecurityRequirement(name = "Bearer")
public class DiscussionController {

    @Operation(summary = "创建研讨记录", description = "记录小组会议讨论内容")
    @PostMapping("/create")
    public Result<DiscussionVO> create(@Valid @RequestBody CreateDiscussionRequest request) {
        DiscussionVO data = new DiscussionVO();
        data.setDiscussionId(System.currentTimeMillis());
        data.setProjectId(request.getProjectId());
        data.setTopic(request.getTopic());
        data.setContent(request.getContent());
        data.setMeetingTime(request.getMeetingTime());
        data.setCreatedAt(LocalDateTime.now());
        return Result.success(data);
    }

    @Operation(summary = "研讨列表", description = "查询项目下的研讨记录")
    @GetMapping("/list")
    public Result<List<DiscussionVO>> list(
            @org.springframework.web.bind.annotation.RequestParam Long projectId,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "1") Integer pageNum,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(new ArrayList<>());
    }

    @Operation(summary = "研讨详情", description = "获取研讨记录详情")
    @GetMapping("/{discussionId}")
    public Result<DiscussionVO> detail(@PathVariable Long discussionId) {
        DiscussionVO data = new DiscussionVO();
        data.setDiscussionId(discussionId);
        data.setTopic("需求评审会议");
        data.setContent("讨论了需求的边界...");
        data.setMeetingTime(LocalDateTime.now());
        data.setCreatedAt(LocalDateTime.now());
        return Result.success(data);
    }

    @Operation(summary = "更新研讨", description = "修改研讨记录")
    @PutMapping("/{discussionId}")
    public Result<Void> update(@PathVariable Long discussionId, @Valid @RequestBody UpdateDiscussionRequest request) {
        return Result.success("更新成功", null);
    }

    @Operation(summary = "删除研讨", description = "删除研讨记录")
    @DeleteMapping("/{discussionId}")
    public Result<Void> delete(@PathVariable Long discussionId) {
        return Result.success("删除成功", null);
    }
}
