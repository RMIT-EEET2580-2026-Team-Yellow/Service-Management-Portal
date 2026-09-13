package com.fixinh.smt.modules.province.service.internal;

import com.fixinh.smt.modules.province.dto.internal.ProvinceResponse;

import java.util.List;

public interface ProvinceInternalService {

    List<ProvinceResponse> getAllProvinces();

    List<ProvinceResponse> getProvincesByRegion(String region);
}
