package com.rocgit.knife4j.controller;

import com.rocgit.knife4j.common.Result;
import com.rocgit.knife4j.dto.decision.*;
import com.rocgit.knife4j.entity.decision.DecisionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1/decision")
@Tag(name = "09.决策", description = "小组决策记录")
@SecurityRequirement(name = "Bearer")
public class DecisionController {

    @Operation(summary = "创建决策", description = "记录小组的关键决策")
    @PostMapping("/create")
    public Result<DecisionVO> create(@Valid @RequestBody CreateDecisionRequest request) {
        DecisionVO data = new DecisionVO();
        data.setDecisionId(System.currentTimeMillis());
        data.setProjectId(request.getProjectId());
        data.setTitle(request.getTitle());
        data.setContent(request.getContent());
        data.setDiscussionId(request.getDiscussionId());
        data.setCreatedAt(LocalDateTime.now());
        return Result.success(data);
    }

    @Operation(summary = "决策列表", description = "查询项目下的决策列表")
    @GetMapping("/list")
    public Result<List<DecisionVO>> list(
            @RequestParam Long projectId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(new ArrayList<>());
    }

    @Operation(summary = "决策详情", description = "获取决策详情")
    @GetMapping("/{decisionId}")
    public Result<DecisionVO> detail(@PathVariable Long decisionId) {
        DecisionVO data = new DecisionVO();
        data.setDecisionId(decisionId);
        data.setTitle("采用 Spring Boot 框架");
        data.setContent("经过讨论决定后端使用 Spring Boot...");
        data.setDiscussionId(101L);
        data.setRelatedDiscussionTopic("技术选型研讨");
        data.setCreatedAt(LocalDateTime.now());
        return Result.success(data);
    }

    @Operation(summary = "更新决策", description = "修改决策内容")
    @PutMapping("/{decisionId}")
    public Result<Void> update(@PathVariable Long decisionId, @Valid @RequestBody UpdateDecisionRequest request) {
        return Result.success("更新成功", null);
    }

    @Operation(summary = "删除决策", description = "删除决策记录")
    @DeleteMapping("/{decisionId}")
    public Result<Void> delete(@PathVariable Long decisionId) {
        return Result.success("删除成功", null);
    }
}
