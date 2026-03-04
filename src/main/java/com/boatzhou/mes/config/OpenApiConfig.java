package com.boatzhou.mes.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Swagger 文档元信息配置。
 */
@Configuration
public class OpenApiConfig {

    /**
     * 构建 Swagger UI 展示用的 OpenAPI 对象。
     */
    @Bean
    public OpenAPI mesOpenAPI() {
        final String securitySchemeName = "BearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("MES Platform API")
                        .version("v1.0.0")
                        .description("MES 接口文档：认证、设备、工单、报表、AI 诊断")
                        .contact(new Contact().name("MES Team")))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name("Authorization")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
