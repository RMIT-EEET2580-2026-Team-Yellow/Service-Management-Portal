package com.fixinh.smt.modules.auth.service;

import com.fixinh.smt.modules.auth.dto.internal.LoginRequest;
import com.fixinh.smt.modules.auth.dto.internal.LoginResponse;
import com.fixinh.smt.modules.auth.repository.UserRepository;
import com.fixinh.smt.modules.auth.service.internal.AuthInternalService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthInternalService {

    private final UserRepository userRepository;

    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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
}
