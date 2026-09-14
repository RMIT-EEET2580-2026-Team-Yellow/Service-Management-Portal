package com.fixinh.smt.modules.servicetype.service;

import com.fixinh.smt.modules.servicetype.dto.internal.ServiceTypeResponse;
import com.fixinh.smt.modules.servicetype.entity.ServiceType;
import com.fixinh.smt.modules.servicetype.mapper.ServiceTypeMapper;
import com.fixinh.smt.modules.servicetype.repository.ServiceTypeRepository;
import com.fixinh.smt.modules.servicetype.service.external.ServiceTypeExternalService;
import com.fixinh.smt.modules.servicetype.service.internal.ServiceTypeInternalService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceTypeServiceImpl implements ServiceTypeInternalService, ServiceTypeExternalService {

    private final ServiceTypeRepository serviceTypeRepository;

    public ServiceTypeServiceImpl(ServiceTypeRepository serviceTypeRepository) {
        this.serviceTypeRepository = serviceTypeRepository;
    }

    @Override
    public List<ServiceTypeResponse> getAll() {
        return serviceTypeRepository.findAll().stream()
                .map(ServiceTypeMapper::toResponse)
                .toList();
    }

    @Override
    public List<String> listNames() {
        return serviceTypeRepository.findAll().stream()
                .map(ServiceType::getName)
                .toList();
    }
}
