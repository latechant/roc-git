package com.rocgit.knife4j.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 *  - 注册 JWT 拦截器
 *  - 配置 CORS 跨域
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${rocgit.file.storage-dir:/opt/roc-git/files}")
    private String fileStorageDir;

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                // 无需鉴权的接口
                .excludePathPatterns(
                        "/api/v1/health",
                        "/api/v1/config",
                        "/api/v1/user/login",
                        "/api/v1/share/validate",            // 分享链接访客校验
                        "/api/v1/share/p/**",                // 访客访问（/p/{shareToken} 前缀，避免与管理端 list/create 等冲突）
                        "/api/v1/group/validate-invite-code"  // 加入前校验邀请码
                );
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将 /files/** 映射到磁盘存储目录，使上传的文件可通过 URL 直接访问
        String location = fileStorageDir.endsWith("/") ? fileStorageDir : fileStorageDir + "/";
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + location);
    }
}
