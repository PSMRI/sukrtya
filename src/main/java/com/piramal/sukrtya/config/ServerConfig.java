package com.piramal.sukrtya.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class ServerConfig implements WebMvcConfigurer {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public String getServerUrl() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String scheme = request.getScheme();
            String serverName = request.getServerName();
            return scheme + "://" + serverName + ":" + serverPort;
        }
        // Fallback to localhost if no request context is available
        return "http://localhost:" + serverPort;
    }
} 