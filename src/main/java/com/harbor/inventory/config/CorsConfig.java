package com.harbor.inventory.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.LinkedHashSet;
import java.util.List;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private static final String AZURE_FRONTEND_ORIGIN =
            "https://inventory-frontend-gafbefcmhpcmerg4.southeastasia-01.azurewebsites.net";

    private final CorsProperties corsProperties;

    public CorsConfig(CorsProperties corsProperties) {
        this.corsProperties = corsProperties;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        var allowedOrigins = new LinkedHashSet<String>();
        allowedOrigins.add(AZURE_FRONTEND_ORIGIN);

        List<String> configuredOrigins = corsProperties.getAllowedOrigins();
        if (configuredOrigins != null) {
            allowedOrigins.addAll(configuredOrigins);
        }

        // Note: We intentionally prefer exact origins for production.
        // If you need dev wildcards, set explicit origins in app.cors.allowed-origins.
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins.toArray(String[]::new))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }
}