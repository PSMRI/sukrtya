package com.piramal.sukrtya.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.stream.Stream;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Get URLs from environment variables and split by comma
                String frontendUrls = System.getenv().getOrDefault("FRONTEND_URL", "http://localhost:3000,https://localhost:3000");
                String backendUrls = System.getenv().getOrDefault("BACKEND_URL", "");
                
                // Combine and clean URLs
                String[] allowedOriginPatterns = Stream.concat(
                                Arrays.stream(frontendUrls.split(",")),
                                Arrays.stream(backendUrls.split(",")))
                        .map(String::trim)
                        .filter(url -> !url.isEmpty())
                        .toArray(String[]::new);

                registry.addMapping("/**")
                        .allowedOriginPatterns(allowedOriginPatterns)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .exposedHeaders("Authorization", "Content-Type")
                        .allowCredentials(true)
                        .maxAge(3600);
            }
        };
    }
}
