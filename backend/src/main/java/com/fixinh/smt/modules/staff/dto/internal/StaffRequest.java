package com.fixinh.smt.modules.staff.dto.internal;

/** 3.1.S1/S3/S4 — Admin creates a Receptionist/Technician with a Branch Address. */
public record StaffRequest(String fullName, String email, String phoneNumber, String password,
                            String confirmPassword, String role,
                            String streetNumber, String streetName, String ward, String province) {
}
