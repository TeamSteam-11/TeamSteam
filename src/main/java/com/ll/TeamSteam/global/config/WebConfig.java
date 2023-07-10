package com.ll.TeamSteam.global.config;

import com.ll.TeamSteam.domain.matching.entity.GenreTagTypeRequestConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new GenreTagTypeRequestConverter());
    }
}