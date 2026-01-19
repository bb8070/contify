package com.example.contify.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info=@Info(
                title = "Contify API",
                version = "v1",
                description = "Contify 서비스 API 문서"
        )
)
@Configuration
public class OpenApiConfig {
}
