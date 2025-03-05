package com.piramal.sukrtya.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {
   // @Value("${app.base.url}")
   // private String baseUrl;
    @Bean
    public WebMvcConfigurer corsConfigurer() {

        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Apply to all endpoints
                        .allowedOrigins("https://ui.sukrtya.in/","https://api.sukrtya.in/") // Replace with your frontend URL
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allowed HTTP methods
                        .allowedHeaders("Authorization", "Content-Type", "X-Requested-With") // Allow specific headers
                        .allowCredentials(false); // Allow credentials (e.g., cookies)
            }
        };
    }
}

