package com.aman.smart_task_manager.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI smartTaskOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Smart Task Manager API")
                .version("v1")
                .description("Versioned API for boards, lists, tasks, comments, collaboration and dashboard insights")
                .contact(new Contact().name("Smart Task Manager"))
                .license(new License().name("MIT")));
    }
}
