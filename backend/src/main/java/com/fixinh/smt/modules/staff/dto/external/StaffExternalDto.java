package com.fixinh.smt.modules.staff.dto.external;

/**
 * External DTO (A.3.1/A.3.2). The ONLY Staff shape the Auth module and the Admin/SBT-facing
 * Staff Search API (3.5.M2, 4.M4) may depend on.
 */
public record StaffExternalDto(String staffId, String fullName, String email, String role, boolean active) {
}
