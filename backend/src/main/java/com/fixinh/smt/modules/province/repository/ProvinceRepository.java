package com.fixinh.smt.modules.province.repository;

import com.fixinh.smt.modules.province.entity.Province;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProvinceRepository extends MongoRepository<Province, String> {

    List<Province> findByRegion(String region);

    boolean existsByNameIgnoreCase(String name);
}
