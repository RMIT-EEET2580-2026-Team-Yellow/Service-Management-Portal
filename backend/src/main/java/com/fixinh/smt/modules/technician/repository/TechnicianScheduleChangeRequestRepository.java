package com.fixinh.smt.modules.technician.repository;

import com.fixinh.smt.modules.technician.entity.TechnicianScheduleChangeRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TechnicianScheduleChangeRequestRepository
        extends MongoRepository<TechnicianScheduleChangeRequest, String> {

    List<TechnicianScheduleChangeRequest> findByTechnicianIdOrderByRequestedAtDesc(String technicianId);

    List<TechnicianScheduleChangeRequest> findByStatusOrderByRequestedAtAsc(String status);

    Optional<TechnicianScheduleChangeRequest> findByTechnicianIdAndStatus(String technicianId, String status);
}
