package com.fixinh.smt.modules.province.mapper;

import com.fixinh.smt.modules.province.dto.internal.ProvinceResponse;
import com.fixinh.smt.modules.province.entity.Province;

public final class ProvinceMapper {

    private ProvinceMapper() {
    }

    public static ProvinceResponse toResponse(Province province) {
        return new ProvinceResponse(province.getId(), province.getName(), province.getRegion());
    }
}
