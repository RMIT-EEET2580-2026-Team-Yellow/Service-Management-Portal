// Path in repo: backend/src/main/java/com/fixinh/smt/modules/auth/service/external/AuthExternalService.java
package com.fixinh.smt.modules.auth.service.external;

import com.fixinh.smt.common.security.RoleConfig;
import com.fixinh.smt.modules.auth.dto.external.AuthExternalDto;

import java.util.Optional;

/**
 * External Interface (A.3.1/A.3.2) — used by common.security (JwtFilter / CustomUserDetailsService)
 * and any other module that needs to resolve "who does this token belong to" without reaching
 * into the Auth module's internal service or the User entity directly (A.2.4).

 * Staff never touches UserRepository directly (A.2.4).
 */
public interface AuthExternalService {

    Optional<AuthExternalDto> validateToken(String token);

    /** Creates the login credential (User) row for a newly created Staff account (3.1.S1). */
    void createCredentials(String staffId, String email, String rawPassword, RoleConfig role);

    /** Keeps the User row's email/role in sync after a Staff profile update. */
    void updateCredentials(String staffId, String email, RoleConfig role);

    /** Removes the login credential (User) row when a Staff account is deleted. */
    void deleteCredentials(String staffId);
}
