package com.rocgit.knife4j.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 消息订阅表
 */
@Data
@TableName("user_subscription")
public class UserSubscription {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Integer auditNotify;

    private Integer taskRemind;

    private Integer memberNotify;

    private Integer stageNotify;

    private Integer mentionNotify;
}
