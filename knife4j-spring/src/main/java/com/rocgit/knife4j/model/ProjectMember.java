package com.rocgit.knife4j.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 项目成员表
 */
@Data
@TableName("project_member")
public class ProjectMember {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long projectId;

    private Long userId;

    private String role;

    private LocalDateTime joinedAt;
}
