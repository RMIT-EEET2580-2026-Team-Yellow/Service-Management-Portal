package com.fixinh.smt.common.security;

import com.fixinh.smt.modules.staff.service.external.StaffExternalService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * Spring Security bridge. Only depends on {@link StaffExternalService} (the Staff module's
 * External Interface), never on Staff's internal service or repository (A.2.4).
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final StaffExternalService staffExternalService;

    public CustomUserDetailsService(StaffExternalService staffExternalService) {
        this.staffExternalService = staffExternalService;
    }

    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String usernameOrEmail) {
        // TODO Milestone 2: staffExternalService.findByEmail(...) then map to UserDetails.
        throw new UnsupportedOperationException("Milestone 2");
    }
}
