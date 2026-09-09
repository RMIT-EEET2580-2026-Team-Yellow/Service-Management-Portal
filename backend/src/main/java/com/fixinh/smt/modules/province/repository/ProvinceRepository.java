package com.fixinh.smt.modules.province.repository;

import com.fixinh.smt.modules.province.entity.Province;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ProvinceRepository extends MongoRepository<Province, String> {

    List<Province> findByRegion(String region);

    boolean existsByNameIgnoreCase(String name);

    Optional<Province> findByNameIgnoreCase(String name);
}
