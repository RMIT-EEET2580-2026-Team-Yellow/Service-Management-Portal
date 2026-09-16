package com.fixinh.smt.modules.auth.dto.internal;

import jakarta.validation.constraints.NotBlank;

/** 3.2.U1-U3 — Google Identity Services credential response, forwarded from the frontend.
 *  Wrapped in an object (rather than a bare {@code @RequestBody String}) because Spring's
 *  {@code StringHttpMessageConverter} reads a raw String body as-is instead of parsing it as
 *  JSON, which would leave the token's surrounding quotes in the value. */
public record GoogleLoginRequest(@NotBlank(message = "Google ID token is required") String idToken) {
}
