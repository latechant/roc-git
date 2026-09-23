package com.rocgit.knife4j.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类（基于 jjwt）
 *
 * 用于签发和验证 JWT Token
 */
@Component
public class JwtUtil {

    /** 密钥（生产环境应从配置中心读取） */
    private static final String SECRET = "rocgit-secret-key-2026-very-long-string";

    /** Token 有效期：7 天 */
    private static final long EXPIRATION = 7 * 24 * 60 * 60 * 1000L;

    /** 密钥串长度 39 字节，HS256（>=32 字节）可用；HS512 需 64 字节会抛异常 */
    private static final Key SIGNING_KEY = Keys.hmacShaKeyFor(SECRET.getBytes(java.nio.charset.StandardCharsets.UTF_8));

    /**
     * 生成 JWT Token
     */
    public String generateToken(String userId, String nickname) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("nickname", nickname);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SIGNING_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析 Token
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(SIGNING_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 校验 Token 是否有效
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从 Token 中获取用户 ID
     */
    public String getUserIdFromToken(String token) {
        try {
            Claims claims = parseToken(token);
            return (String) claims.get("userId");
        } catch (Exception e) {
            return null;
        }
    }
}
