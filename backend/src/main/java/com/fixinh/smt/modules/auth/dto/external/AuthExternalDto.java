// Path in repo: backend/src/main/java/com/fixinh/smt/modules/auth/dto/external/AuthExternalDto.java
package com.fixinh.smt.modules.auth.dto.external;

/**
 * External DTO (A.3.1/A.3.2). The ONLY Auth shape other modules or the Security & Web Config
 * layer may depend on when they need to know "who is this JWT for" without touching the
 * User entity or password hash directly.
 */
public record AuthExternalDto(String userId, String staffId, String email, String role) {
}
