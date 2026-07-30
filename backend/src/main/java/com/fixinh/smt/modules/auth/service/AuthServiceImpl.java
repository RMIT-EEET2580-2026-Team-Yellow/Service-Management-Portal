// Path in repo: backend/src/main/java/com/fixinh/smt/modules/auth/service/AuthServiceImpl.java
// CHANGE: now implements AuthExternalService too (was internal-only before) so Auth actually has
// the external/internal split shown in the diagrams (A.2.3/A.2.4).
package com.fixinh.smt.modules.auth.service;

import com.fixinh.smt.modules.auth.dto.external.AuthExternalDto;
import com.fixinh.smt.modules.auth.dto.internal.LoginRequest;
import com.fixinh.smt.modules.auth.dto.internal.LoginResponse;
import com.fixinh.smt.modules.auth.repository.UserRepository;
import com.fixinh.smt.modules.auth.service.external.AuthExternalService;
import com.fixinh.smt.modules.auth.service.internal.AuthInternalService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthInternalService, AuthExternalService {

    private final UserRepository userRepository;

    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ---- AuthInternalService (controller-only) ----

    @Override
    public LoginResponse login(LoginRequest request) {
        // TODO Milestone 2: verify hashed password, then delegate JWT creation to common.security.JwtUtil (3.2.S1/M1).
        throw new UnsupportedOperationException("Milestone 2");
    }

    @Override
    public void logout(String token) {
        // TODO Milestone 2: invalidate session client-side per 3.2.M4.
        throw new UnsupportedOperationException("Milestone 2");
    }

    @Override
    public LoginResponse loginWithGoogle(String googleIdToken) {
        // TODO Milestone 2: 3.2.U1-U3 — reject if no matching Staff account exists.
        throw new UnsupportedOperationException("Milestone 2");
    }

    // ---- AuthExternalService (consumed by common.security JwtFilter/CustomUserDetailsService) ----

    @Override
    public Optional<AuthExternalDto> validateToken(String token) {
        // TODO Milestone 2: parse via common.security.JwtUtil, look up User by subject claim,
        // return userId/staffId/email/role so other modules never touch User directly (A.2.4).
        throw new UnsupportedOperationException("Milestone 2");
    }
}
