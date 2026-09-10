package com.fixinh.smt.modules.auth.dto.internal;

/** Server-to-server auth for SBT's backend — a shared secret exchanged out-of-band (env var on
 *  both sides), not a Staff email/password. */
public record ServiceTokenRequest(String sharedSecret) {
}
