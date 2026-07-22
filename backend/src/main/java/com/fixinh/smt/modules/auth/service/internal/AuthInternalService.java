package com.fixinh.smt.modules.auth.service.internal;

import com.fixinh.smt.modules.auth.dto.internal.LoginRequest;
import com.fixinh.smt.modules.auth.dto.internal.LoginResponse;

/** Internal Interface (A.3.1/A.3.2) — consumed only by {@code AuthController}. */
public interface AuthInternalService {

    LoginResponse login(LoginRequest request);

    void logout(String token);

    LoginResponse loginWithGoogle(String googleIdToken);
}
