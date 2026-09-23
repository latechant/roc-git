package com.rocgit.knife4j.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rocgit.knife4j.entity.user.ContributionStatsVO;
import com.rocgit.knife4j.entity.user.LoginVO;
import com.rocgit.knife4j.entity.user.ProfileVO;
import com.rocgit.knife4j.mapper.*;
import com.rocgit.knife4j.model.*;
import com.rocgit.knife4j.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 用户服务
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private GroupMemberMapper groupMemberMapper;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private DiscussionMapper discussionMapper;
    @Autowired
    private UserSubscriptionMapper userSubscriptionMapper;
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 微信登录：按 openid 查/建 user
     * 课程项目简化：用 code 直接作为 openid 标识（实际生产应调用微信 code2session 接口）
     */
    @Transactional
    public LoginVO login(String code, String nickname, String avatarUrl) {
        String openid = "wx_" + code;

        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getOpenid, openid));

        boolean isNew = false;
        if (user == null) {
            user = new User();
            user.setOpenid(openid);
            user.setNickname(nickname);
            user.setAvatarUrl(avatarUrl);
            userMapper.insert(user);
            isNew = true;

            // 创建默认订阅设置
            UserSubscription sub = new UserSubscription();
            sub.setUserId(user.getId());
            sub.setAuditNotify(1);
            sub.setTaskRemind(1);
            sub.setMemberNotify(1);
            sub.setStageNotify(1);
            sub.setMentionNotify(1);
            userSubscriptionMapper.insert(sub);
        } else {
            // 每次登录更新昵称头像
            user.setNickname(nickname);
            user.setAvatarUrl(avatarUrl);
            userMapper.updateById(user);
        }

        LoginVO vo = new LoginVO();
        vo.setToken(jwtUtil.generateToken(String.valueOf(user.getId()), user.getNickname()));
        vo.setUserId(user.getId());
        vo.setOpenid(user.getOpenid());
        vo.setNickname(user.getNickname());
        vo.setAvatarUrl(user.getAvatarUrl());
        vo.setIsNewUser(isNew);
        return vo;
    }

    /**
     * 获取个人信息
     */
    public ProfileVO getProfile(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return null;
        }

        Integer groupCount = Math.toIntExact(groupMemberMapper.selectCount(
                new LambdaQueryWrapper<GroupMember>()
                        .eq(GroupMember::getUserId, userId)
                        .eq(GroupMember::getStatus, "active")));

        // 用户参与的项目数 = 所在小组的项目数
        Integer projectCount = Math.toIntExact(projectMapper.selectCount(
                new LambdaQueryWrapper<Project>()
                        .inSql(Project::getGroupId,
                                "SELECT group_id FROM group_member WHERE user_id = " + userId + " AND status = 'active'")));

        ProfileVO vo = new ProfileVO();
        vo.setUserId(user.getId());
        vo.setOpenid(user.getOpenid());
        vo.setNickname(user.getNickname());
        vo.setAvatarUrl(user.getAvatarUrl());
        vo.setCreatedAt(user.getCreatedAt());
        vo.setGroupCount(groupCount);
        vo.setProjectCount(projectCount);
        return vo;
    }

    /**
     * 更新个人信息
     */
    public void updateProfile(Long userId, String nickname, String avatarUrl) {
        User user = new User();
        user.setId(userId);
        if (nickname != null) user.setNickname(nickname);
        if (avatarUrl != null) user.setAvatarUrl(avatarUrl);
        userMapper.updateById(user);
    }

    /**
     * 贡献统计
     */
    public ContributionStatsVO getContributionStats(Long userId) {
        User user = userMapper.selectById(userId);

        Integer uploadedFiles = Math.toIntExact(fileMapper.selectCount(
                new LambdaQueryWrapper<File>().eq(File::getUploaderId, userId)));

        Integer approvedFiles = Math.toIntExact(fileMapper.selectCount(
                new LambdaQueryWrapper<File>()
                        .eq(File::getUploaderId, userId)
                        .eq(File::getStatus, "approved")));

        Integer completedTasks = Math.toIntExact(taskMapper.selectCount(
                new LambdaQueryWrapper<Task>()
                        .eq(Task::getAssigneeId, userId)
                        .eq(Task::getStatus, "completed")));

        Integer discussions = Math.toIntExact(discussionMapper.selectCount(
                new LambdaQueryWrapper<Discussion>().eq(Discussion::getCreatedBy, userId)));

        ContributionStatsVO vo = new ContributionStatsVO();
        vo.setUploadedFiles(uploadedFiles);
        vo.setApprovedFiles(approvedFiles);
        vo.setCompletedTasks(completedTasks);
        vo.setDiscussions(discussions);
        if (user != null && user.getCreatedAt() != null) {
            vo.setJoinDays((int) ChronoUnit.DAYS.between(user.getCreatedAt().toLocalDate(), LocalDateTime.now().toLocalDate()));
        } else {
            vo.setJoinDays(0);
        }
        vo.setLastActiveTime(LocalDateTime.now());
        return vo;
    }
}
