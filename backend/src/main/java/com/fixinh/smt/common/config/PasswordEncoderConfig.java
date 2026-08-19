package com.fixinh.smt.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Kept separate from {@link SecurityConfig} on purpose: SecurityConfig depends on JwtFilter,
 * which (via AuthServiceImpl) depends on BCryptPasswordEncoder — declaring the encoder bean
 * inside SecurityConfig created a circular bean dependency (JwtFilter -> AuthServiceImpl ->
 * SecurityConfig -> JwtFilter).
 */
@Configuration
public class PasswordEncoderConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
