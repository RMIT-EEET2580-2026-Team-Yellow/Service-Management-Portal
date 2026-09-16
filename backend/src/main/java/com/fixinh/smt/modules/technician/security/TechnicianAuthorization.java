package com.fixinh.smt.modules.technician.security;

import com.fixinh.smt.modules.technician.repository.TechnicianRepository;
import org.springframework.stereotype.Component;

/**
 * SpEL helper for {@code @PreAuthorize} (referenced as {@code @technicianAuth} — the bean name
 * below). {@code authentication.name} is the caller's staffId (set from the JWT subject in
 * JwtFilter), never a Technician document's own generated id — so a raw
 * {@code authentication.name == #technicianId} comparison in a {@code @PreAuthorize} expression
 * can never be true for a TECHNICIAN caller. This resolves the {@code technicianId} path variable
 * back to its owning staffId before comparing.
 */
@Component("technicianAuth")
public class TechnicianAuthorization {

    private final TechnicianRepository technicianRepository;

    public TechnicianAuthorization(TechnicianRepository technicianRepository) {
        this.technicianRepository = technicianRepository;
    }

    public boolean isOwner(String technicianId, String staffId) {
        return technicianRepository.findById(technicianId)
            .map(technician -> technician.getStaffId().equals(staffId))
            .orElse(false);
    }
}
