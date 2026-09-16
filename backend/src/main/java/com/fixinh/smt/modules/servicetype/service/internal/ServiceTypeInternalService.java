package com.fixinh.smt.modules.servicetype.service.internal;

import com.fixinh.smt.modules.servicetype.dto.internal.ServiceTypeResponse;

import java.util.List;

/** Internal Interface (A.3.1/A.3.2) — consumed by {@code ServiceTypeController}. */
public interface ServiceTypeInternalService {

    List<ServiceTypeResponse> getAll();
}
