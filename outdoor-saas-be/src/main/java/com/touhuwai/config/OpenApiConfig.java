package com.touhuwai.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI (Swagger) 配置
 * 用于生成 API 文档和 Dify 工具集成
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:16000}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        
        return new OpenAPI()
            .info(new Info()
                .title("Outdoor SaaS API")
                .version("1.0.0")
                .description("户外广告管理系统 REST API 文档\n\n" +
                    "用于 Dify AI 助手集成的工具接口。\n" +
                    "支持的实体：社区、道闸、框架、方案及其关联关系。")
                .contact(new Contact()
                    .name("技术支持")
                    .email("support@touhuwai.com"))
                .license(new License()
                    .name("MIT License")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:" + serverPort)
                    .description("本地开发环境"),
                new Server()
                    .url("/api")
                    .description("相对路径")))
            .addSecurityItem(new SecurityRequirement()
                .addList(securitySchemeName))
            .components(new io.swagger.v3.oas.models.Components()
                .addSecuritySchemes(securitySchemeName, 
                    new SecurityScheme()
                        .name(securitySchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("请输入 JWT Token，格式：Bearer {token}")));
    }
}
