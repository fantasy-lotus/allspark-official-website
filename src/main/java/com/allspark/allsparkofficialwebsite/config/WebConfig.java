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

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/")
                .setCachePeriod(3600)
                .resourceChain(true);
        registry.addResourceHandler("/json/**")
                .addResourceLocations("classpath:/static/json/")
                .setCachePeriod(3600)
                .resourceChain(true);
    }

}
