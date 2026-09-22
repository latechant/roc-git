package com.rocgit.knife4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * ROC-git Knife4j 文档启动类
 *
 * Knife4j 是基于 springdoc-openapi 的增强 UI，访问 /doc.html 查看 API 文档
 *
 * <h2>两种启动方式：</h2>
 * <h3>方式 1：本地开发（IDEA 直接运行）</h3>
 * <pre>
 *   直接运行 main() 方法，启动内嵌 Tomcat（默认 8080）
 *   访问地址：
 *     - Knife4j 文档: http://localhost:8080/doc.html
 *     - OpenAPI JSON: http://localhost:8080/v3/api-docs
 *     - Swagger UI: http://localhost:8080/swagger-ui/index.html
 * </pre>
 *
 * <h3>方式 2：部署到外部 Tomcat（WAR 包方式）</h3>
 * <pre>
 *   1. mvn clean package -DskipTests 生成 WAR
 *   2. 把 target/knife4j-spring-1.0.0.war 拷贝到 $TOMCAT_HOME/webapps/
 *   3. 启动 Tomcat，自动解压部署
 *   4. 访问地址：
 *     - Knife4j 文档: http://localhost:8080/knife4j-spring-1.0.0/doc.html
 *     - 或修改 server.xml 配置 context root 为 / 后：http://localhost:8080/doc.html
 * </pre>
 */
@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    /**
     * 部署到外部 Tomcat 时需要重写此方法
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Application.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println("\n========================================");
        System.out.println("ROC-git Knife4j API 文档已启动");
        System.out.println("本地访问：http://localhost:8080/doc.html");
        System.out.println("========================================\n");
    }
}
