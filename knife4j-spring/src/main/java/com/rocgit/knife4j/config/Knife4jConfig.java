package com.rocgit.knife4j.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j OpenAPI 配置
 *
 * 此配置用于生成 Knife4j UI 所依赖的 OpenAPI 3.0 规范
 * Knife4j 4.x 会自动注入 doc.html 页面
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ROC-git 课程小组协作管理平台 API 文档")
                        .description("基于 Knife4j + Spring Boot 的 API 文档，自动通过 @Operation/@Parameter 注解生成")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("ROC-git Team")
                                .email("team@rocgit.com")
                                .url("https://rocgit.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .components(new Components()
                        .addSecuritySchemes("Bearer",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT 认证令牌，格式: Bearer {token}")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer"));
    }
}
