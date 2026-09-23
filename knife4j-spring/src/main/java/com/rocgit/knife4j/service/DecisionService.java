package com.rocgit.knife4j.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rocgit.knife4j.common.BusinessException;
import com.rocgit.knife4j.common.ResultCode;
import com.rocgit.knife4j.dto.decision.CreateDecisionRequest;
import com.rocgit.knife4j.dto.decision.UpdateDecisionRequest;
import com.rocgit.knife4j.entity.decision.DecisionVO;
import com.rocgit.knife4j.mapper.DecisionMapper;
import com.rocgit.knife4j.mapper.DiscussionMapper;
import com.rocgit.knife4j.mapper.UserMapper;
import com.rocgit.knife4j.model.Decision;
import com.rocgit.knife4j.model.Discussion;
import com.rocgit.knife4j.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 决策记录服务 - 接入真实 MySQL
 */
@Service
public class DecisionService {

    @Autowired
    private DecisionMapper decisionMapper;
    @Autowired
    private DiscussionMapper discussionMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 创建决策记录
     */
    @Transactional
    public DecisionVO create(CreateDecisionRequest request, Long userId) {
        Decision decision = new Decision();
        decision.setProjectId(request.getProjectId());
        decision.setTitle(request.getTitle());
        decision.setContent(request.getContent());
        decision.setDiscussionId(request.getDiscussionId());
        decision.setCreatorId(userId);
        decision.setCreatedAt(LocalDateTime.now());
        decisionMapper.insert(decision);
        return toVO(decision);
    }

    /**
     * 按项目分页查询决策记录
     */
    public List<DecisionVO> listByProject(Long projectId, Integer pageNum, Integer pageSize) {
        Page<Decision> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Decision> wrapper = new LambdaQueryWrapper<Decision>()
                .eq(Decision::getProjectId, projectId)
                .orderByDesc(Decision::getCreatedAt);
        List<Decision> records = decisionMapper.selectPage(page, wrapper).getRecords();
        return records.stream().map(this::toVO).collect(Collectors.toList());
    }

    /**
     * 查询决策详情（含关联研讨主题、决策人昵称）
     */
    public DecisionVO getById(Long decisionId) {
        Decision decision = decisionMapper.selectById(decisionId);
        if (decision == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "决策记录不存在");
        }
        return toVO(decision);
    }

    /**
     * 更新决策记录
     */
    @Transactional
    public void update(Long decisionId, UpdateDecisionRequest request) {
        Decision exist = decisionMapper.selectById(decisionId);
        if (exist == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "决策记录不存在");
        }
        Decision update = new Decision();
        update.setId(decisionId);
        if (request.getTitle() != null) update.setTitle(request.getTitle());
        if (request.getContent() != null) update.setContent(request.getContent());
        decisionMapper.updateById(update);
    }

    /**
     * 删除决策记录
     */
    @Transactional
    public void delete(Long decisionId) {
        decisionMapper.deleteById(decisionId);
    }

    /**
     * 实体转 VO，补充关联研讨主题与决策人昵称
     */
    private DecisionVO toVO(Decision decision) {
        DecisionVO vo = new DecisionVO();
        vo.setDecisionId(decision.getId());
        vo.setProjectId(decision.getProjectId());
        vo.setTitle(decision.getTitle());
        vo.setContent(decision.getContent());
        vo.setDiscussionId(decision.getDiscussionId());
        vo.setCreatorId(decision.getCreatorId());
        vo.setCreatedAt(decision.getCreatedAt());

        // 决策人昵称
        if (decision.getCreatorId() != null) {
            User user = userMapper.selectById(decision.getCreatorId());
            if (user != null) {
                vo.setCreatorName(user.getNickname());
            }
        }
        // 关联研讨主题（discussion_id 非空时）
        if (decision.getDiscussionId() != null) {
            Discussion discussion = discussionMapper.selectById(decision.getDiscussionId());
            if (discussion != null) {
                vo.setRelatedDiscussionTopic(discussion.getTopic());
            }
        }
        return vo;
    }
}
