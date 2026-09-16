package com.fixinh.smt.modules.auth.controller;

import com.fixinh.smt.common.web.ApiResponse;
import com.fixinh.smt.modules.auth.dto.internal.GoogleLoginRequest;
import com.fixinh.smt.modules.auth.dto.internal.LoginRequest;
import com.fixinh.smt.modules.auth.dto.internal.LoginResponse;
import com.fixinh.smt.modules.auth.dto.internal.ServiceTokenRequest;
import com.fixinh.smt.modules.auth.service.internal.AuthInternalService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthInternalService authInternalService;

    public AuthController(AuthInternalService authInternalService) {
        this.authInternalService = authInternalService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(authInternalService.login(request));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.startsWith("Bearer ")
                ? authorizationHeader.substring(7)
                : authorizationHeader;
        authInternalService.logout(token);
        return ApiResponse.ok(null);
    }

    @PostMapping("/google")
    public ApiResponse<LoginResponse> loginWithGoogle(@Valid @RequestBody GoogleLoginRequest request) {
        return ApiResponse.ok(authInternalService.loginWithGoogle(request.idToken()));
    }

    /** Server-to-server auth for SBP's backend calls
     *  this with the shared secret (env var, exchanged out-of-band) to get a SERVICE-role JWT,
     *  not a Staff login. */
    @PostMapping("/service-token")
    public ApiResponse<LoginResponse> issueServiceToken(@RequestBody ServiceTokenRequest request) {
        return ApiResponse.ok(authInternalService.issueServiceToken(request.sharedSecret()));
    }
}
