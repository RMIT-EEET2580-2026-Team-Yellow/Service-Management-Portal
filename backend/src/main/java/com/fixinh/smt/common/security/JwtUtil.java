package com.fixinh.smt.common.security;

import org.springframework.stereotype.Component;

/**
 * Shared JWT infrastructure (SRS 3.2.M1-M3): issues and validates the Staff token. Lives in
 * {@code common} — NOT inside the {@code auth} module — because every module's Controller Tier
 * needs it to authorize requests (A.2.a).
 */
@Component
public class JwtUtil {

    public String generateToken(Long staffId, String role) {
        // TODO Milestone 2: sign a JWT containing the Staff identifier and role.
        throw new UnsupportedOperationException("Milestone 2");
    }

    public boolean isValid(String token) {
        // TODO Milestone 2: validate signature and expiry (3.2.M3).
        throw new UnsupportedOperationException("Milestone 2");
    }

    public Long extractStaffId(String token) {
        throw new UnsupportedOperationException("Milestone 2");
    }
}
