package com.rocgit.knife4j.common;

import lombok.Getter;

/**
 * 业务错误码枚举
 *
 * 对应 openapi.yaml 中的错误码说明：
 *   0    成功
 *   400  请求参数错误
 *   401  未认证
 *   403  无权限
 *   404  资源不存在
 *   409  资源冲突
 *   429  请求频繁
 *   500  服务器错误
 */
@Getter
public enum ResultCode {

    SUCCESS(0, "success"),
    FAIL(500, "服务器内部错误"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未认证"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    CONFLICT(409, "资源冲突"),
    TOO_MANY_REQUESTS(429, "请求过于频繁"),

    // 业务级错误码（10xxx）
    USER_NOT_FOUND(10001, "用户不存在"),
    INVALID_TOKEN(10002, "无效的 Token"),
    TOKEN_EXPIRED(10003, "Token 已过期"),
    GROUP_NOT_FOUND(10101, "小组不存在"),
    NOT_GROUP_MEMBER(10102, "您不是该小组成员"),
    INVITE_CODE_INVALID(10103, "邀请码无效"),
    PROJECT_NOT_FOUND(10201, "项目不存在"),
    FILE_NOT_FOUND(10301, "文件不存在"),
    FILE_UPLOAD_FAIL(10302, "文件上传失败"),
    TASK_NOT_FOUND(10401, "任务不存在"),
    SHARE_LINK_EXPIRED(10501, "分享链接已过期");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
