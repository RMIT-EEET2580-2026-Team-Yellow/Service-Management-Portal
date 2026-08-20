package com.fixinh.smt.modules.staff.dto.internal;

import com.fixinh.smt.common.security.RoleConfig;

/** 3.1.S1/S3/S4 — Admin creates a Receptionist/Technician with a Branch Address. */
public record StaffRequest(String fullName, String email, String phoneNumber, String password,
                            String confirmPassword, RoleConfig role,
                            String streetNumber, String streetName, String ward, String province) {
}
