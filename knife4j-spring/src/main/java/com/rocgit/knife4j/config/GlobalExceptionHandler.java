package com.rocgit.knife4j.config;

import com.rocgit.knife4j.common.BusinessException;
import com.rocgit.knife4j.common.Result;
import com.rocgit.knife4j.common.ResultCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * 统一将异常转换为标准 Result 响应
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 业务异常 */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusiness(BusinessException e) {
        return ResponseEntity.ok(Result.error(e.getCode(), e.getMessage()));
    }

    /** @RequestBody 参数校验失败 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleValid(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return ResponseEntity.ok(Result.error(ResultCode.BAD_REQUEST.getCode(), msg));
    }

    /** 表单绑定异常 */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Result<Void>> handleBind(BindException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return ResponseEntity.ok(Result.error(ResultCode.BAD_REQUEST.getCode(), msg));
    }

    /** @RequestParam 校验失败 */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Result<Void>> handleConstraint(ConstraintViolationException e) {
        String msg = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        return ResponseEntity.ok(Result.error(ResultCode.BAD_REQUEST.getCode(), msg));
    }

    /** 上传大小超限（multipart 解析阶段被框架拦截） */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Result<Void>> handleMaxUploadSize(MaxUploadSizeExceededException e) {
        return ResponseEntity.ok(Result.error(ResultCode.FAIL.getCode(), "文件超过 50MB 大小限制"));
    }

    /** 兜底异常 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleAll(Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.error(ResultCode.FAIL.getCode(), "服务器内部错误: " + e.getMessage()));
    }
}
