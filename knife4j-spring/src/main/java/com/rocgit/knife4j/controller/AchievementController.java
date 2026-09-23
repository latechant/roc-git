package com.rocgit.knife4j.controller;

import com.rocgit.knife4j.common.Result;
import com.rocgit.knife4j.dto.achievement.*;
import com.rocgit.knife4j.entity.achievement.AchievementVO;
import com.rocgit.knife4j.service.AchievementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 成果模块 Controller - 接入真实 MySQL
 */
@RestController
@RequestMapping("/api/v1/achievement")
@Tag(name = "10.成果", description = "项目成果管理")
@SecurityRequirement(name = "Bearer")
public class AchievementController {

    @Autowired
    private AchievementService achievementService;

    @Operation(summary = "创建成果", description = "登记项目阶段性成果，contributorId 为当前登录用户，可携带关联文件")
    @PostMapping("/create")
    public Result<AchievementVO> create(@RequestAttribute("userId") String userId,
                                       @Valid @RequestBody CreateAchievementRequest request) {
        AchievementVO data = achievementService.create(request, Long.parseLong(userId));
        return Result.success(data);
    }

    @Operation(summary = "成果列表", description = "按项目ID查询成果列表")
    @GetMapping("/list")
    public Result<List<AchievementVO>> list(@RequestParam Long projectId) {
        List<AchievementVO> data = achievementService.listByProject(projectId);
        return Result.success(data);
    }

    @Operation(summary = "成果详情", description = "获取成果详情，含阶段名与关联文件")
    @GetMapping("/{achievementId}")
    public Result<AchievementVO> detail(@PathVariable Long achievementId) {
        AchievementVO data = achievementService.getById(achievementId);
        return Result.success(data);
    }

    @Operation(summary = "更新成果", description = "修改成果标题/描述")
    @PutMapping("/{achievementId}")
    public Result<Void> update(@PathVariable Long achievementId,
                               @Valid @RequestBody UpdateAchievementRequest request) {
        achievementService.update(achievementId, request);
        return Result.success("更新成功", null);
    }

    @Operation(summary = "删除成果", description = "删除成果并级联清理关联文件")
    @DeleteMapping("/{achievementId}")
    public Result<Void> delete(@PathVariable Long achievementId) {
        achievementService.delete(achievementId);
        return Result.success("删除成功", null);
    }
}
