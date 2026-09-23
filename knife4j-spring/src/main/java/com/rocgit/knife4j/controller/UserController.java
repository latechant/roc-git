package com.rocgit.knife4j.controller;

import com.rocgit.knife4j.common.Result;
import com.rocgit.knife4j.dto.user.*;
import com.rocgit.knife4j.entity.user.*;
import com.rocgit.knife4j.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

/**
 * 用户模块 Controller - 接入真实 MySQL
 */
@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "02.用户", description = "用户认证与个人信息")
@SecurityRequirement(name = "Bearer")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "微信登录", description = "通过微信 code 换取 JWT Token，首次登录会自动注册")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginRequest request) {
        LoginVO data = userService.login(request.getCode(), request.getNickname(), request.getAvatarUrl());
        return Result.success(data);
    }

    @Operation(summary = "获取个人信息", description = "通过 JWT Token 获取当前登录用户信息")
    @GetMapping("/profile")
    public Result<ProfileVO> getProfile(@RequestAttribute("userId") String userId) {
        ProfileVO data = userService.getProfile(Long.parseLong(userId));
        return Result.success(data);
    }

    @Operation(summary = "更新个人信息", description = "更新昵称、头像等")
    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestAttribute("userId") String userId,
                                      @Valid @RequestBody UpdateProfileRequest request) {
        userService.updateProfile(Long.parseLong(userId), request.getNickname(), request.getAvatarUrl());
        return Result.success("更新成功", null);
    }

    @Operation(summary = "登录历史", description = "查询近 30 天登录记录（演示接口，暂无登录日志表）")
    @GetMapping("/login-history")
    public Result<Map<String, Object>> loginHistory() {
        Map<String, Object> data = new HashMap<>();
        data.put("total", 0);
        data.put("list", new ArrayList<>());
        return Result.success(data);
    }

    @Operation(summary = "退出登录", description = "清除当前会话")
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success("已退出登录", null);
    }

    @Operation(summary = "刷新 Token", description = "使用 refreshToken 换取新 Token（演示接口）")
    @PostMapping("/refresh-token")
    public Result<RefreshTokenVO> refreshToken(@RequestBody Map<String, String> body) {
        RefreshTokenVO data = new RefreshTokenVO();
        data.setToken("");
        data.setExpiresIn(7200);
        return Result.success(data);
    }

    @Operation(summary = "贡献统计", description = "获取当前用户的贡献数据")
    @GetMapping("/contribution-stats")
    public Result<ContributionStatsVO> contributionStats(@RequestAttribute("userId") String userId) {
        ContributionStatsVO data = userService.getContributionStats(Long.parseLong(userId));
        return Result.success(data);
    }

    @Operation(summary = "绑定微信", description = "绑定微信 openId 到当前账号（演示接口）")
    @PostMapping("/bind-wechat")
    public Result<Void> bindWechat(@Valid @RequestBody BindWechatRequest request) {
        return Result.success("绑定成功", null);
    }
}
