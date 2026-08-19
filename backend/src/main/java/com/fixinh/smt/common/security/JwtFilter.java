package com.fixinh.smt.common.security;

import com.fixinh.smt.modules.auth.dto.external.AuthExternalDto;
import com.fixinh.smt.modules.auth.service.external.AuthExternalService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/** Reads the Authorization header and populates the Spring Security context (3.2.M2/M3). */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final AuthExternalService authExternalService;

    public JwtFilter(AuthExternalService authExternalService) {
        this.authExternalService = authExternalService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            Optional<AuthExternalDto> authDto = authExternalService.validateToken(token);

            if (authDto.isPresent() && SecurityContextHolder.getContext().getAuthentication() == null) {
                AuthExternalDto dto = authDto.get();

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        dto.staffId(),
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + dto.role()))
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        chain.doFilter(request, response);
    }
}
