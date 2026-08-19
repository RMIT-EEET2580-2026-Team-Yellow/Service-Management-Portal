package com.fixinh.smt.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Restrictive CORS (A.2.6): only the SMP frontend origin(s) are trusted — no wildcard.
 *
 * <p>Exposes a {@link CorsConfigurationSource} bean (not just a WebMvcConfigurer) so that
 * Spring Security's {@code .cors(Customizer.withDefaults())} in SecurityConfig can find and
 * apply the same rules — otherwise Security's filter chain runs before MVC's CORS handling
 * and would block cross-origin requests (including the login preflight) regardless of this.
 */
@Configuration
public class CorsConfig {

    @Value("${smt.cors.allowed-origin}")
    private String allowedOrigin;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(allowedOrigin));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
