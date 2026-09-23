package com.rocgit.knife4j.controller;

import com.rocgit.knife4j.common.Result;
import com.rocgit.knife4j.dto.discussion.*;
import com.rocgit.knife4j.entity.discussion.DiscussionVO;
import com.rocgit.knife4j.service.DiscussionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 研讨模块 Controller - 接入真实 MySQL
 */
@RestController
@RequestMapping("/api/v1/discussion")
@Tag(name = "08.研讨", description = "研讨记录管理")
@SecurityRequirement(name = "Bearer")
public class DiscussionController {

    @Autowired
    private DiscussionService discussionService;

    @Operation(summary = "创建研讨记录", description = "记录小组会议讨论内容，createdBy 为当前登录用户")
    @PostMapping("/create")
    public Result<DiscussionVO> create(@RequestAttribute("userId") String userId,
                                       @Valid @RequestBody CreateDiscussionRequest request) {
        DiscussionVO data = discussionService.create(request, Long.parseLong(userId));
        return Result.success(data);
    }

    @Operation(summary = "研讨列表", description = "按项目ID分页查询研讨记录")
    @GetMapping("/list")
    public Result<List<DiscussionVO>> list(@RequestParam Long projectId,
                                           @RequestParam(defaultValue = "1") Integer pageNum,
                                           @RequestParam(defaultValue = "20") Integer pageSize) {
        List<DiscussionVO> data = discussionService.listByProject(projectId, pageNum, pageSize);
        return Result.success(data);
    }

    @Operation(summary = "研讨详情", description = "获取研讨记录详情")
    @GetMapping("/{discussionId}")
    public Result<DiscussionVO> detail(@PathVariable Long discussionId) {
        DiscussionVO data = discussionService.getById(discussionId);
        return Result.success(data);
    }

    @Operation(summary = "更新研讨", description = "修改研讨主题/内容/会议时间/参会人员")
    @PutMapping("/{discussionId}")
    public Result<Void> update(@PathVariable Long discussionId,
                               @Valid @RequestBody UpdateDiscussionRequest request) {
        discussionService.update(discussionId, request);
        return Result.success("更新成功", null);
    }

    @Operation(summary = "删除研讨", description = "删除研讨记录")
    @DeleteMapping("/{discussionId}")
    public Result<Void> delete(@PathVariable Long discussionId) {
        discussionService.delete(discussionId);
        return Result.success("删除成功", null);
    }
}
