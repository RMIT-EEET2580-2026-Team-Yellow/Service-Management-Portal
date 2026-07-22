package com.fixinh.smt.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI smtOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Fixinh - Service Management Portal API")
                .description("SMT backend: Staff, Technician, Booking, Notification, Statistics modules")
                .version("v1"));
    }
}
