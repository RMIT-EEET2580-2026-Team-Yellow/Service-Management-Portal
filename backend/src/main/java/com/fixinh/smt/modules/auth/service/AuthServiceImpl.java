// Path in repo: backend/src/main/java/com/fixinh/smt/modules/auth/service/AuthServiceImpl.java
// CHANGE: now implements AuthExternalService too (was internal-only before) so Auth actually has
// the external/internal split shown in the diagrams (A.2.3/A.2.4).
package com.fixinh.smt.modules.auth.service;

import com.fixinh.smt.common.exception.ResourceNotFoundException;
import com.fixinh.smt.common.exception.UnauthorizedException;
import com.fixinh.smt.common.security.JwtUtil;
import com.fixinh.smt.common.security.RoleConfig;
import com.fixinh.smt.modules.auth.dto.external.AuthExternalDto;
import com.fixinh.smt.modules.auth.dto.internal.LoginRequest;
import com.fixinh.smt.modules.auth.dto.internal.LoginResponse;
import com.fixinh.smt.modules.auth.entity.User;
import com.fixinh.smt.modules.auth.repository.UserRepository;
import com.fixinh.smt.modules.auth.service.external.AuthExternalService;
import com.fixinh.smt.modules.auth.service.internal.AuthInternalService;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.Optional;

@Slf4j
@Service
public class AuthServiceImpl implements AuthInternalService, AuthExternalService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RestClient googleRestClient;

    @Value("${smt.service.auth-secret}")
    private String serviceAuthSecret;

    @Value("${smt.service.subject:sbp-service}")
    private String serviceSubject;

    @Value("${smt.google.client-id:}")
    private String googleClientId;

    public AuthServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.googleRestClient = RestClient.builder().baseUrl("https://oauth2.googleapis.com").build();
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
        // Stateless JWT (3.2.M4): there is no server-side session to invalidate — the
        // client discards the token. Still validate it here so a malformed/expired
        // token surfaces as 401 rather than the endpoint silently no-op'ing.
        if (!jwtUtil.isValid(token)) {
            throw new UnauthorizedException("Invalid or expired token");
        }
    }

    @Override
    public LoginResponse loginWithGoogle(String googleIdToken) {
        // 3.2.U1-U3: Google only proves the person owns that email — it does not create an
        // account. Sign-in succeeds only if a Staff/Admin already has matching User credentials
        // (created via Staff CRUD, see createCredentials below); everyone else is rejected.
        GoogleTokenInfo tokenInfo = verifyGoogleIdToken(googleIdToken);

        User user = userRepository.findByEmail(tokenInfo.email())
                .orElseThrow(() -> new UnauthorizedException("No Fixinh account is linked to this Google email"));

        if (user.getGoogleId() == null) {
            user.setGoogleId(tokenInfo.sub());
            userRepository.save(user);
        }

        String token = jwtUtil.generateToken(user.getStaffId(), user.getRole());
        return new LoginResponse(token, user.getStaffId(), user.getRole());
    }

    /** Verifies the ID token's signature/expiry/audience via Google's tokeninfo endpoint (the
     *  documented low-volume-verification approach — fine at Staff-login scale; a JWKS-based
     *  local verifier would be the next step if this ever needs to scale beyond that). */
    private GoogleTokenInfo verifyGoogleIdToken(String googleIdToken) {
        GoogleTokenInfo tokenInfo;
        try {
            tokenInfo = googleRestClient.get()
                    .uri("/tokeninfo?id_token={token}", googleIdToken)
                    .retrieve()
                    .body(GoogleTokenInfo.class);
        } catch (RestClientResponseException e) {
            log.warn("Google tokeninfo rejected the ID token: {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new UnauthorizedException("Invalid or expired Google token");
        } catch (RestClientException e) {
            log.warn("Could not reach Google's tokeninfo endpoint: {}", e.getMessage());
            throw new UnauthorizedException("Invalid or expired Google token");
        }

        if (tokenInfo == null || tokenInfo.email() == null || !"true".equals(tokenInfo.emailVerified())) {
            throw new UnauthorizedException("Invalid or expired Google token");
        }
        if (googleClientId.isEmpty() || !googleClientId.equals(tokenInfo.aud())) {
            throw new UnauthorizedException("Google token was not issued for this application");
        }
        return tokenInfo;
    }

    private record GoogleTokenInfo(String sub, String email, @JsonProperty("email_verified") String emailVerified,
                                    String aud) {
    }

    @Override
    public LoginResponse issueServiceToken(String sharedSecret) {
        // Constant-ish comparison isn't the point here (this isn't a password hash) — the real
        // guard is that the secret is only ever exchanged with SBT out-of-band (env var on both
        // sides), never typed by an end user or stored per-Staff like a login credential.
        if (sharedSecret == null || !sharedSecret.equals(serviceAuthSecret)) {
            throw new UnauthorizedException("Invalid service credentials");
        }

        String token = jwtUtil.generateToken(serviceSubject, RoleConfig.SERVICE);
        return new LoginResponse(token, serviceSubject, RoleConfig.SERVICE);
    }

    // ---- AuthExternalService (consumed by common.security JwtFilter/CustomUserDetailsService,
    //      and by the Staff module for create/update/delete Staff CRUD, 3.5.S1) ----

    @Override
    public void createCredentials(String staffId, String email, String rawPassword, RoleConfig role) {
        User user = User.builder()
                .staffId(staffId)
                .email(email)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .role(role)
                .build();
        userRepository.save(user);
    }

    @Override
    public void updateCredentials(String staffId, String email, RoleConfig role) {
        User user = userRepository.findByStaffId(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("User credentials not found for staffId: " + staffId));
        user.setEmail(email);
        user.setRole(role);
        userRepository.save(user);
    }

    @Override
    public void deleteCredentials(String staffId) {
        userRepository.findByStaffId(staffId).ifPresent(userRepository::delete);
    }

    @Override
    public Optional<AuthExternalDto> validateToken(String token) {
        if (!jwtUtil.isValid(token)) {
            return Optional.empty();
        }

        String subject = jwtUtil.extractStaffId(token);
        RoleConfig role = jwtUtil.extractRole(token);

        if (role == RoleConfig.SERVICE) {
            return Optional.of(new AuthExternalDto(null, subject, null, RoleConfig.SERVICE));
        }

        return userRepository.findByStaffId(subject)
                .map(user -> new AuthExternalDto(user.getId(), user.getStaffId(), user.getEmail(), user.getRole()));
    }
}
