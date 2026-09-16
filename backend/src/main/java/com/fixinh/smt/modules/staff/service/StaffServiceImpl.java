package com.fixinh.smt.modules.staff.service;

import com.fixinh.smt.common.exception.ApiException;
import com.fixinh.smt.common.exception.ResourceNotFoundException;
import com.fixinh.smt.common.sharding.ShardKeyResolver;
import com.fixinh.smt.common.sharding.ShardRegion;
import com.fixinh.smt.common.security.RoleConfig;
import com.fixinh.smt.modules.auth.service.external.AuthExternalService;
import com.fixinh.smt.modules.staff.dto.external.StaffExternalDto;
import com.fixinh.smt.modules.staff.dto.internal.StaffRequest;
import com.fixinh.smt.modules.staff.dto.internal.StaffResponse;
import com.fixinh.smt.modules.staff.entity.Staff;
import com.fixinh.smt.modules.staff.mapper.StaffMapper;
import com.fixinh.smt.modules.staff.repository.StaffShardRepository;
import com.fixinh.smt.modules.staff.service.external.StaffExternalService;
import com.fixinh.smt.modules.staff.service.internal.StaffInternalService;
import com.fixinh.smt.modules.technician.service.external.TechnicianExternalService;
import com.fixinh.smt.common.util.ValidationUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Implements both interfaces (A.3.1/A.3.2) — split is at the interface level, not the impl. */
@Service
public class StaffServiceImpl implements StaffExternalService, StaffInternalService {

    private final StaffShardRepository staffShardRepository;
    private final AuthExternalService authExternalService;
    private final ShardKeyResolver shardKeyResolver;
    private final TechnicianExternalService technicianExternalService;

    public StaffServiceImpl(StaffShardRepository staffShardRepository, AuthExternalService authExternalService,
                             ShardKeyResolver shardKeyResolver,
                             @Lazy // Technician module depends back on StaffExternalService — avoids a circular bean cycle
                             TechnicianExternalService technicianExternalService) {
        this.staffShardRepository = staffShardRepository;
        this.authExternalService = authExternalService;
        this.shardKeyResolver = shardKeyResolver;
        this.technicianExternalService = technicianExternalService;
    }


    @Override
    public Optional<StaffExternalDto> findByEmail(String email) {
        throw new UnsupportedOperationException("Milestone 2");
    }

    @Override
    public Optional<StaffExternalDto> findById(String staffId) {
        return staffShardRepository.findByIdWithRegion(staffId)
                .map(Map.Entry::getValue)
                .map(StaffMapper::toExternalDto);
    }

    @Override
    public List<StaffExternalDto> search(String nameIdOrEmail) {
        return staffShardRepository.findAllAcrossShards().stream()
                .filter(staff -> matchesQuery(staff, nameIdOrEmail))
                .map(StaffMapper::toExternalDto)
                .toList();
    }

    private boolean matchesQuery(Staff staff, String query) {
        if (!StringUtils.hasText(query)) {
            return true;
        }
        String needle = query.trim().toLowerCase();
        return (staff.getId() != null && staff.getId().toLowerCase().contains(needle))
                || (staff.getFullName() != null && staff.getFullName().toLowerCase().contains(needle))
                || (staff.getEmail() != null && staff.getEmail().toLowerCase().contains(needle));
    }

    // ---- StaffInternalService (controller-only, 3.1) ----

    @Override
    public StaffResponse create(StaffRequest request) {
        if (!ValidationUtil.isValidEmail(request.email())) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                "Invalid email format. Must contain exactly one '@', at least one '.' after it, "
                + "and be under 255 characters. Example: name@example.com");
        }

        if (!ValidationUtil.isValidPhoneNumber(request.phoneNumber())) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                "Invalid phone number format. Should have 7-11 digits and start with 0. Example: 0912345678");
        }

        if (!ValidationUtil.isValidPassword(request.password())) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                "Invalid password format. Password must be at least 8 characters, including a digit, "
                + "an uppercase letter, and a special character. Example: Passw0rd!");
        }

        if (!request.password().equals(request.confirmPassword())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Password and confirm password do not match");
        }

        if (staffShardRepository.existsByEmail(request.email())) {
            throw new ApiException(HttpStatus.CONFLICT, "Email already exists");
        }

        ShardRegion region = shardKeyResolver.resolve(request.province());

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

        Staff saved = staffShardRepository.insert(staff, region);
        authExternalService.createCredentials(saved.getId(), saved.getEmail(), request.password(), saved.getRole());

        if (saved.getRole() == RoleConfig.TECHNICIAN) {
            technicianExternalService.createDefaultProfile(saved.getId());
        }

        return StaffMapper.toResponse(saved);
    }

    @Override
    public StaffResponse getById(String staffId) {
        Staff staff = staffShardRepository.findByIdWithRegion(staffId)
                .map(Map.Entry::getValue)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found: " + staffId));
        return StaffMapper.toResponse(staff);
    }

    @Override
    public List<StaffResponse> list() {
        return staffShardRepository.findAllAcrossShards().stream().map(StaffMapper::toResponse).toList();
    }

    @Override
    public StaffResponse update(String staffId, StaffRequest request) {
        Map.Entry<ShardRegion, Staff> located = staffShardRepository.findByIdWithRegion(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found: " + staffId));
        ShardRegion currentRegion = located.getKey();
        Staff staff = located.getValue();

        if (!ValidationUtil.isValidEmail(request.email())) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                "Invalid email format. Must contain exactly one '@', at least one '.' after it, "
                + "and be under 255 characters. Example: name@example.com");
        }

        if (!ValidationUtil.isValidPhoneNumber(request.phoneNumber())) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                "Invalid phone number format. Should have 7-11 digits and start with 0. Example: 0912345678");
        }

        staff.setFullName(request.fullName());
        staff.setEmail(request.email());
        staff.setPhoneNumber(request.phoneNumber());
        staff.setRole(request.role());
        staff.setStreetNumber(request.streetNumber());
        staff.setStreetName(request.streetName());
        staff.setWard(request.ward());
        staff.setProvince(request.province());

        ShardRegion newRegion = shardKeyResolver.resolve(request.province());

        Staff saved;
        if (newRegion == currentRegion) {
            saved = staffShardRepository.save(staff, currentRegion);
        } else {
            staffShardRepository.delete(staff, currentRegion);
            saved = staffShardRepository.insert(staff, newRegion);
        }
        authExternalService.updateCredentials(saved.getId(), saved.getEmail(), saved.getRole());

        return StaffMapper.toResponse(saved);
    }

    @Override
    public void delete(String staffId) {
        Map.Entry<ShardRegion, Staff> located = staffShardRepository.findByIdWithRegion(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found: " + staffId));

        authExternalService.deleteCredentials(staffId);
        staffShardRepository.delete(located.getValue(), located.getKey());
    }

    @Override
    public List<StaffResponse> search(String query, RoleConfig role, Boolean active) {
        return staffShardRepository.findAllAcrossShards().stream()
                .filter(staff -> matchesQuery(staff, query))
                .filter(staff -> role == null || staff.getRole() == role)
                .filter(staff -> active == null || staff.isActive() == active)
                .map(StaffMapper::toResponse)
                .toList();
    }
}
