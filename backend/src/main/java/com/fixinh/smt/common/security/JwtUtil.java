package com.fixinh.smt.common.security;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Shared JWT infrastructure (SRS 3.2.M1-M3): issues and validates the Staff token. Lives in
 * {@code common} — NOT inside the {@code auth} module — because every module's Controller Tier
 * needs it to authorize requests (A.2.a).
 *

 */
@Component
public class JwtUtil {

    private final KeyStoreManager keyStoreManager;

    @Value("${smt.jwt.expiration-minutes}")
    private long expirationMinutes;

    public JwtUtil(KeyStoreManager keyStoreManager) {
        this.keyStoreManager = keyStoreManager;
    }

    public String generateToken(String staffId, RoleConfig role) {
        return Jwts.builder()
                .subject(staffId)
                .claim("role", role.name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMinutes * 60 * 1000))
                .signWith(keyStoreManager.getPrivateKey(), Jwts.SIG.RS256)
                .compact();
    }

    public boolean isValid(String token) {
        try {
            Jwts.parser().verifyWith(keyStoreManager.getPublicKey()).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractStaffId(String token) {
        return Jwts.parser().verifyWith(keyStoreManager.getPublicKey()).build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public RoleConfig extractRole(String token) {
        String role = Jwts.parser().verifyWith(keyStoreManager.getPublicKey()).build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
        return RoleConfig.valueOf(role);
    }
}
