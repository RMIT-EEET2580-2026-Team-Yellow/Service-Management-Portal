package com.fixinh.smt.modules.staff.controller;

import com.fixinh.smt.common.web.ApiResponse;
import com.fixinh.smt.modules.staff.dto.internal.StaffRequest;
import com.fixinh.smt.modules.staff.dto.internal.StaffResponse;
import com.fixinh.smt.modules.staff.service.internal.StaffInternalService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Also backs the Staff Search Interface consumed by SBT's Admin Portal (3.5.M2, 4.M4) —
 * exposed under {@code /api/v1/staff/search} for cross-portal use.
 *
 * <p>Managing staff accounts (and the roles they carry) is an ADMIN-only capability (A.2.a).
 */
@RestController
@RequestMapping("/api/v1/staff")
@PreAuthorize("hasRole('ADMIN')")
public class StaffController {

    private final StaffInternalService staffInternalService;

    public StaffController(StaffInternalService staffInternalService) {
        this.staffInternalService = staffInternalService;
    }

    @PostMapping
    public ApiResponse<StaffResponse> create(@Valid @RequestBody StaffRequest request) {
        return ApiResponse.ok(staffInternalService.create(request));
    }

    @GetMapping
    public ApiResponse<List<StaffResponse>> list() {
        return ApiResponse.ok(staffInternalService.list());
    }

    @GetMapping("/{staffId}")
    public ApiResponse<StaffResponse> getById(@PathVariable String staffId) {
        return ApiResponse.ok(staffInternalService.getById(staffId));
    }

    @PutMapping("/{staffId}")
    public ApiResponse<StaffResponse> update(@PathVariable String staffId, @Valid @RequestBody StaffRequest request) {
        return ApiResponse.ok(staffInternalService.update(staffId, request));
    }

    @DeleteMapping("/{staffId}")
    public ApiResponse<Void> delete(@PathVariable String staffId) {
        staffInternalService.delete(staffId);
        return ApiResponse.ok(null);
    }
}
