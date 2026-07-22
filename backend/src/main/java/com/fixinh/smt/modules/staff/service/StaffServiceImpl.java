package com.fixinh.smt.modules.staff.service;

import com.fixinh.smt.modules.staff.dto.external.StaffExternalDto;
import com.fixinh.smt.modules.staff.dto.internal.StaffRequest;
import com.fixinh.smt.modules.staff.dto.internal.StaffResponse;
import com.fixinh.smt.modules.staff.repository.StaffRepository;
import com.fixinh.smt.modules.staff.service.external.StaffExternalService;
import com.fixinh.smt.modules.staff.service.internal.StaffInternalService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/** Implements both interfaces (A.3.1/A.3.2) — split is at the interface level, not the impl. */
@Service
public class StaffServiceImpl implements StaffExternalService, StaffInternalService {

    private final StaffRepository staffRepository;

    public StaffServiceImpl(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    // ---- StaffExternalService (used by Auth module + Admin/SBT staff search, 3.5.M2/4.M4) ----

    @Override
    public Optional<StaffExternalDto> findByEmail(String email) {
        throw new UnsupportedOperationException("Milestone 2");
    }

    @Override
    public Optional<StaffExternalDto> findById(String staffId) {
        throw new UnsupportedOperationException("Milestone 2");
    }

    @Override
    public List<StaffExternalDto> search(String nameIdOrEmail) {
        // TODO Milestone 2: case-insensitive pattern match on name/id/email (3.5.M2).
        throw new UnsupportedOperationException("Milestone 2");
    }

    // ---- StaffInternalService (controller-only, 3.1) ----

    @Override
    public StaffResponse create(StaffRequest request) {
        // TODO Milestone 2: validate (3.1.M1-M4), resolve shard (3.1.U2), persist, hash password.
        throw new UnsupportedOperationException("Milestone 2");
    }

    @Override
    public StaffResponse getById(String staffId) {
        throw new UnsupportedOperationException("Milestone 2");
    }

    @Override
    public List<StaffResponse> list() {
        throw new UnsupportedOperationException("Milestone 2");
    }

    @Override
    public StaffResponse update(String staffId, StaffRequest request) {
        throw new UnsupportedOperationException("Milestone 2");
    }
}
