package com.fixinh.smt.modules.staff.service.internal;

import com.fixinh.smt.modules.staff.dto.internal.StaffRequest;
import com.fixinh.smt.modules.staff.dto.internal.StaffResponse;

import java.util.List;

/** Internal Interface (A.3.1/A.3.2) — consumed only by {@code StaffController}. */
public interface StaffInternalService {

    StaffResponse create(StaffRequest request);

    StaffResponse getById(String staffId);

    List<StaffResponse> list();

    StaffResponse update(String staffId, StaffRequest request);
}
