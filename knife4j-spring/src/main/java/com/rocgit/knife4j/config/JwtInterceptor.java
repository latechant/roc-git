package com.rocgit.knife4j.config;

import com.rocgit.knife4j.common.Result;
import com.rocgit.knife4j.common.ResultCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

/**
 * JWT 鉴权拦截器
 *
 * 从请求头读取 Authorization: Bearer {token}，校验通过后将 userId 存入 request
 * 无需鉴权的接口应在 Knife4jConfig 中标注 security: []
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // OPTIONS 跨域预检请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return writeError(response, ResultCode.UNAUTHORIZED, "缺少 Authorization 头");
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return writeError(response, ResultCode.TOKEN_EXPIRED, "Token 无效或已过期");
        }

        String userId = jwtUtil.getUserIdFromToken(token);
        request.setAttribute("userId", userId);
        return true;
    }

    private boolean writeError(HttpServletResponse response, ResultCode code, String msg) throws Exception {
        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");
        Result<Void> result = Result.error(code.getCode(), msg);
        response.getWriter().write(new ObjectMapper().writeValueAsString(result));
        return false;
    }
}
