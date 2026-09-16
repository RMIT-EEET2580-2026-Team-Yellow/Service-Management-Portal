package com.fixinh.smt.modules.staff.dto.internal;

import com.fixinh.smt.common.security.RoleConfig;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** 3.1.S1/S3/S4 — Admin creates a Receptionist/Technician with a Branch Address. */
public record StaffRequest(
    @NotBlank(message = "Full name is required") String fullName,
    @NotBlank(message = "Email is required") String email,
    @NotBlank(message = "Phone number is required") String phoneNumber,
    @NotBlank(message = "Password is required") String password,
    @NotBlank(message = "Confirm password is required") String confirmPassword,
    @NotNull(message = "Role is required") RoleConfig role,
    @NotBlank(message = "Street number is required") String streetNumber,
    @NotBlank(message = "Street name is required") String streetName,
    @NotBlank(message = "Ward is required") String ward,
    @NotBlank(message = "Province is required") String province) {
}
