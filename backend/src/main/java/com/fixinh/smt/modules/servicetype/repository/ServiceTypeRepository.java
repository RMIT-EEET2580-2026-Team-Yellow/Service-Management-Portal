package com.fixinh.smt.modules.servicetype.repository;

import com.fixinh.smt.modules.servicetype.entity.ServiceType;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ServiceTypeRepository extends MongoRepository<ServiceType, String> {
}
