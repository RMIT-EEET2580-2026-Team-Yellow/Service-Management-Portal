/**
 * Auth bounded context: Staff/Technician login, JWT issuance hand-off, logout (SRS 3.2.S1,
 * 3.2.M1-M4, 3.2.U1-U3). Delegates actual token creation to {@code common.security.JwtUtil}
 * since that infrastructure is shared by every module's Controller Tier.
 */
package com.fixinh.smt.modules.auth;
