package com.rocgit.knife4j.controller;

import com.rocgit.knife4j.common.Result;
import com.rocgit.knife4j.dto.achievement.*;
import com.rocgit.knife4j.entity.achievement.AchievementVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1/achievement")
@Tag(name = "10.成果", description = "项目成果管理")
@SecurityRequirement(name = "Bearer")
public class AchievementController {

    @Operation(summary = "创建成果", description = "登记项目阶段性成果")
    @PostMapping("/create")
    public Result<AchievementVO> create(@Valid @RequestBody CreateAchievementRequest request) {
        AchievementVO data = new AchievementVO();
        data.setAchievementId(System.currentTimeMillis());
        data.setProjectId(request.getProjectId());
        data.setStageId(request.getStageId());
        data.setTitle(request.getTitle());
        data.setDescription(request.getDescription());
        data.setCreatedAt(LocalDateTime.now());
        return Result.success(data);
    }

    @Operation(summary = "成果列表", description = "查询项目下的成果")
    @GetMapping("/list")
    public Result<List<AchievementVO>> list(@RequestParam Long projectId) {
        return Result.success(new ArrayList<>());
    }

    @Operation(summary = "成果详情", description = "获取成果详情")
    @GetMapping("/{achievementId}")
    public Result<AchievementVO> detail(@PathVariable Long achievementId) {
        AchievementVO data = new AchievementVO();
        data.setAchievementId(achievementId);
        data.setTitle("完成需求分析");
        data.setDescription("产出完整的需求规格说明书");
        data.setStageName("需求分析");
        return Result.success(data);
    }

    @Operation(summary = "更新成果", description = "修改成果信息")
    @PutMapping("/{achievementId}")
    public Result<Void> update(@PathVariable Long achievementId, @Valid @RequestBody UpdateAchievementRequest request) {
        return Result.success("更新成功", null);
    }

    @Operation(summary = "删除成果", description = "删除成果")
    @DeleteMapping("/{achievementId}")
    public Result<Void> delete(@PathVariable Long achievementId) {
        return Result.success("删除成功", null);
    }
}
