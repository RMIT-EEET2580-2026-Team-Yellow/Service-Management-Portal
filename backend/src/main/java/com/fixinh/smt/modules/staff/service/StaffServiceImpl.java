package com.fixinh.smt.modules.staff.service;

import com.fixinh.smt.common.exception.ApiException;
import com.fixinh.smt.common.exception.ResourceNotFoundException;
import com.fixinh.smt.modules.auth.service.external.AuthExternalService;
import com.fixinh.smt.modules.staff.dto.external.StaffExternalDto;
import com.fixinh.smt.modules.staff.dto.internal.StaffRequest;
import com.fixinh.smt.modules.staff.dto.internal.StaffResponse;
import com.fixinh.smt.modules.staff.entity.Staff;
import com.fixinh.smt.modules.staff.mapper.StaffMapper;
import com.fixinh.smt.modules.staff.repository.StaffRepository;
import com.fixinh.smt.modules.staff.service.external.StaffExternalService;
import com.fixinh.smt.modules.staff.service.internal.StaffInternalService;
import com.fixinh.smt.common.util.ValidationUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/** Implements both interfaces (A.3.1/A.3.2) — split is at the interface level, not the impl. */
@Service
public class StaffServiceImpl implements StaffExternalService, StaffInternalService {

    private final StaffRepository staffRepository;
    private final AuthExternalService authExternalService;

    public StaffServiceImpl(StaffRepository staffRepository, AuthExternalService authExternalService) {
        this.staffRepository = staffRepository;
        this.authExternalService = authExternalService;
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
        if (!ValidationUtil.isValidEmail(request.email())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid email format");
        }

        if (!ValidationUtil.isValidPhoneNumber(request.phoneNumber())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid phone number format. Should have 7-11 digits and starts with 0");
        }

        if (!ValidationUtil.isValidPassword(request.password())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid password format. Password must be at least 8 characters, including a digit, an uppercase letter, and a special character");
        }

        if (!request.password().equals(request.confirmPassword())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Password and confirm password do not match");
        }

        if (staffRepository.existsByEmail(request.email())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        Staff staff = Staff.builder()
            .fullName(request.fullName())
            .email(request.email())
            .phoneNumber(request.phoneNumber())
            .role(request.role())
            .streetNumber(request.streetNumber())
            .streetName(request.streetName())
            .ward(request.ward())
            .province(request.province())
            .active(true)
            .build();

        Staff saved = staffRepository.save(staff);
        authExternalService.createCredentials(saved.getId(), saved.getEmail(), request.password(), saved.getRole());
        
        return StaffMapper.toResponse(saved);
    }

    @Override
    public StaffResponse getById(String staffId) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found: " + staffId));
        return StaffMapper.toResponse(staff);
    }

    @Override
    public List<StaffResponse> list() {
        return staffRepository.findAll().stream().map(StaffMapper::toResponse).toList();
    }

    @Override
    public StaffResponse update(String staffId, StaffRequest request) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found: " + staffId));

        staff.setFullName(request.fullName());
        staff.setEmail(request.email());
        staff.setPhoneNumber(request.phoneNumber());
        staff.setRole(request.role());
        staff.setStreetNumber(request.streetNumber());
        staff.setStreetName(request.streetName());
        staff.setWard(request.ward());
        staff.setProvince(request.province());

        Staff saved = staffRepository.save(staff);
        authExternalService.updateCredentials(saved.getId(), saved.getEmail(), saved.getRole());

        return StaffMapper.toResponse(saved);
    }

    @Override
    public void delete(String staffId) {
        if (!staffRepository.existsById(staffId)) {
            throw new ResourceNotFoundException("Staff not found: " + staffId);
        }
        authExternalService.deleteCredentials(staffId);
        staffRepository.deleteById(staffId);
    }
}
