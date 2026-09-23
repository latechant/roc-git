package com.rocgit.knife4j.controller;

import com.rocgit.knife4j.common.Result;
import com.rocgit.knife4j.dto.decision.*;
import com.rocgit.knife4j.entity.decision.DecisionVO;
import com.rocgit.knife4j.service.DecisionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 决策模块 Controller - 接入真实 MySQL
 */
@RestController
@RequestMapping("/api/v1/decision")
@Tag(name = "09.决策", description = "小组决策记录")
@SecurityRequirement(name = "Bearer")
public class DecisionController {

    @Autowired
    private DecisionService decisionService;

    @Operation(summary = "创建决策", description = "记录小组的关键决策，creatorId 为当前登录用户")
    @PostMapping("/create")
    public Result<DecisionVO> create(@RequestAttribute("userId") String userId,
                                      @Valid @RequestBody CreateDecisionRequest request) {
        DecisionVO data = decisionService.create(request, Long.parseLong(userId));
        return Result.success(data);
    }

    @Operation(summary = "决策列表", description = "按项目ID分页查询决策列表")
    @GetMapping("/list")
    public Result<List<DecisionVO>> list(@RequestParam Long projectId,
                                         @RequestParam(defaultValue = "1") Integer pageNum,
                                         @RequestParam(defaultValue = "20") Integer pageSize) {
        List<DecisionVO> data = decisionService.listByProject(projectId, pageNum, pageSize);
        return Result.success(data);
    }

    @Operation(summary = "决策详情", description = "获取决策详情，关联研讨主题一并返回")
    @GetMapping("/{decisionId}")
    public Result<DecisionVO> detail(@PathVariable Long decisionId) {
        DecisionVO data = decisionService.getById(decisionId);
        return Result.success(data);
    }

    @Operation(summary = "更新决策", description = "修改决策标题/内容")
    @PutMapping("/{decisionId}")
    public Result<Void> update(@PathVariable Long decisionId,
                               @Valid @RequestBody UpdateDecisionRequest request) {
        decisionService.update(decisionId, request);
        return Result.success("更新成功", null);
    }

    @Operation(summary = "删除决策", description = "删除决策记录")
    @DeleteMapping("/{decisionId}")
    public Result<Void> delete(@PathVariable Long decisionId) {
        decisionService.delete(decisionId);
        return Result.success("删除成功", null);
    }
}
