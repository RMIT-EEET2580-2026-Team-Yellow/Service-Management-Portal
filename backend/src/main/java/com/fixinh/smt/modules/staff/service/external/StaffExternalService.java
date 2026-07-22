package com.fixinh.smt.modules.staff.service.external;

import com.fixinh.smt.modules.staff.dto.external.StaffExternalDto;

import java.util.List;
import java.util.Optional;

/**
 * External Interface (A.3.1/A.3.2) — used by {@code common.security.CustomUserDetailsService}
 * for login, and by the Admin Staff Search endpoint that SBT's Admin Portal calls (3.5.M2, 4.M4).
 */
public interface StaffExternalService {

    Optional<StaffExternalDto> findByEmail(String email);

    Optional<StaffExternalDto> findById(String staffId);

    List<StaffExternalDto> search(String nameIdOrEmail);
}
