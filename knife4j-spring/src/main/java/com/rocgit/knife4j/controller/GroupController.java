package com.rocgit.knife4j.controller;

import com.rocgit.knife4j.common.Result;
import com.rocgit.knife4j.dto.group.*;
import com.rocgit.knife4j.entity.group.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/v1/group")
@Tag(name = "03.小组", description = "小组管理相关接口")
@SecurityRequirement(name = "Bearer")
public class GroupController {

    @Operation(summary = "创建小组", description = "创建新的课程小组")
    @PostMapping("/create")
    public Result<GroupVO> create(@Valid @RequestBody CreateGroupRequest request) {
        GroupVO data = new GroupVO();
        data.setGroupId(System.currentTimeMillis());
        data.setName(request.getName());
        data.setDescription(request.getDescription());
        data.setLeaderId(10001L);
        data.setLeaderName("组长");
        data.setInviteCode("ABC123");
        data.setQrCodeUrl("https://cdn.rocgit.com/qr/abc123.png");
        data.setCreatedAt(LocalDateTime.now());
        return Result.success(data);
    }

    @Operation(summary = "加入小组", description = "通过邀请码加入小组")
    @PostMapping("/join")
    public Result<Map<String, Object>> join(@Valid @RequestBody JoinGroupRequest request) {
        Map<String, Object> data = new HashMap<>();
        data.put("groupId", "group_001");
        data.put("groupName", "课程设计第十二组");
        data.put("joinedAt", System.currentTimeMillis());
        return Result.success(data);
    }

    @Operation(summary = "校验邀请码", description = "校验邀请码是否有效")
    @PostMapping("/validate-invite-code")
    public Result<ValidateInviteCodeVO> validateInviteCode(@Valid @RequestBody ValidateInviteCodeRequest request) {
        ValidateInviteCodeVO data = new ValidateInviteCodeVO();
        data.setValid(true);
        data.setGroupName("课程设计第十二组");
        data.setMemberCount(5);
        data.setLeaderName("张三");
        return Result.success(data);
    }

    @Operation(summary = "我的小组列表", description = "获取当前用户加入的所有小组")
    @GetMapping("/my-list")
    public Result<List<GroupVO>> myList() {
        List<GroupVO> list = new ArrayList<>();
        GroupVO g = new GroupVO();
        g.setGroupId(1L);
        g.setName("课程设计第十二组");
        g.setLeaderId(10001L);
        g.setLeaderName("张三");
        g.setMemberCount(5);
        g.setProjectCount(3);
        g.setInviteCode("ABC123");
        list.add(g);
        return Result.success(list);
    }

    @Operation(summary = "小组详情", description = "获取指定小组的详细信息")
    @Parameter(name = "groupId", description = "小组ID", required = true, example = "1")
    @GetMapping("/{groupId}")
    public Result<GroupVO> detail(@PathVariable Long groupId) {
        GroupVO data = new GroupVO();
        data.setGroupId(groupId);
        data.setName("课程设计第十二组");
        data.setLeaderId(10001L);
        data.setLeaderName("张三");
        data.setMemberCount(5);
        data.setProjectCount(3);
        data.setCreatedAt(LocalDateTime.now());
        return Result.success(data);
    }

    @Operation(summary = "更新小组", description = "更新小组信息")
    @PutMapping("/{groupId}")
    public Result<Void> update(@PathVariable Long groupId, @Valid @RequestBody UpdateGroupRequest request) {
        return Result.success("更新成功", null);
    }

    @Operation(summary = "退出小组", description = "当前用户退出小组")
    @PostMapping("/{groupId}/leave")
    public Result<Void> leave(@PathVariable Long groupId) {
        return Result.success("已退出", null);
    }

    @Operation(summary = "小组成员列表", description = "获取小组的所有成员")
    @GetMapping("/{groupId}/members")
    public Result<Map<String, Object>> members(@PathVariable Long groupId,
                                               @RequestParam(defaultValue = "1") Integer pageNum,
                                               @RequestParam(defaultValue = "20") Integer pageSize) {
        MemberVO m1 = new MemberVO();
        m1.setUserId(10001L);
        m1.setNickname("张三");
        m1.setAvatarUrl("https://cdn.rocgit.com/avatar/1.jpg");
        m1.setRole("leader");
        m1.setJoinedAt(LocalDateTime.now().minusMonths(6));
        m1.setStatus("active");
        m1.setRemark("组长");
        Map<String, Object> data = new HashMap<>();
        data.put("total", 5);
        data.put("members", Arrays.asList(m1));
        return Result.success(data);
    }

    @Operation(summary = "修改成员备注", description = "组长修改成员的备注名")
    @PutMapping("/{groupId}/member/{userId}/remark")
    public Result<Void> updateRemark(@PathVariable Long groupId, @PathVariable Long userId,
                                     @Valid @RequestBody UpdateRemarkRequest request) {
        return Result.success("备注已更新", null);
    }

    @Operation(summary = "转让组长", description = "组长转让给其他成员")
    @PostMapping("/{groupId}/transfer-leader")
    public Result<Void> transferLeader(@PathVariable Long groupId, @Valid @RequestBody TransferLeaderRequest request) {
        return Result.success("转让成功", null);
    }

    @Operation(summary = "重新生成邀请码", description = "生成新的邀请码，旧码失效")
    @PostMapping("/{groupId}/invite-code")
    public Result<RegenerateInviteCodeVO> regenerateInviteCode(@PathVariable Long groupId) {
        RegenerateInviteCodeVO data = new RegenerateInviteCodeVO();
        data.setInviteCode("NEW" + System.currentTimeMillis() % 100000);
        data.setQrCodeUrl("https://cdn.rocgit.com/qr/new.png");
        data.setExpiresAt(LocalDateTime.now().plusDays(7));
        return Result.success(data);
    }

    @Operation(summary = "小组统计", description = "小组的整体统计数据")
    @GetMapping("/{groupId}/stats")
    public Result<GroupStatsVO> stats(@PathVariable Long groupId) {
        GroupStatsVO data = new GroupStatsVO();
        data.setMemberCount(5);
        data.setProjectCount(3);
        data.setActiveProjectCount(2);
        data.setCompletedProjectCount(1);
        data.setTotalFileCount(28);
        data.setTotalTaskCount(20);
        return Result.success(data);
    }

    @Operation(summary = "成员贡献排行", description = "小组成员的贡献排行榜")
    @GetMapping("/{groupId}/member-stats")
    public Result<List<MemberStatsVO>> memberStats(@PathVariable Long groupId) {
        List<MemberStatsVO> list = new ArrayList<>();
        MemberStatsVO m = new MemberStatsVO();
        m.setUserId(10001L);
        m.setNickname("张三");
        m.setContributions(567);
        m.setFileCount(15);
        m.setTaskCount(10);
        m.setRank(1);
        list.add(m);
        return Result.success(list);
    }
}
