package com.shop.admin.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfiguration implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry resourceHandlerRegistry) {
        String directory = "user-photos";
        Path photoDirectory = Paths.get(directory);
        String photoPath = photoDirectory.toFile().getAbsolutePath();
        resourceHandlerRegistry
                .addResourceHandler("/" + directory + "/**")
                .addResourceLocations("file:/" + photoPath + "/");
    }
}
