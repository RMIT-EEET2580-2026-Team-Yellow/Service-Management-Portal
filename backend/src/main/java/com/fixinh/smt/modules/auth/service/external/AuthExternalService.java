// Path in repo: backend/src/main/java/com/fixinh/smt/modules/auth/service/external/AuthExternalService.java
package com.fixinh.smt.modules.auth.service.external;

import com.fixinh.smt.modules.auth.dto.external.AuthExternalDto;

import java.util.Optional;

/**
 * External Interface (A.3.1/A.3.2) — used by common.security (JwtFilter / CustomUserDetailsService)
 * and any other module that needs to resolve "who does this token belong to" without reaching
 * into the Auth module's internal service or the User entity directly (A.2.4).
 */
public interface AuthExternalService {

    Optional<AuthExternalDto> validateToken(String token);
}
