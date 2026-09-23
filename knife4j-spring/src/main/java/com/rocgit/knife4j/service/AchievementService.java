package com.rocgit.knife4j.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rocgit.knife4j.common.BusinessException;
import com.rocgit.knife4j.common.ResultCode;
import com.rocgit.knife4j.dto.achievement.CreateAchievementRequest;
import com.rocgit.knife4j.dto.achievement.UpdateAchievementRequest;
import com.rocgit.knife4j.entity.achievement.AchievementVO;
import com.rocgit.knife4j.mapper.AchievementFileMapper;
import com.rocgit.knife4j.mapper.AchievementMapper;
import com.rocgit.knife4j.mapper.FileMapper;
import com.rocgit.knife4j.mapper.StageMapper;
import com.rocgit.knife4j.mapper.UserMapper;
import com.rocgit.knife4j.model.Achievement;
import com.rocgit.knife4j.model.AchievementFile;
import com.rocgit.knife4j.model.File;
import com.rocgit.knife4j.model.Stage;
import com.rocgit.knife4j.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 项目成果服务 - 接入真实 MySQL
 */
@Service
public class AchievementService {

    @Autowired
    private AchievementMapper achievementMapper;
    @Autowired
    private AchievementFileMapper achievementFileMapper;
    @Autowired
    private StageMapper stageMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private FileMapper fileMapper;

    /**
     * 创建成果登记，若携带 fileIds 则批量写入 achievement_file
     */
    @Transactional
    public AchievementVO create(CreateAchievementRequest request, Long userId) {
        Achievement achievement = new Achievement();
        achievement.setProjectId(request.getProjectId());
        achievement.setStageId(request.getStageId());
        achievement.setTitle(request.getTitle());
        achievement.setDescription(request.getDescription());
        achievement.setContributorId(userId);
        achievement.setCreatedAt(LocalDateTime.now());
        achievementMapper.insert(achievement);

        List<Long> fileIds = request.getRelatedFileIds();
        if (fileIds != null && !fileIds.isEmpty()) {
            for (Long fileId : fileIds) {
                AchievementFile af = new AchievementFile();
                af.setAchievementId(achievement.getId());
                af.setFileId(fileId);
                achievementFileMapper.insert(af);
            }
        }
        return toVO(achievement);
    }

    /**
     * 按项目查询成果列表
     */
    public List<AchievementVO> listByProject(Long projectId) {
        LambdaQueryWrapper<Achievement> wrapper = new LambdaQueryWrapper<Achievement>()
                .eq(Achievement::getProjectId, projectId)
                .orderByDesc(Achievement::getCreatedAt);
        return achievementMapper.selectList(wrapper).stream()
                .map(this::toVO).collect(Collectors.toList());
    }

    /**
     * 查询成果详情（含阶段名、贡献者昵称、关联文件）
     */
    public AchievementVO getById(Long achievementId) {
        Achievement achievement = achievementMapper.selectById(achievementId);
        if (achievement == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "成果不存在");
        }
        return toVO(achievement);
    }

    /**
     * 更新成果信息
     */
    @Transactional
    public void update(Long achievementId, UpdateAchievementRequest request) {
        Achievement exist = achievementMapper.selectById(achievementId);
        if (exist == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "成果不存在");
        }
        Achievement update = new Achievement();
        update.setId(achievementId);
        if (request.getTitle() != null) update.setTitle(request.getTitle());
        if (request.getDescription() != null) update.setDescription(request.getDescription());
        achievementMapper.updateById(update);
    }

    /**
     * 删除成果，并级联删除关联文件记录
     */
    @Transactional
    public void delete(Long achievementId) {
        achievementMapper.deleteById(achievementId);
        achievementFileMapper.delete(new LambdaQueryWrapper<AchievementFile>()
                .eq(AchievementFile::getAchievementId, achievementId));
    }

    /**
     * 实体转 VO，补充阶段名、贡献者昵称与关联文件
     */
    private AchievementVO toVO(Achievement achievement) {
        AchievementVO vo = new AchievementVO();
        vo.setAchievementId(achievement.getId());
        vo.setProjectId(achievement.getProjectId());
        vo.setStageId(achievement.getStageId());
        vo.setTitle(achievement.getTitle());
        vo.setDescription(achievement.getDescription());
        vo.setContributorId(achievement.getContributorId());
        vo.setCreatedAt(achievement.getCreatedAt());

        // 阶段名称
        if (achievement.getStageId() != null) {
            Stage stage = stageMapper.selectById(achievement.getStageId());
            if (stage != null) {
                vo.setStageName(stage.getName());
            }
        }
        // 贡献者昵称
        if (achievement.getContributorId() != null) {
            User user = userMapper.selectById(achievement.getContributorId());
            if (user != null) {
                vo.setContributorName(user.getNickname());
            }
        }
        // 关联文件
        List<AchievementFile> links = achievementFileMapper.selectList(
                new LambdaQueryWrapper<AchievementFile>()
                        .eq(AchievementFile::getAchievementId, achievement.getId()));
        if (!links.isEmpty()) {
            List<Long> fileIds = links.stream().map(AchievementFile::getFileId).collect(Collectors.toList());
            Map<Long, File> fileMap = fileMapper.selectBatchIds(fileIds).stream()
                    .collect(Collectors.toMap(File::getId, f -> f));
            List<AchievementVO.RelatedFile> relatedFiles = new ArrayList<>();
            for (Long fileId : fileIds) {
                File file = fileMap.get(fileId);
                if (file == null) {
                    continue;
                }
                AchievementVO.RelatedFile rf = new AchievementVO.RelatedFile();
                rf.setFileId(file.getId());
                rf.setName(file.getName());
                relatedFiles.add(rf);
            }
            vo.setRelatedFiles(relatedFiles);
        }
        return vo;
    }
}
