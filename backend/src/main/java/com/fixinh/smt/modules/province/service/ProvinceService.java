package com.fixinh.smt.modules.province.service;

import com.fixinh.smt.modules.province.entity.Province;
import com.fixinh.smt.modules.province.repository.ProvinceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProvinceService {

    @Autowired
    private ProvinceRepository repository;

    public List<Province> getAllProvinces() {
        return repository.findAll();
    }

    public List<Province> getProvincesByRegion(String region) {
        return repository.findByRegion(region);
    }
}
