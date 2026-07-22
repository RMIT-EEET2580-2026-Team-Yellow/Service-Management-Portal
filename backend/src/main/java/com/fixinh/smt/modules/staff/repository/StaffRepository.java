package com.fixinh.smt.modules.staff.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.fixinh.smt.modules.staff.entity.Staff;

import java.util.Optional;

public interface StaffRepository extends MongoRepository<Staff, String> {

    Optional<Staff> findByEmail(String email);

    boolean existsByEmail(String email);
}
