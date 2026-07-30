// Path in repo: backend/src/main/java/com/fixinh/smt/modules/statistics/repository/StatisticsRepository.java
// NOTE: the current file in the repo is just an empty placeholder class — this replaces it with
// the real MongoRepository interface (originally proposed earlier this session).
package com.fixinh.smt.modules.statistics.repository;

import com.fixinh.smt.modules.statistics.entity.Statistics;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface StatisticsRepository extends MongoRepository<Statistics, String> {

    /** The overall snapshot for a period (no Service Type / Technician breakdown). */
    Optional<Statistics> findByPeriodTypeAndPeriodValueAndServiceTypeIsNullAndTechnicianIdIsNull(
            String periodType, String periodValue);

    /** Snapshots broken down by Service Type for a period (SRS 3.4.M3, 3.4.U1). */
    List<Statistics> findByPeriodTypeAndPeriodValueAndServiceTypeIsNotNull(
            String periodType, String periodValue);

    /** Snapshots broken down by Technician for a period (SRS 3.4.U2). */
    List<Statistics> findByPeriodTypeAndPeriodValueAndTechnicianIdIsNotNull(
            String periodType, String periodValue);

    List<Statistics> findByServiceType(String serviceType);

    List<Statistics> findByTechnicianId(String technicianId);
}
