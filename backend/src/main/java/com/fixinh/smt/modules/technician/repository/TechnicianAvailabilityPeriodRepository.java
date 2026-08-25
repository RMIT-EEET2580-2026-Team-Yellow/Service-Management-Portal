package com.fixinh.smt.modules.technician.repository;

import com.fixinh.smt.modules.technician.entity.TechnicianAvailabilityPeriod;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface TechnicianAvailabilityPeriodRepository
        extends MongoRepository<TechnicianAvailabilityPeriod, String> {

    List<TechnicianAvailabilityPeriod> findByTechnicianIdAndDateOrderByStartTimeAsc(
            String technicianId,
            LocalDate date
    );

    List<TechnicianAvailabilityPeriod> findByTechnicianIdAndDateBetweenOrderByDateAscStartTimeAsc(
            String technicianId,
            LocalDate from,
            LocalDate to
    );

    @Query("""
            {
              'technicianId': ?0,
              'date': ?1,
              'active': true,
              'startTime': { '$lt': ?3 },
              'endTime': { '$gt': ?2 }
            }
            """)
    List<TechnicianAvailabilityPeriod> findOverlapping(
            String technicianId,
            LocalDate date,
            LocalTime newStart,
            LocalTime newEnd
    );
}
