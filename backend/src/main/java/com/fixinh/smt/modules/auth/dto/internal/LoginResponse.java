package com.fixinh.smt.modules.auth.dto.internal;

/** 3.2.M1 — returned JWT plus the authenticated Staff's role, for frontend route-guarding (A.2.a). */
public record LoginResponse(String token, String staffId, String role) {
}
