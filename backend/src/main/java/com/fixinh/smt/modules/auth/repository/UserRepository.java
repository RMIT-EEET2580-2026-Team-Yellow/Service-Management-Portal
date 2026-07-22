package com.fixinh.smt.modules.auth.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.fixinh.smt.modules.auth.entity.User;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    Optional<User> findByGoogleId(String googleId);
}
