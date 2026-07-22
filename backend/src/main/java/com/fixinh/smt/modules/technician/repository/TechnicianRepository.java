package com.fixinh.smt.modules.technician.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.fixinh.smt.modules.technician.entity.Technician;

import java.util.Optional;

public interface TechnicianRepository extends MongoRepository<Technician, String> {

    Optional<Technician> findByStaffId(String staffId);
}
