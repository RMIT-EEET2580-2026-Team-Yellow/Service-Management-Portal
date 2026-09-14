package com.fixinh.smt.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

/** Serves files uploaded via FileStorageController back out as plain static resources. */
@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    private final String uploadDirPath;

    public StaticResourceConfig(@Value("${smt.storage.upload-dir:uploads}") String uploadDirPath) {
        this.uploadDirPath = uploadDirPath;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = Path.of(uploadDirPath).toAbsolutePath().normalize().toUri().toString();
        registry.addResourceHandler("/api/v1/files/**").addResourceLocations(location);
    }
}
