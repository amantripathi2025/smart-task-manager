package com.aman.smart_task_manager.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private Cors cors = new Cors();

    @Data
    public static class Cors {
        private String allowedOrigins = "http://localhost:5173";
    }
}
