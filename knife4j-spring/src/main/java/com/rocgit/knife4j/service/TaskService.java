package com.rocgit.knife4j.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rocgit.knife4j.common.BusinessException;
import com.rocgit.knife4j.common.ResultCode;
import com.rocgit.knife4j.dto.task.*;
import com.rocgit.knife4j.entity.task.TaskVO;
import com.rocgit.knife4j.mapper.*;
import com.rocgit.knife4j.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 任务服务 — 任务 CRUD + 状态流转
 */
@Service
public class TaskService {

    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private UserMapper userMapper;

    // ==================== 任务 CRUD ====================

    @Transactional
    public TaskVO createTask(CreateTaskRequest request) {
        Task task = new Task();
        task.setProjectId(request.getProjectId());
        task.setName(request.getName());
        task.setDescription(request.getDescription());
        task.setAssigneeId(request.getAssigneeId());
        task.setDeadline(request.getDeadline());
        task.setPriority(request.getPriority() != null ? request.getPriority() : "normal");
        task.setStatus("pending");
        task.setProgress(0);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        taskMapper.insert(task);
        return toTaskVO(task);
    }

    public Map<String, Object> listTasks(Long projectId, Long assigneeId, String status,
                                         Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<Task>()
                .eq(Task::getProjectId, projectId)
                .orderByDesc(Task::getCreatedAt);
        if (assigneeId != null) {
            wrapper.eq(Task::getAssigneeId, assigneeId);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Task::getStatus, status);
        }
        List<Task> all = taskMapper.selectList(wrapper);
        return paginate(all, pageNum, pageSize);
    }

    public Map<String, Object> getMyTasks(Long userId, String status, Boolean overdue,
                                         Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<Task>()
                .eq(Task::getAssigneeId, userId)
                .orderByDesc(Task::getCreatedAt);
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Task::getStatus, status);
        } else {
            wrapper.ne(Task::getStatus, "completed");
        }
        if (overdue != null && overdue) {
            wrapper.lt(Task::getDeadline, LocalDateTime.now());
        }
        List<Task> all = taskMapper.selectList(wrapper);
        return paginate(all, pageNum, pageSize);
    }

    public List<TaskVO> getDueSoonTasks(Integer days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadline = now.plusDays(days);
        List<Task> tasks = taskMapper.selectList(
                new LambdaQueryWrapper<Task>()
                        .gt(Task::getDeadline, now)
                        .lt(Task::getDeadline, deadline)
                        .ne(Task::getStatus, "completed")
                        .orderByAsc(Task::getDeadline));
        return tasks.stream().map(this::toTaskVO).collect(Collectors.toList());
    }

    public TaskVO getTaskDetail(Long taskId) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(ResultCode.TASK_NOT_FOUND);
        }
        return toTaskVO(task);
    }

    @Transactional
    public void updateTask(Long taskId, UpdateTaskRequest request) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(ResultCode.TASK_NOT_FOUND);
        }
        if (request.getName() != null) task.setName(request.getName());
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getAssigneeId() != null) task.setAssigneeId(request.getAssigneeId());
        if (request.getDeadline() != null) task.setDeadline(request.getDeadline());
        if (request.getPriority() != null) task.setPriority(request.getPriority());
        task.setUpdatedAt(LocalDateTime.now());
        taskMapper.updateById(task);
    }

    @Transactional
    public void deleteTask(Long taskId) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(ResultCode.TASK_NOT_FOUND);
        }
        taskMapper.deleteById(taskId);
    }

    // ==================== 状态流转 ====================

    @Transactional
    public void completeTask(Long taskId, CompleteTaskRequest request) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(ResultCode.TASK_NOT_FOUND);
        }
        task.setStatus("completed");
        task.setProgress(100);
        task.setCompletedAt(LocalDateTime.now());
        if (request != null && request.getRemark() != null) {
            task.setCompletionRemark(request.getRemark());
        }
        task.setUpdatedAt(LocalDateTime.now());
        taskMapper.updateById(task);
    }

    @Transactional
    public void abandonTask(Long taskId, AbandonTaskRequest request) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(ResultCode.TASK_NOT_FOUND);
        }
        task.setStatus("abandoned");
        if (request != null && request.getReason() != null) {
            task.setCompletionRemark(request.getReason());
        }
        task.setUpdatedAt(LocalDateTime.now());
        taskMapper.updateById(task);
    }

    @Transactional
    public void transferTask(Long taskId, TransferTaskRequest request) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(ResultCode.TASK_NOT_FOUND);
        }
        task.setAssigneeId(request.getNewAssigneeId());
        task.setUpdatedAt(LocalDateTime.now());
        taskMapper.updateById(task);
    }

    @Transactional
    public void updateTaskProgress(Long taskId, TaskProgressRequest request) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(ResultCode.TASK_NOT_FOUND);
        }
        task.setProgress(request.getProgress());
        // 进度 100 自动标记完成
        if (request.getProgress() >= 100) {
            task.setStatus("completed");
            task.setCompletedAt(LocalDateTime.now());
        } else if (request.getProgress() > 0 && "pending".equals(task.getStatus())) {
            task.setStatus("in_progress");
        }
        task.setUpdatedAt(LocalDateTime.now());
        taskMapper.updateById(task);
    }

    // ==================== 私有辅助方法 ====================

    private Map<String, Object> paginate(List<Task> all, Integer pageNum, Integer pageSize) {
        int total = all.size();
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        List<Task> pageList = fromIndex >= total ? new ArrayList<>() : all.subList(fromIndex, toIndex);

        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("tasks", pageList.stream().map(this::toTaskVO).collect(Collectors.toList()));
        return data;
    }

    private TaskVO toTaskVO(Task t) {
        TaskVO vo = new TaskVO();
        vo.setTaskId(t.getId());
        vo.setProjectId(t.getProjectId());
        vo.setName(t.getName());
        vo.setDescription(t.getDescription());
        vo.setAssigneeId(t.getAssigneeId());
        vo.setDeadline(t.getDeadline());
        vo.setPriority(t.getPriority());
        vo.setStatus(t.getStatus());
        vo.setProgress(t.getProgress());
        vo.setCompletedAt(t.getCompletedAt());
        vo.setCompletionRemark(t.getCompletionRemark());
        vo.setCreatedAt(t.getCreatedAt());

        // project name
        Project p = projectMapper.selectById(t.getProjectId());
        if (p != null) vo.setProjectName(p.getName());

        // assignee name
        if (t.getAssigneeId() != null) {
            User u = userMapper.selectById(t.getAssigneeId());
            if (u != null) vo.setAssigneeName(u.getNickname());
        }

        return vo;
    }
}
