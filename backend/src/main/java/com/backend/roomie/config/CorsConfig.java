package com.backend.roomie.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    /**
     * Configures Cross-Origin Resource Sharing (CORS) settings for the application.
     * Allows requests from all origins with specified methods and headers
     * to access all endpoints.
     *
     * @return WebMvcConfigurer implementation that applies CORS configuration
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")  // Apply to all endpoints
                        .allowedOrigins("*")  // Allow all origins for testing
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*");
                // Note: removed allowCredentials(true) as it's incompatible with wildcard origins
            }
        };
    }
}
