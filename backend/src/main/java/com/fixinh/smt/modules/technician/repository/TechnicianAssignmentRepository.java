// Path in repo: backend/src/main/java/com/fixinh/smt/modules/technician/repository/TechnicianAssignmentRepository.java
package com.fixinh.smt.modules.technician.repository;

import com.fixinh.smt.modules.technician.entity.TechnicianAssignment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TechnicianAssignmentRepository extends MongoRepository<TechnicianAssignment, String> {

    List<TechnicianAssignment> findByBookingId(String bookingId);

    List<TechnicianAssignment> findByTechnicianId(String technicianId);

    List<TechnicianAssignment> findByBookingIdAndStatus(String bookingId, String status);

    Optional<TechnicianAssignment> findByBookingIdAndTechnicianId(String bookingId, String technicianId);
}
