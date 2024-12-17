package com.piramal.sukrtya.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Apply to all endpoints
                        .allowedOriginPatterns("*") // Allow all origins using patterns
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allow specific methods
                        .allowedHeaders("Authorization", "Content-Type", "X-Requested-With") // Allow all headers
                        .allowCredentials(true); // Allow credentials (cookies, authorization headers)
            }
        };
    }
}

