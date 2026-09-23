package com.rocgit.knife4j.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 成果关联文件表
 */
@Data
@TableName("achievement_file")
public class AchievementFile {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long achievementId;

    private Long fileId;
}
