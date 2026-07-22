package com.fixinh.smt.modules.technician.dto.external;

/**
 * External DTO (A.3.1/A.3.2). Used by the Booking module for auto-assignment (3.3.M3) and by
 * the integration layer that exposes Technician info to SBP (4.S1).
 */
public record TechnicianExternalDto(String technicianId, String fullName, boolean availableNow) {
}
