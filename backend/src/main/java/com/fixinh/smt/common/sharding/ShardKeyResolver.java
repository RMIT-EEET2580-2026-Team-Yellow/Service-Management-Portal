package com.fixinh.smt.common.sharding;

import com.fixinh.smt.common.exception.ApiException;
import com.fixinh.smt.modules.province.service.ProvinceService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


@Component
public class ShardKeyResolver {

    private final ProvinceService provinceService;

    public ShardKeyResolver(ProvinceService provinceService) {
        this.provinceService = provinceService;
    }

    public ShardRegion resolve(String province) {
        if (!StringUtils.hasText(province)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "province is required to determine the Staff shard.");
        }

        String region = provinceService.getRegionForProvince(province);
        if (region == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Unknown province '" + province + "'. Must exactly match one of the supported Vietnam provinces/cities.");
        }

        try {
            return ShardRegion.valueOf(region.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(
                    "Province '" + province + "' has an unrecognized region value in the database: " + region);
        }
    }
}
