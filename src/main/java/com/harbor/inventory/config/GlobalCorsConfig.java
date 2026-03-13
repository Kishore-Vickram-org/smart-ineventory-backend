package com.harbor.inventory.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class GlobalCorsConfig implements WebMvcConfigurer {

    private final CorsProperties corsProperties;

    public GlobalCorsConfig(CorsProperties corsProperties) {
        this.corsProperties = corsProperties;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        var mapping = registry.addMapping("/**")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);

        if (corsProperties.getAllowedOriginPatterns() != null && !corsProperties.getAllowedOriginPatterns().isEmpty()) {
            mapping.allowedOriginPatterns(corsProperties.getAllowedOriginPatterns().toArray(String[]::new));
            return;
        }

        if (corsProperties.getAllowedOrigins() != null && !corsProperties.getAllowedOrigins().isEmpty()) {
            mapping.allowedOrigins(corsProperties.getAllowedOrigins().toArray(String[]::new));
            return;
        }

        // If nothing is configured, default to a permissive dev-friendly behavior.
        mapping.allowedOriginPatterns("*");
    }
}
