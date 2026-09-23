package com.rocgit.knife4j.controller;

import com.rocgit.knife4j.common.Result;
import com.rocgit.knife4j.dto.group.*;
import com.rocgit.knife4j.entity.group.*;
import com.rocgit.knife4j.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 小组模块 Controller - 接入真实 MySQL
 */
@RestController
@RequestMapping("/api/v1/group")
@Tag(name = "03.小组", description = "小组管理相关接口")
@SecurityRequirement(name = "Bearer")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Operation(summary = "创建小组", description = "创建新的课程小组，当前用户自动成为组长")
    @PostMapping("/create")
    public Result<GroupVO> create(@RequestAttribute("userId") String userId,
                                  @Valid @RequestBody CreateGroupRequest request) {
        GroupVO data = groupService.create(Long.parseLong(userId), request.getName(), request.getDescription());
        return Result.success(data);
    }

    @Operation(summary = "加入小组", description = "通过邀请码加入小组")
    @PostMapping("/join")
    public Result<Map<String, Object>> join(@RequestAttribute("userId") String userId,
                                            @Valid @RequestBody JoinGroupRequest request) {
        Map<String, Object> data = groupService.join(Long.parseLong(userId), request.getInviteCode());
        return Result.success(data);
    }

    @Operation(summary = "校验邀请码", description = "校验邀请码是否有效")
    @PostMapping("/validate-invite-code")
    public Result<ValidateInviteCodeVO> validateInviteCode(@Valid @RequestBody ValidateInviteCodeRequest request) {
        ValidateInviteCodeVO data = groupService.validateInviteCode(request.getInviteCode());
        return Result.success(data);
    }

    @Operation(summary = "我的小组列表", description = "获取当前用户加入的所有小组")
    @GetMapping("/my-list")
    public Result<List<GroupVO>> myList(@RequestAttribute("userId") String userId) {
        List<GroupVO> list = groupService.myList(Long.parseLong(userId));
        return Result.success(list);
    }

    @Operation(summary = "小组详情", description = "获取指定小组的详细信息")
    @Parameter(name = "groupId", description = "小组ID", required = true, example = "1")
    @GetMapping("/{groupId}")
    public Result<GroupVO> detail(@PathVariable Long groupId) {
        GroupVO data = groupService.detail(groupId);
        return Result.success(data);
    }

    @Operation(summary = "更新小组", description = "更新小组信息（仅组长）")
    @PutMapping("/{groupId}")
    public Result<Void> update(@PathVariable Long groupId,
                               @RequestAttribute("userId") String userId,
                               @Valid @RequestBody UpdateGroupRequest request) {
        groupService.update(groupId, Long.parseLong(userId), request.getName(), request.getDescription());
        return Result.success("更新成功", null);
    }

    @Operation(summary = "退出小组", description = "当前用户退出小组（组长不可退出，需先转让）")
    @PostMapping("/{groupId}/leave")
    public Result<Void> leave(@PathVariable Long groupId,
                              @RequestAttribute("userId") String userId) {
        groupService.leave(groupId, Long.parseLong(userId));
        return Result.success("已退出", null);
    }

    @Operation(summary = "小组成员列表", description = "获取小组的所有成员（分页）")
    @GetMapping("/{groupId}/members")
    public Result<Map<String, Object>> members(@PathVariable Long groupId,
                                               @RequestParam(defaultValue = "1") Integer pageNum,
                                               @RequestParam(defaultValue = "20") Integer pageSize) {
        Map<String, Object> data = groupService.members(groupId, pageNum, pageSize);
        return Result.success(data);
    }

    @Operation(summary = "修改成员备注", description = "组长修改成员的备注名")
    @PutMapping("/{groupId}/member/{userId}/remark")
    public Result<Void> updateRemark(@PathVariable Long groupId,
                                     @PathVariable("userId") Long targetUserId,
                                     @RequestAttribute("userId") String userId,
                                     @Valid @RequestBody UpdateRemarkRequest request) {
        groupService.updateRemark(groupId, Long.parseLong(userId), targetUserId, request.getRemark());
        return Result.success("备注已更新", null);
    }

    @Operation(summary = "转让组长", description = "组长转让给其他成员")
    @PostMapping("/{groupId}/transfer-leader")
    public Result<Void> transferLeader(@PathVariable Long groupId,
                                       @RequestAttribute("userId") String userId,
                                       @Valid @RequestBody TransferLeaderRequest request) {
        groupService.transferLeader(groupId, Long.parseLong(userId), request.getNewLeaderId());
        return Result.success("转让成功", null);
    }

    @Operation(summary = "重新生成邀请码", description = "生成新的邀请码，旧码失效")
    @PostMapping("/{groupId}/invite-code")
    public Result<RegenerateInviteCodeVO> regenerateInviteCode(@PathVariable Long groupId,
                                                               @RequestAttribute("userId") String userId) {
        RegenerateInviteCodeVO data = groupService.regenerateInviteCode(groupId, Long.parseLong(userId));
        return Result.success(data);
    }

    @Operation(summary = "小组统计", description = "小组的整体统计数据")
    @GetMapping("/{groupId}/stats")
    public Result<GroupStatsVO> stats(@PathVariable Long groupId) {
        GroupStatsVO data = groupService.stats(groupId);
        return Result.success(data);
    }

    @Operation(summary = "成员贡献排行", description = "小组成员的贡献排行榜")
    @GetMapping("/{groupId}/member-stats")
    public Result<List<MemberStatsVO>> memberStats(@PathVariable Long groupId) {
        List<MemberStatsVO> list = groupService.memberStats(groupId);
        return Result.success(list);
    }
}
