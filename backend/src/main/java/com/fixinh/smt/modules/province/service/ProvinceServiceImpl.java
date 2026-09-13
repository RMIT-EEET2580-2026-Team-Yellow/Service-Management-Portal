package com.fixinh.smt.modules.province.service;

import com.fixinh.smt.modules.province.dto.internal.ProvinceResponse;
import com.fixinh.smt.modules.province.entity.Province;
import com.fixinh.smt.modules.province.mapper.ProvinceMapper;
import com.fixinh.smt.modules.province.repository.ProvinceRepository;
import com.fixinh.smt.modules.province.service.external.ProvinceExternalService;
import com.fixinh.smt.modules.province.service.internal.ProvinceInternalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProvinceServiceImpl implements ProvinceInternalService, ProvinceExternalService {

    @Autowired
    private ProvinceRepository repository;

    @Override
    public List<ProvinceResponse> getAllProvinces() {
        return repository.findAll().stream().map(ProvinceMapper::toResponse).toList();
    }

    @Override
    public List<ProvinceResponse> getProvincesByRegion(String region) {
        return repository.findByRegion(region).stream().map(ProvinceMapper::toResponse).toList();
    }

    @Override
    public String getRegionForProvince(String name) {
        return repository.findByNameIgnoreCase(name)
                .map(Province::getRegion)
                .orElse(null);
    }
}
