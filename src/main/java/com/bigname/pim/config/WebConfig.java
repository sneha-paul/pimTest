package com.bigname.pim.config;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created by Manu on 8/3/2018.
 */
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/*.js/**").addResourceLocations("/static/");
        registry.addResourceHandler("/*.css/**").addResourceLocations("/static/");
        registry.addResourceHandler("/*.jpg/**").addResourceLocations("/static/");
        registry.addResourceHandler("/*.gif/**").addResourceLocations("/static/");
        registry.addResourceHandler("/*.png/**").addResourceLocations("/static/");
    }
}
