package com.fixinh.smt.modules.staff.service.internal;

import com.fixinh.smt.common.security.RoleConfig;
import com.fixinh.smt.modules.staff.dto.internal.StaffRequest;
import com.fixinh.smt.modules.staff.dto.internal.StaffResponse;

import java.util.List;
public interface StaffInternalService {

    StaffResponse create(StaffRequest request);

    StaffResponse getById(String staffId);

    List<StaffResponse> list();

    StaffResponse update(String staffId, StaffRequest request);

    void delete(String staffId);

    /** 3.5.M2/4.M4 — backs SBT's Admin Portal staff search (query, role, active all optional). */
    List<StaffResponse> search(String query, RoleConfig role, Boolean active);
}
