package com.fixinh.smt.modules.staff.dto.internal;

public record StaffResponse(String id, String fullName, String email, String phoneNumber,
                             String role, String province, boolean active) {
}
