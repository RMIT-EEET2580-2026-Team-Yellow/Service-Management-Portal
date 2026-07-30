// Path in repo: backend/src/main/java/com/fixinh/smt/modules/technician/repository/TechnicianAssignmentRepository.java
package com.fixinh.smt.modules.technician.repository;

import com.fixinh.smt.modules.technician.entity.TechnicianAssignment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TechnicianAssignmentRepository extends MongoRepository<TechnicianAssignment, String> {

    List<TechnicianAssignment> findByBookingId(String bookingId);

    List<TechnicianAssignment> findByTechnicianId(String technicianId);

    List<TechnicianAssignment> findByBookingIdAndStatus(String bookingId, String status);
}
