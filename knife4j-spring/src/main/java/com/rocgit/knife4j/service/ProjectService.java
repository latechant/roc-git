package com.rocgit.knife4j.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rocgit.knife4j.common.BusinessException;
import com.rocgit.knife4j.common.ResultCode;
import com.rocgit.knife4j.dto.project.*;
import com.rocgit.knife4j.entity.project.*;
import com.rocgit.knife4j.mapper.*;
import com.rocgit.knife4j.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 项目服务 — 项目 CRUD + 阶段管理 + 模板 + 统计
 */
@Service
public class ProjectService {

    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private StageMapper stageMapper;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private GroupMemberMapper groupMemberMapper;
    @Autowired
    private StageTemplateMapper stageTemplateMapper;
    @Autowired
    private OperationLogMapper operationLogMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ProjectMemberMapper projectMemberMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ==================== 项目 CRUD ====================

    @Transactional
    public ProjectDetailVO createProject(CreateProjectRequest request, Long userId) {
        Project project = new Project();
        project.setGroupId(request.getGroupId());
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setStatus("active");
        project.setProgress(0);
        project.setCreatedBy(userId);
        project.setIsShared(0);
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        // 审核/提醒规则：非空则序列化为 JSON 存列
        try {
            project.setAuditRule(request.getAuditRule() != null
                    ? objectMapper.writeValueAsString(request.getAuditRule()) : null);
            project.setReminderRule(request.getReminderRule() != null
                    ? objectMapper.writeValueAsString(request.getReminderRule()) : null);
        } catch (Exception e) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "规则序列化失败: " + e.getMessage());
        }
        projectMapper.insert(project);

        // 插入阶段：优先用模板，其次用 request 自带的 stages
        if (request.getTemplateId() != null) {
            applyTemplateStages(project.getId(), request.getTemplateId());
        } else if (request.getStages() != null && !request.getStages().isEmpty()) {
            int order = 1;
            for (CreateProjectRequest.StageItem item : request.getStages()) {
                Stage stage = new Stage();
                stage.setProjectId(project.getId());
                stage.setName(item.getName());
                stage.setOrderNum(item.getOrderNum() != null ? item.getOrderNum() : order);
                stage.setStatus("pending");
                stage.setCreatedAt(LocalDateTime.now());
                stage.setUpdatedAt(LocalDateTime.now());
                stageMapper.insert(stage);
                order++;
            }
        }

        // 初始化第一个阶段为 active + 维护 current_stage_id（阶段推进闭环起点）
        List<Stage> firsts = stageMapper.selectList(new LambdaQueryWrapper<Stage>()
                .eq(Stage::getProjectId, project.getId())
                .orderByAsc(Stage::getOrderNum));
        if (!firsts.isEmpty()) {
            Stage first = firsts.get(0);
            first.setStatus("active");
            first.setUpdatedAt(LocalDateTime.now());
            stageMapper.updateById(first);
            project.setCurrentStageId(first.getId());
            project.setUpdatedAt(LocalDateTime.now());
            projectMapper.updateById(project);
        }

        // 插入项目成员：可选；非空时逐条校验必须是该小组 active 成员，再批量写入
        if (request.getMembers() != null && !request.getMembers().isEmpty()) {
            for (CreateProjectRequest.MemberItem member : request.getMembers()) {
                Long cnt = groupMemberMapper.selectCount(new LambdaQueryWrapper<GroupMember>()
                        .eq(GroupMember::getGroupId, request.getGroupId())
                        .eq(GroupMember::getUserId, member.getUserId())
                        .eq(GroupMember::getStatus, "active"));
                if (cnt == null || cnt == 0) {
                    throw new BusinessException(ResultCode.BAD_REQUEST,
                            "成员不在小组内：userId=" + member.getUserId());
                }
            }
            LocalDateTime now = LocalDateTime.now();
            for (CreateProjectRequest.MemberItem member : request.getMembers()) {
                ProjectMember pm = new ProjectMember();
                pm.setProjectId(project.getId());
                pm.setUserId(member.getUserId());
                pm.setRole(member.getRole() != null ? member.getRole() : "member");
                pm.setJoinedAt(now);
                projectMemberMapper.insert(pm);
            }
        }

        return buildProjectDetail(project);
    }

    public List<ProjectMemberVO> getProjectMembers(Long projectId) {
        List<ProjectMember> list = projectMemberMapper.selectList(
                new LambdaQueryWrapper<ProjectMember>()
                        .eq(ProjectMember::getProjectId, projectId)
                        .orderByAsc(ProjectMember::getJoinedAt)
                        .orderByAsc(ProjectMember::getId));
        List<ProjectMemberVO> result = new ArrayList<>();
        for (ProjectMember pm : list) {
            ProjectMemberVO vo = new ProjectMemberVO();
            vo.setUserId(pm.getUserId());
            vo.setRole(pm.getRole());
            vo.setJoinedAt(pm.getJoinedAt());
            User u = userMapper.selectById(pm.getUserId());
            vo.setNickname(u != null ? u.getNickname() : null);
            result.add(vo);
        }
        return result;
    }

    public List<ProjectVO> listProjects(Long groupId) {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<Project>()
                .eq(Project::getGroupId, groupId)
                .ne(Project::getStatus, "archived")
                .orderByDesc(Project::getCreatedAt);
        List<Project> list = projectMapper.selectList(wrapper);
        return list.stream().map(this::toProjectVO).collect(Collectors.toList());
    }

    public ProjectDetailVO getProjectDetail(Long projectId) {
        Project project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new BusinessException(ResultCode.PROJECT_NOT_FOUND);
        }
        return buildProjectDetail(project);
    }

    @Transactional
    public void updateProject(Long projectId, UpdateProjectRequest request) {
        Project project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new BusinessException(ResultCode.PROJECT_NOT_FOUND);
        }
        if (request.getName() != null) project.setName(request.getName());
        if (request.getDescription() != null) project.setDescription(request.getDescription());
        project.setUpdatedAt(LocalDateTime.now());
        projectMapper.updateById(project);
    }

    @Transactional
    public void deleteProject(Long projectId) {
        Project project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new BusinessException(ResultCode.PROJECT_NOT_FOUND);
        }
        projectMapper.deleteById(projectId);
    }

    @Transactional
    public ProjectVO copyProject(Long projectId, CopyProjectRequest request) {
        Project source = projectMapper.selectById(projectId);
        if (source == null) {
            throw new BusinessException(ResultCode.PROJECT_NOT_FOUND);
        }

        Project copy = new Project();
        copy.setGroupId(source.getGroupId());
        copy.setName(request.getName());
        copy.setDescription(source.getDescription());
        copy.setStatus("active");
        copy.setProgress(0);
        copy.setCreatedBy(source.getCreatedBy());
        copy.setIsShared(0);
        copy.setCreatedAt(LocalDateTime.now());
        copy.setUpdatedAt(LocalDateTime.now());
        projectMapper.insert(copy);

        // 级联复制阶段
        List<Stage> stages = stageMapper.selectList(
                new LambdaQueryWrapper<Stage>().eq(Stage::getProjectId, projectId).orderByAsc(Stage::getOrderNum));
        for (Stage s : stages) {
            Stage ns = new Stage();
            ns.setProjectId(copy.getId());
            ns.setName(s.getName());
            ns.setOrderNum(s.getOrderNum());
            ns.setStatus("pending");
            ns.setStartDate(s.getStartDate());
            ns.setEndDate(s.getEndDate());
            ns.setRemark(s.getRemark());
            ns.setCreatedAt(LocalDateTime.now());
            ns.setUpdatedAt(LocalDateTime.now());
            stageMapper.insert(ns);
        }

        return toProjectVO(copy);
    }

    @Transactional
    public void completeProject(Long projectId) {
        Project project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new BusinessException(ResultCode.PROJECT_NOT_FOUND);
        }
        project.setStatus("completed");
        project.setProgress(100);
        project.setUpdatedAt(LocalDateTime.now());
        projectMapper.updateById(project);
    }

    @Transactional
    public void archiveProject(Long projectId) {
        Project project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new BusinessException(ResultCode.PROJECT_NOT_FOUND);
        }
        project.setStatus("archived");
        project.setArchivedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        projectMapper.updateById(project);
    }

    // ==================== 项目统计 ====================

    public Map<String, Object> getProjectProgress(Long projectId) {
        Project project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new BusinessException(ResultCode.PROJECT_NOT_FOUND);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("overall", project.getProgress());

        Map<String, Object> fileStats = new HashMap<>();
        Long fileTotal = fileMapper.selectCount(
                new LambdaQueryWrapper<File>().eq(File::getProjectId, projectId));
        Long fileApproved = fileMapper.selectCount(
                new LambdaQueryWrapper<File>().eq(File::getProjectId, projectId).eq(File::getStatus, "approved"));
        fileStats.put("total", Math.toIntExact(fileTotal));
        fileStats.put("approved", Math.toIntExact(fileApproved));
        data.put("fileStats", fileStats);

        Map<String, Object> taskStats = new HashMap<>();
        Long taskTotal = taskMapper.selectCount(
                new LambdaQueryWrapper<Task>().eq(Task::getProjectId, projectId));
        Long taskCompleted = taskMapper.selectCount(
                new LambdaQueryWrapper<Task>().eq(Task::getProjectId, projectId).eq(Task::getStatus, "completed"));
        taskStats.put("total", Math.toIntExact(taskTotal));
        taskStats.put("completed", Math.toIntExact(taskCompleted));
        data.put("taskStats", taskStats);

        return data;
    }

    public List<ActivityVO> getProjectActivities(Long projectId, String type, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<OperationLog>()
                .eq(OperationLog::getProjectId, projectId)
                .orderByDesc(OperationLog::getCreatedAt);
        if (type != null && !type.isEmpty()) {
            wrapper.eq(OperationLog::getTargetType, type);
        }
        List<OperationLog> all = operationLogMapper.selectList(wrapper);

        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, all.size());
        List<OperationLog> pageList = fromIndex >= all.size() ? new ArrayList<>() : all.subList(fromIndex, toIndex);

        List<ActivityVO> result = new ArrayList<>();
        for (OperationLog log : pageList) {
            ActivityVO vo = new ActivityVO();
            vo.setId(log.getId());
            vo.setType(log.getTargetType());
            vo.setAction(log.getAction());
            vo.setUserId(log.getUserId());
            vo.setTargetId(log.getTargetId());
            vo.setTargetName(log.getTargetName());
            vo.setCreatedAt(log.getCreatedAt() != null ? log.getCreatedAt().toString() : null);
            if (log.getUserId() != null) {
                User u = userMapper.selectById(log.getUserId());
                if (u != null) vo.setUserName(u.getNickname());
            }
            result.add(vo);
        }
        return result;
    }

    public Map<String, Object> getTaskStats(Long projectId) {
        List<Task> tasks = taskMapper.selectList(
                new LambdaQueryWrapper<Task>().eq(Task::getProjectId, projectId));
        Map<String, Object> data = new HashMap<>();
        data.put("total", tasks.size());
        data.put("completed", (int) tasks.stream().filter(t -> "completed".equals(t.getStatus())).count());
        data.put("in_progress", (int) tasks.stream().filter(t -> "in_progress".equals(t.getStatus())).count());
        data.put("pending", (int) tasks.stream().filter(t -> "pending".equals(t.getStatus())).count());
        return data;
    }

    // ==================== 阶段管理 ====================

    @Transactional
    public StageVO addStage(Long projectId, AddStageRequest request) {
        Project project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new BusinessException(ResultCode.PROJECT_NOT_FOUND);
        }

        Stage stage = new Stage();
        stage.setProjectId(projectId);
        stage.setName(request.getName());
        if (request.getOrderNum() != null) {
            stage.setOrderNum(request.getOrderNum());
        } else {
            Long maxOrder = stageMapper.selectCount(
                    new LambdaQueryWrapper<Stage>().eq(Stage::getProjectId, projectId));
            stage.setOrderNum(Math.toIntExact(maxOrder) + 1);
        }
        stage.setStatus("pending");
        stage.setStartDate(request.getStartDate());
        stage.setEndDate(request.getEndDate());
        stage.setCreatedAt(LocalDateTime.now());
        stage.setUpdatedAt(LocalDateTime.now());
        stageMapper.insert(stage);

        return toStageVO(stage);
    }

    public List<StageVO> getStageList(Long projectId) {
        List<Stage> stages = stageMapper.selectList(
                new LambdaQueryWrapper<Stage>().eq(Stage::getProjectId, projectId).orderByAsc(Stage::getOrderNum));
        return stages.stream().map(this::toStageVO).collect(Collectors.toList());
    }

    @Transactional
    public void updateStageOrder(Long projectId, StageOrderRequest request) {
        List<Long> stageIds = request.getStageIds();
        for (int i = 0; i < stageIds.size(); i++) {
            Stage stage = new Stage();
            stage.setId(stageIds.get(i));
            stage.setOrderNum(i + 1);
            stage.setUpdatedAt(LocalDateTime.now());
            stageMapper.updateById(stage);
        }
    }

    public StageVO getStageDetail(Long projectId, Long stageId) {
        Stage stage = stageMapper.selectById(stageId);
        if (stage == null || !stage.getProjectId().equals(projectId)) {
            throw new BusinessException(ResultCode.PROJECT_NOT_FOUND, "阶段不存在");
        }
        return toStageVO(stage);
    }

    @Transactional
    public void updateStage(Long projectId, Long stageId, UpdateStageRequest request) {
        Stage stage = stageMapper.selectById(stageId);
        if (stage == null || !stage.getProjectId().equals(projectId)) {
            throw new BusinessException(ResultCode.PROJECT_NOT_FOUND, "阶段不存在");
        }
        if (request.getName() != null) stage.setName(request.getName());
        if (request.getOrderNum() != null) stage.setOrderNum(request.getOrderNum());
        if (request.getStartDate() != null) stage.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) stage.setEndDate(request.getEndDate());
        stage.setUpdatedAt(LocalDateTime.now());
        stageMapper.updateById(stage);
    }

    @Transactional
    public void deleteStage(Long projectId, Long stageId) {
        Stage stage = stageMapper.selectById(stageId);
        if (stage == null || !stage.getProjectId().equals(projectId)) {
            throw new BusinessException(ResultCode.PROJECT_NOT_FOUND, "阶段不存在");
        }
        stageMapper.deleteById(stageId);
    }

    /**
     * 阶段推进：完成当前阶段 → 激活下一个阶段 → 维护 project.current_stage_id
     * 返回 allCompleted（是否全部阶段完成，供前端提示可结项）
     */
    @Transactional
    public Map<String, Object> advanceStage(Long projectId, Long stageId, Long userId) {
        Project project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new BusinessException(ResultCode.PROJECT_NOT_FOUND);
        }
        // 成员校验：必须是该项目所属小组的 active 成员
        Long cnt = groupMemberMapper.selectCount(new LambdaQueryWrapper<GroupMember>()
                .eq(GroupMember::getUserId, userId)
                .eq(GroupMember::getGroupId, project.getGroupId())
                .eq(GroupMember::getStatus, "active"));
        if (cnt == null || cnt == 0) {
            throw new BusinessException(ResultCode.NOT_GROUP_MEMBER);
        }
        Stage stage = stageMapper.selectById(stageId);
        if (stage == null || !stage.getProjectId().equals(projectId)) {
            throw new BusinessException(ResultCode.PROJECT_NOT_FOUND, "阶段不存在");
        }
        if ("completed".equals(stage.getStatus())) {
            throw new BusinessException(ResultCode.CONFLICT, "该阶段已完成，无需重复推进");
        }
        stage.setStatus("completed");
        stage.setUpdatedAt(LocalDateTime.now());
        stageMapper.updateById(stage);

        // 按 orderNum 找下一个未完成阶段 → 置 active；没有则视为全部完成
        List<Stage> next = stageMapper.selectList(new LambdaQueryWrapper<Stage>()
                .eq(Stage::getProjectId, projectId)
                .gt(Stage::getOrderNum, stage.getOrderNum())
                .orderByAsc(Stage::getOrderNum));
        boolean allCompleted = next.isEmpty();
        Long currentStageId = null;
        if (!next.isEmpty()) {
            Stage n = next.get(0);
            n.setStatus("active");
            n.setUpdatedAt(LocalDateTime.now());
            stageMapper.updateById(n);
            currentStageId = n.getId();
            project.setCurrentStageId(n.getId());
            project.setUpdatedAt(LocalDateTime.now());
            projectMapper.updateById(project);
        } else {
            // 全部完成：current_stage_id 置空（updateById 会忽略 null，需显式 wrapper set null）
            projectMapper.update(null, new LambdaUpdateWrapper<Project>()
                    .eq(Project::getId, projectId)
                    .set(Project::getCurrentStageId, null)
                    .set(Project::getUpdatedAt, LocalDateTime.now()));
        }

        Map<String, Object> data = new HashMap<>();
        data.put("allCompleted", allCompleted);
        data.put("currentStageId", currentStageId);
        return data;
    }

    // ==================== 模板管理 ====================

    public List<ProjectTemplateVO> getTemplateList() {
        List<StageTemplate> all = stageTemplateMapper.selectList(
                new LambdaQueryWrapper<StageTemplate>().orderByAsc(StageTemplate::getTemplateName).orderByAsc(StageTemplate::getOrderNum));
        // 按 templateName 分组
        Map<String, List<StageTemplate>> grouped = all.stream()
                .collect(Collectors.groupingBy(StageTemplate::getTemplateName));
        List<ProjectTemplateVO> result = new ArrayList<>();
        for (Map.Entry<String, List<StageTemplate>> entry : grouped.entrySet()) {
            List<StageTemplate> rows = entry.getValue();
            ProjectTemplateVO vo = new ProjectTemplateVO();
            vo.setTemplateId(rows.get(0).getId());
            vo.setName(entry.getKey());
            vo.setStageCount(rows.size());
            vo.setIsPublic(rows.get(0).getIsPublic() != null && rows.get(0).getIsPublic() == 1);
            result.add(vo);
        }
        return result;
    }

    public ProjectTemplateVO getTemplateDetail(Long templateId) {
        StageTemplate row = stageTemplateMapper.selectById(templateId);
        if (row == null) {
            throw new BusinessException(ResultCode.PROJECT_NOT_FOUND, "模板不存在");
        }
        List<StageTemplate> all = stageTemplateMapper.selectList(
                new LambdaQueryWrapper<StageTemplate>().eq(StageTemplate::getTemplateName, row.getTemplateName()));
        ProjectTemplateVO vo = new ProjectTemplateVO();
        vo.setTemplateId(templateId);
        vo.setName(row.getTemplateName());
        vo.setStageCount(all.size());
        vo.setIsPublic(row.getIsPublic() != null && row.getIsPublic() == 1);
        return vo;
    }

    @Transactional
    public ProjectTemplateVO createTemplate(CreateTemplateRequest request) {
        int isPublic = (request.getIsPublic() != null && request.getIsPublic()) ? 1 : 0;
        Long firstId = null;
        if (request.getStages() != null) {
            int order = 1;
            for (CreateTemplateRequest.StageItem item : request.getStages()) {
                StageTemplate tpl = new StageTemplate();
                tpl.setTemplateName(request.getName());
                tpl.setStageName(item.getName());
                tpl.setOrderNum(item.getOrderNum() != null ? item.getOrderNum() : order);
                tpl.setIsPublic(isPublic);
                stageTemplateMapper.insert(tpl);
                if (firstId == null) firstId = tpl.getId();
                order++;
            }
        }

        ProjectTemplateVO vo = new ProjectTemplateVO();
        vo.setTemplateId(firstId != null ? firstId : 0L);
        vo.setName(request.getName());
        vo.setStageCount(request.getStages() != null ? request.getStages().size() : 0);
        vo.setIsPublic(isPublic == 1);
        return vo;
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 根据 templateId 查找模板名称，批量插入阶段
     */
    private void applyTemplateStages(Long projectId, Long templateId) {
        StageTemplate seed = stageTemplateMapper.selectById(templateId);
        if (seed == null) return;
        List<StageTemplate> tplStages = stageTemplateMapper.selectList(
                new LambdaQueryWrapper<StageTemplate>()
                        .eq(StageTemplate::getTemplateName, seed.getTemplateName())
                        .orderByAsc(StageTemplate::getOrderNum));
        for (StageTemplate tpl : tplStages) {
            Stage stage = new Stage();
            stage.setProjectId(projectId);
            stage.setName(tpl.getStageName());
            stage.setOrderNum(tpl.getOrderNum());
            stage.setStatus("pending");
            stage.setCreatedAt(LocalDateTime.now());
            stage.setUpdatedAt(LocalDateTime.now());
            stageMapper.insert(stage);
        }
    }

    /**
     * 组装项目详情 VO（含 stages、统计计数）
     */
    private ProjectDetailVO buildProjectDetail(Project p) {
        ProjectDetailVO vo = new ProjectDetailVO();
        vo.setProjectId(p.getId());
        vo.setGroupId(p.getGroupId());
        vo.setName(p.getName());
        vo.setDescription(p.getDescription());
        vo.setProgress(p.getProgress());
        vo.setStatus(p.getStatus());
        vo.setCurrentStageId(p.getCurrentStageId());
        vo.setCreatedAt(p.getCreatedAt());

        // 审核/提醒规则：反序列化为 Map 透出（null 容错）
        try {
            if (p.getAuditRule() != null) {
                vo.setAuditRule(objectMapper.readValue(p.getAuditRule(),
                        new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {}));
            }
            if (p.getReminderRule() != null) {
                vo.setReminderRule(objectMapper.readValue(p.getReminderRule(),
                        new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {}));
            }
        } catch (Exception ignored) {
            // 规则 JSON 异常不阻断详情返回
        }

        // stages
        List<Stage> stages = stageMapper.selectList(
                new LambdaQueryWrapper<Stage>().eq(Stage::getProjectId, p.getId()).orderByAsc(Stage::getOrderNum));
        vo.setStages(stages.stream().map(this::toStageVO).collect(Collectors.toList()));

        // current stage name
        if (p.getCurrentStageId() != null) {
            Stage current = stageMapper.selectById(p.getCurrentStageId());
            if (current != null) vo.setCurrentStage(current.getName());
        }

        // counts
        vo.setMemberCount(Math.toIntExact(groupMemberMapper.selectCount(
                new LambdaQueryWrapper<GroupMember>()
                        .eq(GroupMember::getGroupId, p.getGroupId())
                        .eq(GroupMember::getStatus, "active"))));
        vo.setFileCount(Math.toIntExact(fileMapper.selectCount(
                new LambdaQueryWrapper<File>().eq(File::getProjectId, p.getId()))));
        vo.setTaskCount(Math.toIntExact(taskMapper.selectCount(
                new LambdaQueryWrapper<Task>().eq(Task::getProjectId, p.getId()))));
        vo.setCompletedTaskCount(Math.toIntExact(taskMapper.selectCount(
                new LambdaQueryWrapper<Task>().eq(Task::getProjectId, p.getId()).eq(Task::getStatus, "completed"))));

        return vo;
    }

    private ProjectVO toProjectVO(Project p) {
        ProjectVO vo = new ProjectVO();
        vo.setProjectId(p.getId());
        vo.setGroupId(p.getGroupId());
        vo.setName(p.getName());
        vo.setDescription(p.getDescription());
        vo.setStatus(p.getStatus());
        vo.setProgress(p.getProgress());
        vo.setCreatedAt(p.getCreatedAt());
        return vo;
    }

    private StageVO toStageVO(Stage s) {
        StageVO vo = new StageVO();
        vo.setStageId(s.getId());
        vo.setName(s.getName());
        vo.setOrderNum(s.getOrderNum());
        vo.setStatus(s.getStatus());
        vo.setStartDate(s.getStartDate());
        vo.setEndDate(s.getEndDate());
        vo.setFileCount(Math.toIntExact(fileMapper.selectCount(
                new LambdaQueryWrapper<File>().eq(File::getStageId, s.getId()))));
        vo.setTaskCount(Math.toIntExact(taskMapper.selectCount(
                new LambdaQueryWrapper<Task>().eq(Task::getStageId, s.getId()))));
        vo.setCompletedTaskCount(Math.toIntExact(taskMapper.selectCount(
                new LambdaQueryWrapper<Task>().eq(Task::getStageId, s.getId()).eq(Task::getStatus, "completed"))));
        return vo;
    }
}
