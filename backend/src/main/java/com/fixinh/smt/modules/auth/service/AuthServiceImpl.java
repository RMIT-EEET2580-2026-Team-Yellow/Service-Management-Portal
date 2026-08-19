// Path in repo: backend/src/main/java/com/fixinh/smt/modules/auth/service/AuthServiceImpl.java
// CHANGE: now implements AuthExternalService too (was internal-only before) so Auth actually has
// the external/internal split shown in the diagrams (A.2.3/A.2.4).
package com.fixinh.smt.modules.auth.service;

import com.fixinh.smt.common.exception.UnauthorizedException;
import com.fixinh.smt.common.security.JwtUtil;
import com.fixinh.smt.modules.auth.dto.external.AuthExternalDto;
import com.fixinh.smt.modules.auth.dto.internal.LoginRequest;
import com.fixinh.smt.modules.auth.dto.internal.LoginResponse;
import com.fixinh.smt.modules.auth.entity.User;
import com.fixinh.smt.modules.auth.repository.UserRepository;
import com.fixinh.smt.modules.auth.service.external.AuthExternalService;
import com.fixinh.smt.modules.auth.service.internal.AuthInternalService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthInternalService, AuthExternalService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // ---- AuthInternalService (controller-only) ----

    @Override
    public LoginResponse login(LoginRequest request) {
        // NOTE: User has no phone field yet, so this currently only matches by email —
        // 3.2.S1's "email/phone" login will need a phone field added to User once Staff
        // phone lookup is wired through, out of scope for the Master Admin login flow.
        User user = userRepository.findByEmail(request.emailOrPhone())
                .orElseThrow(() -> new UnauthorizedException("Email or password is incorrect"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Email or password is incorrect");
        }

        String token = jwtUtil.generateToken(user.getStaffId(), user.getRole());
        return new LoginResponse(token, user.getStaffId(), user.getRole());
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
        if (!jwtUtil.isValid(token)) {
            return Optional.empty();
        }

        String staffId = jwtUtil.extractStaffId(token);

        return userRepository.findByStaffId(staffId)
                .map(user -> new AuthExternalDto(user.getId(), user.getStaffId(), user.getEmail(), user.getRole()));
    }
}
