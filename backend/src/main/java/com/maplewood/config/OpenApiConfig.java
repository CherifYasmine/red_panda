package com.maplewood.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Maplewood Course Planning API")
                .version("1.0.0")
                .description("REST API for course scheduling, enrollment, and academic metrics")
                .contact(new Contact()
                    .name("Maplewood High School"))
            );
    }
}
