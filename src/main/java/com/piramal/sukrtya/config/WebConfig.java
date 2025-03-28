package com.piramal.sukrtya.config;


import org.springframework.beans.factory.annotation.Value;
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
                        .allowedOrigins(
                                "https://sukrtya.api.nitag.in",
                                "https://sukrtya.ui.nitag.in",
                                "https://ui.sukrtya.in","https://api.sukrtya.in","https://dev-api.sukrtya.in"
                                ,"http://localhost:3000","https://localhost:3000","https://dev-ui.sukrtya.in") // Replace with your frontend URL
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allowed HTTP methods
                        .allowedHeaders("*") // Allow specific headers
                        .allowCredentials(true); // Allow credentials (e.g., cookies)
            }
        };
    }
}

