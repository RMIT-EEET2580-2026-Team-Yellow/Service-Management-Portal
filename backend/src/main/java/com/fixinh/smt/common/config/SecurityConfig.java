package com.fixinh.smt.common.config;

import com.fixinh.smt.common.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Role-based authorization (A.2.a): Staff/Technician cannot reach Admin-only routes, and a
 * Technician cannot reach another Technician's booking data.
 */
@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // TODO Milestone 2: permit /api/v1/auth/**, require role-based auth on the rest,
        // and register jwtFilter before UsernamePasswordAuthenticationFilter.
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
