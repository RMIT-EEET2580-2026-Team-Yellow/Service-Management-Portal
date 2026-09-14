package com.fixinh.smt.modules.servicetype.mapper;

import com.fixinh.smt.modules.servicetype.dto.internal.ServiceTypeResponse;
import com.fixinh.smt.modules.servicetype.entity.ServiceType;

public final class ServiceTypeMapper {

    private ServiceTypeMapper() {
    }

    public static ServiceTypeResponse toResponse(ServiceType serviceType) {
        return new ServiceTypeResponse(serviceType.getId(), serviceType.getName());
    }
}
