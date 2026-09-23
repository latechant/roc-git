package com.rocgit.knife4j.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rocgit.knife4j.common.BusinessException;
import com.rocgit.knife4j.common.ResultCode;
import com.rocgit.knife4j.entity.group.GroupStatsVO;
import com.rocgit.knife4j.entity.group.GroupVO;
import com.rocgit.knife4j.entity.group.MemberStatsVO;
import com.rocgit.knife4j.entity.group.MemberVO;
import com.rocgit.knife4j.entity.group.RegenerateInviteCodeVO;
import com.rocgit.knife4j.entity.group.ValidateInviteCodeVO;
import com.rocgit.knife4j.mapper.FileMapper;
import com.rocgit.knife4j.mapper.GroupMapper;
import com.rocgit.knife4j.mapper.GroupMemberMapper;
import com.rocgit.knife4j.mapper.ProjectMapper;
import com.rocgit.knife4j.mapper.TaskMapper;
import com.rocgit.knife4j.mapper.UserMapper;
import com.rocgit.knife4j.model.File;
import com.rocgit.knife4j.model.Group;
import com.rocgit.knife4j.model.GroupMember;
import com.rocgit.knife4j.model.Project;
import com.rocgit.knife4j.model.Task;
import com.rocgit.knife4j.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * 小组服务 - 接入真实 MySQL
 */
@Service
public class GroupService {

    /** 邀请码字符集（字母+数字，去掉易混淆的 0/O、1/l/I） */
    private static final String INVITE_CHARS =
            "ABCDEFGHJKMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
    private static final int INVITE_CODE_LEN = 6;
    private static final int INVITE_CODE_RETRY = 20;

    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private GroupMemberMapper groupMemberMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private TaskMapper taskMapper;

    // ============================ 创建 / 加入 ============================

    /**
     * 创建小组：当前用户为组长，生成唯一邀请码，并写入 group_member(leader)
     */
    @Transactional
    public GroupVO create(Long userId, String name, String description) {
        String inviteCode = generateUniqueInviteCode();

        Group group = new Group();
        group.setName(name);
        group.setDescription(description);
        group.setLeaderId(userId);
        group.setInviteCode(inviteCode);
        group.setQrCodeUrl(buildQrCodeUrl(inviteCode));
        group.setStatus("active");
        group.setCreatedAt(LocalDateTime.now());
        groupMapper.insert(group);

        GroupMember leader = new GroupMember();
        leader.setGroupId(group.getId());
        leader.setUserId(userId);
        leader.setRole("leader");
        leader.setStatus("active");
        leader.setJoinedAt(LocalDateTime.now());
        groupMemberMapper.insert(leader);

        return fillGroupVO(group, true, true);
    }

    /**
     * 通过邀请码加入小组
     */
    @Transactional
    public Map<String, Object> join(Long userId, String inviteCode) {
        Group group = groupMapper.selectOne(
                new LambdaQueryWrapper<Group>().eq(Group::getInviteCode, inviteCode));
        if (group == null) {
            throw new BusinessException(ResultCode.INVITE_CODE_INVALID);
        }

        LocalDateTime joinedAt;
        GroupMember member = groupMemberMapper.selectOne(
                new LambdaQueryWrapper<GroupMember>()
                        .eq(GroupMember::getGroupId, group.getId())
                        .eq(GroupMember::getUserId, userId));
        if (member == null) {
            member = new GroupMember();
            member.setGroupId(group.getId());
            member.setUserId(userId);
            member.setRole("member");
            member.setStatus("active");
            joinedAt = LocalDateTime.now();
            member.setJoinedAt(joinedAt);
            groupMemberMapper.insert(member);
        } else {
            member.setStatus("active");
            groupMemberMapper.updateById(member);
            joinedAt = member.getJoinedAt();
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("groupId", group.getId());
        data.put("groupName", group.getName());
        data.put("joinedAt", joinedAt);
        return data;
    }

    /**
     * 校验邀请码
     */
    public ValidateInviteCodeVO validateInviteCode(String inviteCode) {
        ValidateInviteCodeVO vo = new ValidateInviteCodeVO();
        Group group = groupMapper.selectOne(
                new LambdaQueryWrapper<Group>().eq(Group::getInviteCode, inviteCode));
        if (group == null) {
            vo.setValid(false);
            vo.setGroupName(null);
            vo.setMemberCount(0);
            vo.setLeaderName(null);
            return vo;
        }
        vo.setValid(true);
        vo.setGroupName(group.getName());
        vo.setMemberCount(countActiveMembers(group.getId()));
        vo.setLeaderName(getLeaderName(group.getLeaderId()));
        return vo;
    }

    // ============================ 查询 ============================

    /**
     * 我加入的小组列表
     */
    public List<GroupVO> myList(Long userId) {
        List<GroupMember> memberships = groupMemberMapper.selectList(
                new LambdaQueryWrapper<GroupMember>()
                        .eq(GroupMember::getUserId, userId)
                        .eq(GroupMember::getStatus, "active"));
        if (memberships.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> groupIds = memberships.stream()
                .map(GroupMember::getGroupId).collect(Collectors.toList());
        List<Group> groups = groupMapper.selectList(
                new LambdaQueryWrapper<Group>().in(Group::getId, groupIds));
        List<GroupVO> result = new ArrayList<>();
        for (Group g : groups) {
            result.add(fillGroupVO(g, true, true));
        }
        return result;
    }

    /**
     * 小组详情
     */
    public GroupVO detail(Long groupId) {
        Group group = getGroupOrThrow(groupId);
        return fillGroupVO(group, true, true);
    }

    // ============================ 更新 / 成员管理 ============================

    /**
     * 更新小组信息（仅组长）
     */
    @Transactional
    public void update(Long groupId, Long userId, String name, String description) {
        Group group = getGroupOrThrow(groupId);
        checkLeader(group, userId);

        Group update = new Group();
        update.setId(groupId);
        if (name != null) update.setName(name);
        if (description != null) update.setDescription(description);
        update.setUpdatedAt(LocalDateTime.now());
        groupMapper.updateById(update);
    }

    /**
     * 退出小组（组长不可退出）
     */
    @Transactional
    public void leave(Long groupId, Long userId) {
        Group group = getGroupOrThrow(groupId);
        if (group.getLeaderId() != null && group.getLeaderId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "组长不能退出小组，请先转让组长");
        }
        GroupMember member = groupMemberMapper.selectOne(
                new LambdaQueryWrapper<GroupMember>()
                        .eq(GroupMember::getGroupId, groupId)
                        .eq(GroupMember::getUserId, userId));
        if (member == null || !"active".equals(member.getStatus())) {
            throw new BusinessException(ResultCode.NOT_GROUP_MEMBER);
        }
        member.setStatus("removed");
        groupMemberMapper.updateById(member);
    }

    /**
     * 小组成员列表（分页）
     */
    public Map<String, Object> members(Long groupId, Integer pageNum, Integer pageSize) {
        getGroupOrThrow(groupId);
        List<GroupMember> all = groupMemberMapper.selectList(
                new LambdaQueryWrapper<GroupMember>()
                        .eq(GroupMember::getGroupId, groupId)
                        .eq(GroupMember::getStatus, "active")
                        .orderByAsc(GroupMember::getJoinedAt));

        long total = all.size();
        int pn = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int ps = pageSize == null || pageSize < 1 ? 20 : pageSize;
        int fromIndex = Math.max(0, (pn - 1) * ps);
        int toIndex = Math.min(fromIndex + ps, all.size());
        List<GroupMember> page = fromIndex < all.size()
                ? all.subList(fromIndex, toIndex) : Collections.emptyList();

        // 批量加载用户信息
        List<Long> userIds = page.stream().map(GroupMember::getUserId).collect(Collectors.toList());
        Map<Long, User> userMap = userIds.isEmpty()
                ? Collections.emptyMap()
                : userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u, (a, b) -> a));

        List<MemberVO> members = new ArrayList<>();
        for (GroupMember m : page) {
            MemberVO vo = new MemberVO();
            vo.setUserId(m.getUserId());
            User u = userMap.get(m.getUserId());
            if (u != null) {
                vo.setNickname(u.getNickname());
                vo.setAvatarUrl(u.getAvatarUrl());
            }
            vo.setRole(m.getRole());
            vo.setJoinedAt(m.getJoinedAt());
            vo.setStatus(m.getStatus());
            vo.setRemark(m.getRemark());
            members.add(vo);
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("total", total);
        data.put("members", members);
        return data;
    }

    /**
     * 修改成员备注（仅组长）
     */
    @Transactional
    public void updateRemark(Long groupId, Long userId, Long targetUserId, String remark) {
        Group group = getGroupOrThrow(groupId);
        checkLeader(group, userId);

        GroupMember member = groupMemberMapper.selectOne(
                new LambdaQueryWrapper<GroupMember>()
                        .eq(GroupMember::getGroupId, groupId)
                        .eq(GroupMember::getUserId, targetUserId));
        if (member == null) {
            throw new BusinessException(ResultCode.NOT_GROUP_MEMBER);
        }
        member.setRemark(remark);
        groupMemberMapper.updateById(member);
    }

    /**
     * 转让组长
     */
    @Transactional
    public void transferLeader(Long groupId, Long userId, Long newLeaderId) {
        Group group = getGroupOrThrow(groupId);
        checkLeader(group, userId);

        GroupMember newLeader = groupMemberMapper.selectOne(
                new LambdaQueryWrapper<GroupMember>()
                        .eq(GroupMember::getGroupId, groupId)
                        .eq(GroupMember::getUserId, newLeaderId)
                        .eq(GroupMember::getStatus, "active"));
        if (newLeader == null) {
            throw new BusinessException(ResultCode.NOT_GROUP_MEMBER, "新组长不是该小组成员");
        }

        // 更新 group.leader_id
        Group update = new Group();
        update.setId(groupId);
        update.setLeaderId(newLeaderId);
        update.setUpdatedAt(LocalDateTime.now());
        groupMapper.updateById(update);

        // 原组长 -> member
        GroupMember oldLeader = groupMemberMapper.selectOne(
                new LambdaQueryWrapper<GroupMember>()
                        .eq(GroupMember::getGroupId, groupId)
                        .eq(GroupMember::getUserId, userId));
        if (oldLeader != null) {
            oldLeader.setRole("member");
            groupMemberMapper.updateById(oldLeader);
        }

        // 新组长 -> leader
        newLeader.setRole("leader");
        groupMemberMapper.updateById(newLeader);
    }

    /**
     * 重新生成邀请码
     */
    @Transactional
    public RegenerateInviteCodeVO regenerateInviteCode(Long groupId, Long userId) {
        Group group = getGroupOrThrow(groupId);
        // 仅小组成员可重新生成
        GroupMember member = groupMemberMapper.selectOne(
                new LambdaQueryWrapper<GroupMember>()
                        .eq(GroupMember::getGroupId, groupId)
                        .eq(GroupMember::getUserId, userId)
                        .eq(GroupMember::getStatus, "active"));
        if (member == null) {
            throw new BusinessException(ResultCode.NOT_GROUP_MEMBER);
        }

        String inviteCode = generateUniqueInviteCode();
        Group update = new Group();
        update.setId(groupId);
        update.setInviteCode(inviteCode);
        update.setQrCodeUrl(buildQrCodeUrl(inviteCode));
        update.setUpdatedAt(LocalDateTime.now());
        groupMapper.updateById(update);

        RegenerateInviteCodeVO vo = new RegenerateInviteCodeVO();
        vo.setInviteCode(inviteCode);
        vo.setQrCodeUrl(buildQrCodeUrl(inviteCode));
        vo.setExpiresAt(LocalDateTime.now().plusDays(7));
        return vo;
    }

    // ============================ 统计 ============================

    /**
     * 小组整体统计
     */
    public GroupStatsVO stats(Long groupId) {
        getGroupOrThrow(groupId);

        Integer memberCount = countActiveMembers(groupId);

        List<Project> projects = projectMapper.selectList(
                new LambdaQueryWrapper<Project>().eq(Project::getGroupId, groupId));
        int projectCount = projects.size();
        int activeProjectCount = 0;
        int completedProjectCount = 0;
        for (Project p : projects) {
            if ("completed".equalsIgnoreCase(p.getStatus())) {
                completedProjectCount++;
            } else if ("active".equalsIgnoreCase(p.getStatus())) {
                activeProjectCount++;
            }
        }

        // 子查询：该组所有项目 id
        String projectIdsSubSql = "SELECT id FROM project WHERE group_id = " + groupId;
        Integer totalFileCount = Math.toIntExact(fileMapper.selectCount(
                new LambdaQueryWrapper<File>().inSql(File::getProjectId, projectIdsSubSql)));
        Integer totalTaskCount = Math.toIntExact(taskMapper.selectCount(
                new LambdaQueryWrapper<Task>().inSql(Task::getProjectId, projectIdsSubSql)));

        GroupStatsVO vo = new GroupStatsVO();
        vo.setMemberCount(memberCount);
        vo.setProjectCount(projectCount);
        vo.setActiveProjectCount(activeProjectCount);
        vo.setCompletedProjectCount(completedProjectCount);
        vo.setTotalFileCount(totalFileCount);
        vo.setTotalTaskCount(totalTaskCount);
        return vo;
    }

    /**
     * 成员贡献排行
     */
    public List<MemberStatsVO> memberStats(Long groupId) {
        getGroupOrThrow(groupId);

        List<GroupMember> members = groupMemberMapper.selectList(
                new LambdaQueryWrapper<GroupMember>()
                        .eq(GroupMember::getGroupId, groupId)
                        .eq(GroupMember::getStatus, "active"));

        List<Long> projectIds = projectMapper.selectList(
                        new LambdaQueryWrapper<Project>().select(Project::getId).eq(Project::getGroupId, groupId))
                .stream().map(Project::getId).collect(Collectors.toList());

        List<Long> userIds = members.stream().map(GroupMember::getUserId).collect(Collectors.toList());
        Map<Long, User> userMap = userIds.isEmpty()
                ? Collections.emptyMap()
                : userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u, (a, b) -> a));

        List<MemberStatsVO> list = new ArrayList<>();
        for (GroupMember m : members) {
            int fileCount = 0;
            int taskCount = 0;
            if (!projectIds.isEmpty()) {
                fileCount = Math.toIntExact(fileMapper.selectCount(
                        new LambdaQueryWrapper<File>()
                                .eq(File::getUploaderId, m.getUserId())
                                .in(File::getProjectId, projectIds)));
                taskCount = Math.toIntExact(taskMapper.selectCount(
                        new LambdaQueryWrapper<Task>()
                                .eq(Task::getAssigneeId, m.getUserId())
                                .in(Task::getProjectId, projectIds)));
            }
            MemberStatsVO vo = new MemberStatsVO();
            vo.setUserId(m.getUserId());
            User u = userMap.get(m.getUserId());
            if (u != null) {
                vo.setNickname(u.getNickname());
                vo.setAvatarUrl(u.getAvatarUrl());
            }
            vo.setFileCount(fileCount);
            vo.setTaskCount(taskCount);
            vo.setContributions(fileCount + taskCount);
            list.add(vo);
        }

        list.sort(Comparator.comparingInt(MemberStatsVO::getContributions).reversed());
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setRank(i + 1);
        }
        return list;
    }

    // ============================ 私有工具方法 ============================

    /** 组装 GroupVO（含组长昵称、成员数、项目数） */
    private GroupVO fillGroupVO(Group group, boolean withLeaderName, boolean withCounts) {
        GroupVO vo = new GroupVO();
        vo.setGroupId(group.getId());
        vo.setName(group.getName());
        vo.setDescription(group.getDescription());
        vo.setLeaderId(group.getLeaderId());
        vo.setInviteCode(group.getInviteCode());
        vo.setQrCodeUrl(group.getQrCodeUrl() != null
                ? group.getQrCodeUrl() : buildQrCodeUrl(group.getInviteCode()));
        vo.setCreatedAt(group.getCreatedAt());
        if (withLeaderName) {
            vo.setLeaderName(getLeaderName(group.getLeaderId()));
        }
        if (withCounts) {
            vo.setMemberCount(countActiveMembers(group.getId()));
            vo.setProjectCount(countProjects(group.getId()));
        }
        return vo;
    }

    private Group getGroupOrThrow(Long groupId) {
        Group group = groupMapper.selectById(groupId);
        if (group == null) {
            throw new BusinessException(ResultCode.GROUP_NOT_FOUND);
        }
        return group;
    }

    private void checkLeader(Group group, Long userId) {
        if (group.getLeaderId() == null || !group.getLeaderId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "仅组长可执行此操作");
        }
    }

    private Integer countActiveMembers(Long groupId) {
        return Math.toIntExact(groupMemberMapper.selectCount(
                new LambdaQueryWrapper<GroupMember>()
                        .eq(GroupMember::getGroupId, groupId)
                        .eq(GroupMember::getStatus, "active")));
    }

    private Integer countProjects(Long groupId) {
        return Math.toIntExact(projectMapper.selectCount(
                new LambdaQueryWrapper<Project>().eq(Project::getGroupId, groupId)));
    }

    private String getLeaderName(Long leaderId) {
        if (leaderId == null) return null;
        User leader = userMapper.selectById(leaderId);
        return leader != null ? leader.getNickname() : null;
    }

    private String buildQrCodeUrl(String inviteCode) {
        return "https://cdn.rocgit.com/qr/" + inviteCode.toLowerCase() + ".png";
    }

    /** 生成 6 位字母数字邀请码，并保证全局唯一 */
    private String generateUniqueInviteCode() {
        for (int i = 0; i < INVITE_CODE_RETRY; i++) {
            StringBuilder sb = new StringBuilder(INVITE_CODE_LEN);
            for (int j = 0; j < INVITE_CODE_LEN; j++) {
                sb.append(INVITE_CHARS.charAt(
                        ThreadLocalRandom.current().nextInt(INVITE_CHARS.length())));
            }
            String code = sb.toString();
            Long cnt = groupMapper.selectCount(
                    new LambdaQueryWrapper<Group>().eq(Group::getInviteCode, code));
            if (cnt == null || cnt == 0L) {
                return code;
            }
        }
        throw new BusinessException(ResultCode.FAIL, "邀请码生成失败，请重试");
    }
}
