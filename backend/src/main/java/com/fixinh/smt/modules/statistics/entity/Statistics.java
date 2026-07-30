// Path in repo: backend/src/main/java/com/fixinh/smt/modules/statistics/entity/Statistics.java
// NOTE: the current file in the repo is just an empty placeholder class — this replaces it with
// the full entity (originally proposed earlier this session), now with a UUID id default too.
package com.fixinh.smt.modules.statistics.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

/**
 * Aggregated revenue and rating statistics, computed periodically (monthly/yearly),
 * optionally broken down by Service Type or Technician.
 *
 * Revenue figures are sourced from the Booking Module (service fees only, per SRS 3.4.S3).
 * Rating figures are sourced from SBP via the SBP Integration Adapter (per SRS 3.4.S1/S5, 3.4.U1/U2).
 */
@Document(collection = "statistics")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Statistics {

    @Id
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    /** "MONTHLY" or "YEARLY" */
    private String periodType;

    /** e.g. "2026-07" for MONTHLY, "2026" for YEARLY */
    private String periodValue;

    /** Optional — set when this snapshot is broken down per Service Type (SRS 3.4.M3, 3.4.U1) */
    private String serviceType;

    /** Optional — set when this snapshot is broken down per Technician (SRS 3.4.U2) */
    private String technicianId;

    /** Service revenue only, excludes material cost (SRS 3.4.S3) */
    private Double totalRevenue;

    private Integer completedBookingCount;

    /** Overall rating average (SRS 3.4.M1) */
    private Double overallRating;

    /** Sub-category rating averages (SRS 3.4.M1) */
    private Double integrityRating;
    private Double qualityRating;
    private Double friendlinessRating;

    private Instant computedAt;
}
