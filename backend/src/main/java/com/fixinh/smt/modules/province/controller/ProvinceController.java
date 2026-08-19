package com.fixinh.smt.modules.province.controller;

import com.fixinh.smt.modules.province.entity.Province;
import com.fixinh.smt.modules.province.service.ProvinceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Backs the Province dropdown in Address forms (Staff creation, Branch creation) — 3.1.S4. */
@RestController
@RequestMapping("/api/v1/provinces")
public class ProvinceController {

    @Autowired
    private ProvinceService provinceService;

    @GetMapping()
    public List<Province> getProvinces() {
        return provinceService.getAllProvinces();
    }

    @GetMapping("region/{region}")
    public List<Province> getProvincesByRegion(@PathVariable String region) {
        return provinceService.getProvincesByRegion(region);
    }
}
