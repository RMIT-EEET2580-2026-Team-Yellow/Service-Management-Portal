package com.fixinh.smt.modules.province.controller;

import com.fixinh.smt.modules.province.dto.internal.ProvinceResponse;
import com.fixinh.smt.modules.province.service.internal.ProvinceInternalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/provinces")
public class ProvinceController {

    @Autowired
    private ProvinceInternalService provinceService;

    @GetMapping()
    public List<ProvinceResponse> getProvinces() {
        return provinceService.getAllProvinces();
    }

    @GetMapping("region/{region}")
    public List<ProvinceResponse> getProvincesByRegion(@PathVariable String region) {
        return provinceService.getProvincesByRegion(region);
    }
}
