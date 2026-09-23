package com.rocgit.knife4j.entity.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录历史记录
 */
@Data
@Schema(description = "登录历史")
public class LoginHistoryVO {

    @Schema(description = "登录时间")
    private LocalDateTime loginTime;

    @Schema(description = "登录设备", example = "iPhone 15 Pro")
    private String device;

    @Schema(description = "登录IP", example = "192.168.1.1")
    private String ip;
}
