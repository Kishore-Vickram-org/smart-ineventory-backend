package com.harbor.inventory.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final CorsProperties corsProperties;

    public CorsConfig(CorsProperties corsProperties) {
        this.corsProperties = corsProperties;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        CorsConfiguration corsConfiguration = buildCorsConfiguration();

        var registration = registry.addMapping("/**")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);

        List<String> allowedOrigins = corsConfiguration.getAllowedOrigins();
        List<String> allowedOriginPatterns = corsConfiguration.getAllowedOriginPatterns();

        if (allowedOrigins != null && !allowedOrigins.isEmpty()) {
            registration.allowedOrigins(allowedOrigins.toArray(String[]::new));
        } else {
            registration.allowedOriginPatterns((allowedOriginPatterns == null || allowedOriginPatterns.isEmpty())
                    ? new String[]{"*"}
                    : allowedOriginPatterns.toArray(String[]::new));
        }
    }

    /**
     * Exposes a {@link CorsConfigurationSource} that Spring Security can reuse when present.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", buildCorsConfiguration());
        return source;
    }

    /**
     * Registers a high-precedence {@link CorsFilter} so CORS works even when Spring Security is added.
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistration(CorsConfigurationSource corsConfigurationSource) {
        FilterRegistrationBean<CorsFilter> registrationBean = new FilterRegistrationBean<>(new CorsFilter(corsConfigurationSource));
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }

    private CorsConfiguration buildCorsConfiguration() {
        CorsConfiguration config = new CorsConfiguration();

        List<String> allowedOrigins = corsProperties.getAllowedOrigins();
        List<String> allowedOriginPatterns = corsProperties.getAllowedOriginPatterns();

        boolean hasExactOrigins = allowedOrigins != null && !allowedOrigins.isEmpty();
        boolean hasPatterns = allowedOriginPatterns != null && !allowedOriginPatterns.isEmpty();

        // Default to allow any origin.
        if (hasExactOrigins) {
            config.setAllowedOrigins(allowedOrigins);
        } else if (hasPatterns) {
            config.setAllowedOriginPatterns(allowedOriginPatterns);
        } else {
            config.addAllowedOriginPattern("*");
        }

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.addAllowedHeader("*");
        config.setAllowCredentials(false);
        config.setMaxAge(3600L);
        return config;
    }
}