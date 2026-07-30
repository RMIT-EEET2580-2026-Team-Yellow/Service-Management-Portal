// Path in repo: backend/src/main/java/com/fixinh/smt/modules/booking/entity/Booking.java
// CHANGES vs current repo (per 29-30/7 agreement with SBT):
//   1. customerId: Long -> String (UUID), SBP's customer PK is switching to UUID.
//   2. id: default now explicitly generated as a UUID string instead of relying on Mongo's ObjectId.
//   3. assignedTechnicianIds removed -> replaced by querying TechnicianAssignmentRepository.findByBookingId(id).
//      Do NOT keep a parallel list here; TechnicianAssignment is now the single source of truth (1 Booking - n Assignments).
//   4. branchId added directly on Booking (SMT's own proposal, sent 30/7, pending SBT confirmation) —
//      Statistics module queries by branch often enough that deriving it via address/province every time was worth avoiding.
package com.fixinh.smt.modules.booking.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/** Maps to the {@code bookings} collection. Mirrors the lifecycle defined in SRS 3.3. */
@Document(collection = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    private String customerId; // owned by SBP; SMT only stores the reference (4.M1). UUID string, per 29-30/7 agreement.

    private String branchId; // pending SBT confirmation (proposed 30/7) — see backlog 6.3/6.6

    private String serviceType;
    private String issueDescription;
    private String serviceAddress;

    private LocalDate preferredDate;
    private LocalTime availableStart;
    private LocalTime availableEnd;

    /** Created | Confirmed | Scheduled | Working | Completed | Cancelled by Customer |
     *  Terminated by Customer | Aborted by Technician. */
    private String status;

    private Double estimatedServiceFee;
    private Integer estimatedDurationMinutes;

    // assignedTechnicianIds removed — use TechnicianAssignmentRepository.findByBookingId(id) instead.

    private Double serviceFee;
    private Double materialCost;
    private String signedBillImageUrl;

    private String cancelReason;
    private List<String> cancelEvidenceImageUrls;

    @CreatedDate
    private LocalDateTime createdAt;
}
