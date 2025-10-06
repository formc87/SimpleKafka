package com.example.producer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 프론트엔드 애플리케이션에서 프로듀서 API를 호출할 수 있도록 CORS를 허용한다.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final String[] ALLOWED_ORIGINS = {
            "http://localhost:5173",
            "http://localhost:5174",
            "http://localhost:3000"
    };

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/publish/**")
                .allowedOrigins(ALLOWED_ORIGINS)
                .allowedMethods("POST")
                .allowCredentials(true);

        registry.addMapping("/logout")
                .allowedOrigins(ALLOWED_ORIGINS)
                .allowedMethods("GET")
                .allowCredentials(true);
    }
}
