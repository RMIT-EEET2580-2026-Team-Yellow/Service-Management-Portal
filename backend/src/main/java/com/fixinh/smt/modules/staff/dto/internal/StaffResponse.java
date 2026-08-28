package com.fixinh.smt.modules.staff.dto.internal;

import com.fixinh.smt.common.security.RoleConfig;

public record StaffResponse(String id, String fullName, String email, String phoneNumber,
                             RoleConfig role, String streetNumber, String streetName, String ward,
                             String province, boolean active) {
}
