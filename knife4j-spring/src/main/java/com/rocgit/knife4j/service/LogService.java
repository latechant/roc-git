package com.rocgit.knife4j.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rocgit.knife4j.common.BusinessException;
import com.rocgit.knife4j.common.ResultCode;
import com.rocgit.knife4j.entity.log.LogStatsVO;
import com.rocgit.knife4j.entity.log.OperationLogVO;
import com.rocgit.knife4j.mapper.OperationLogMapper;
import com.rocgit.knife4j.mapper.UserMapper;
import com.rocgit.knife4j.model.OperationLog;
import com.rocgit.knife4j.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 操作日志服务
 */
@Service
public class LogService {

    @Autowired
    private OperationLogMapper operationLogMapper;
    @Autowired
    private UserMapper userMapper;

    private OperationLogVO toVO(OperationLog log) {
        OperationLogVO vo = new OperationLogVO();
        vo.setLogId(log.getId());
        vo.setAction(log.getAction());
        vo.setUserId(log.getUserId());
        if (log.getUserId() != null) {
            User user = userMapper.selectById(log.getUserId());
            vo.setUserName(user == null ? null : user.getNickname());
        }
        vo.setTargetType(log.getTargetType());
        vo.setTargetId(log.getTargetId());
        vo.setTargetName(log.getTargetName());
        vo.setDetail(log.getDetail());
        vo.setCreatedAt(log.getCreatedAt());
        vo.setIp(log.getIp());
        return vo;
    }

    /** 操作日志列表，type 过滤 target_type，分页 */
    public List<OperationLogVO> list(Long projectId, String type, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<OperationLog>()
                .eq(projectId != null, OperationLog::getProjectId, projectId)
                .eq(type != null && !type.isEmpty(), OperationLog::getTargetType, type)
                .orderByDesc(OperationLog::getCreatedAt);
        int offset = (pageNum - 1) * pageSize;
        if (offset < 0) offset = 0;
        wrapper.last("LIMIT " + offset + "," + pageSize);
        List<OperationLog> records = operationLogMapper.selectList(wrapper);
        return records.stream().map(this::toVO).collect(Collectors.toList());
    }

    /** 日志详情 */
    public OperationLogVO detail(Long logId) {
        OperationLog log = operationLogMapper.selectById(logId);
        if (log == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "日志不存在");
        }
        return toVO(log);
    }

    /** 项目日志统计 */
    public LogStatsVO logStats(Long projectId) {
        long totalLogs = operationLogMapper.selectCount(
                new LambdaQueryWrapper<OperationLog>().eq(OperationLog::getProjectId, projectId));

        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        long todayLogs = operationLogMapper.selectCount(
                new LambdaQueryWrapper<OperationLog>()
                        .eq(OperationLog::getProjectId, projectId)
                        .ge(OperationLog::getCreatedAt, todayStart));

        long activeUsers = operationLogMapper.selectObjs(
                new QueryWrapper<OperationLog>().select("DISTINCT user_id")
                        .eq("project_id", projectId)).stream().count();

        long fileOperations = operationLogMapper.selectCount(
                new LambdaQueryWrapper<OperationLog>()
                        .eq(OperationLog::getProjectId, projectId)
                        .eq(OperationLog::getTargetType, "file"));

        long taskOperations = operationLogMapper.selectCount(
                new LambdaQueryWrapper<OperationLog>()
                        .eq(OperationLog::getProjectId, projectId)
                        .eq(OperationLog::getTargetType, "task"));

        LogStatsVO vo = new LogStatsVO();
        vo.setTotalLogs(totalLogs);
        vo.setTodayLogs((int) todayLogs);
        vo.setActiveUsers((int) activeUsers);
        vo.setFileOperations((int) fileOperations);
        vo.setTaskOperations((int) taskOperations);
        return vo;
    }
}
