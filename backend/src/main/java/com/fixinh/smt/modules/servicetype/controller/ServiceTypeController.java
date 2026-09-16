package com.fixinh.smt.modules.servicetype.controller;

import com.fixinh.smt.common.web.ApiResponse;
import com.fixinh.smt.modules.servicetype.dto.internal.ServiceTypeResponse;
import com.fixinh.smt.modules.servicetype.service.internal.ServiceTypeInternalService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/service-types")
public class ServiceTypeController {

    private final ServiceTypeInternalService serviceTypeInternalService;

    public ServiceTypeController(ServiceTypeInternalService serviceTypeInternalService) {
        this.serviceTypeInternalService = serviceTypeInternalService;
    }

    @GetMapping
    public ApiResponse<List<ServiceTypeResponse>> list() {
        return ApiResponse.ok(serviceTypeInternalService.getAll());
    }
}
