// Path in repo: backend/src/main/java/com/fixinh/smt/modules/technician/repository/TechnicianAvailabilityRepository.java
package com.fixinh.smt.modules.technician.repository;

import com.fixinh.smt.modules.technician.entity.TechnicianAvailability;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TechnicianAvailabilityRepository extends MongoRepository<TechnicianAvailability, String> {

    Optional<TechnicianAvailability> findByTechnicianId(String technicianId);
}
