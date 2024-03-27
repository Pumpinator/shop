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
        String userDirectory = "user-photos";
        Path userPhotoDirectory = Paths.get(userDirectory);
        String userPhotoPath = userPhotoDirectory.toFile().getAbsolutePath();
        resourceHandlerRegistry
                .addResourceHandler("/" + userDirectory + "/**")
                .addResourceLocations("file:/" + userPhotoPath + "/");

        String categoryDirectory = "../category-images";
        Path categoryPhotoDirectory = Paths.get(categoryDirectory);
        String categoryPhotoPath = categoryPhotoDirectory.toFile().getAbsolutePath();
        resourceHandlerRegistry
                .addResourceHandler("/category-images/**")
                .addResourceLocations("file:/" + categoryPhotoPath + "/");
    }
}
