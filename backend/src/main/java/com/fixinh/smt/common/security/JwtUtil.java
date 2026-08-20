package com.fixinh.smt.common.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Shared JWT infrastructure (SRS 3.2.M1-M3): issues and validates the Staff token. Lives in
 * {@code common} — NOT inside the {@code auth} module — because every module's Controller Tier
 * needs it to authorize requests (A.2.a).
 *
 * <p>Uses HMAC (symmetric key, from {@code smt.jwt.secret}) rather than RSA — simpler, no
 * keystore file to manage, and matches the secret already declared in application.properties.
 */
@Component
public class JwtUtil {

    @Value("${smt.jwt.secret}")
    private String secret;

    @Value("${smt.jwt.expiration-minutes}")
    private long expirationMinutes;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String staffId, RoleConfig role) {
        return Jwts.builder()
                .subject(staffId)
                .claim("role", role.name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMinutes * 60 * 1000))
                .signWith(getSigningKey())
                .compact();
    }

    public boolean isValid(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractStaffId(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public RoleConfig extractRole(String token) {
        String role = Jwts.parser().verifyWith(getSigningKey()).build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
        return RoleConfig.valueOf(role);
    }
}
