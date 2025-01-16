package com.allspark.allsparkofficialwebsite.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * WebConfig
 * Description:
 *
 * @author lotus
 * @version 1.0
 * @since 2025/1/16 下午12:13
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${storage.image.path}")
    private String imageStoragePath;

    @Value("${storage.json.path}")
    private String jsonStoragePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String absolutePath = Paths.get(imageStoragePath).toAbsolutePath().toUri().toString();
        registry.addResourceHandler("/images/**")
                .addResourceLocations(absolutePath)
                .setCachePeriod(3600)
                .resourceChain(true);

    }

}
