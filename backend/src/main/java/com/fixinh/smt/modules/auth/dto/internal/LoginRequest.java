package com.fixinh.smt.modules.auth.dto.internal;

/** 3.2.S1 — authenticate by email/phone + password. */
public record LoginRequest(String emailOrPhone, String password) {
}
