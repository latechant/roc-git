package com.rocgit.knife4j.controller;

import com.rocgit.knife4j.common.Result;
import com.rocgit.knife4j.dto.user.*;
import com.rocgit.knife4j.entity.user.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户模块 Controller - 完整 DTO 版本
 *
 * 每个接口都有严格的入参类型、出参类型、Swagger 注解
 */
@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "02.用户", description = "用户认证与个人信息")
@SecurityRequirement(name = "Bearer")
public class UserController {

    @Operation(
            summary = "微信登录",
            description = "通过微信 code 换取 JWT Token，首次登录会自动注册"
    )
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginRequest request) {
        LoginVO data = new LoginVO();
        data.setToken("eyJhbGciOiJIUzI1NiJ9.example.token");
        data.setUserId(10001L);
        data.setOpenid("oUpF8uMuAJ...");
        data.setNickname(request.getNickname());
        data.setAvatarUrl(request.getAvatarUrl());
        data.setIsNewUser(true);
        return Result.success(data);
    }

    @Operation(
            summary = "获取个人信息",
            description = "通过 JWT Token 获取当前登录用户信息"
    )
    @GetMapping("/profile")
    public Result<ProfileVO> getProfile() {
        ProfileVO data = new ProfileVO();
        data.setUserId(10001L);
        data.setOpenid("oUpF8uMuAJ...");
        data.setNickname("张三");
        data.setAvatarUrl("https://cdn.rocgit.com/avatar/1.jpg");
        data.setCreatedAt(java.time.LocalDateTime.now());
        data.setGroupCount(3);
        data.setProjectCount(5);
        return Result.success(data);
    }

    @Operation(summary = "更新个人信息", description = "更新昵称、头像等")
    @PutMapping("/profile")
    public Result<Void> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return Result.success("更新成功", null);
    }

    @Operation(summary = "登录历史", description = "查询近 30 天登录记录")
    @GetMapping("/login-history")
    public Result<Map<String, Object>> loginHistory() {
        LoginHistoryVO h1 = new LoginHistoryVO();
        h1.setLoginTime(java.time.LocalDateTime.now().minusDays(1));
        h1.setDevice("iPhone 15 Pro");
        h1.setIp("192.168.1.1");
        LoginHistoryVO h2 = new LoginHistoryVO();
        h2.setLoginTime(java.time.LocalDateTime.now().minusDays(3));
        h2.setDevice("Chrome/Windows");
        h2.setIp("192.168.1.2");
        Map<String, Object> data = new HashMap<>();
        data.put("total", 2);
        data.put("list", Arrays.asList(h1, h2));
        return Result.success(data);
    }

    @Operation(summary = "退出登录", description = "清除当前会话")
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success("已退出登录", null);
    }

    @Operation(summary = "刷新 Token", description = "使用 refreshToken 换取新 Token")
    @PostMapping("/refresh-token")
    public Result<RefreshTokenVO> refreshToken(@RequestBody Map<String, String> body) {
        RefreshTokenVO data = new RefreshTokenVO();
        data.setToken("eyJhbGciOiJIUzI1NiJ9.newtoken");
        data.setExpiresIn(7200);
        return Result.success(data);
    }

    @Operation(summary = "贡献统计", description = "获取当前用户的贡献数据")
    @GetMapping("/contribution-stats")
    public Result<ContributionStatsVO> contributionStats() {
        ContributionStatsVO data = new ContributionStatsVO();
        data.setUploadedFiles(28);
        data.setApprovedFiles(25);
        data.setCompletedTasks(12);
        data.setDiscussions(8);
        data.setJoinDays(365);
        data.setLastActiveTime(java.time.LocalDateTime.now());
        return Result.success(data);
    }

    @Operation(summary = "绑定微信", description = "绑定微信 openId 到当前账号")
    @PostMapping("/bind-wechat")
    public Result<Void> bindWechat(@Valid @RequestBody BindWechatRequest request) {
        return Result.success("绑定成功", null);
    }
}
