// Path in repo: backend/src/main/java/com/fixinh/smt/modules/technician/entity/TechnicianAssignment.java
package com.fixinh.smt.modules.technician.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Join entity for the 1 Booking – n Technicians relationship (SRS 3.3.M3–M5). Replaces the old
 * flat {@code Booking.assignedTechnicianIds} list so each Technician's accept/reject response
 * can be tracked individually — needed because a Booking only becomes "Scheduled" once ALL
 * assigned Technicians have accepted (3.3.M5).
 *
 * Cardinality agreed with SBT (29–30/7): 1 Booking – n TechnicianAssignment.
 */
@Document(collection = "technician_assignments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TechnicianAssignment {

    @Id
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    private String bookingId;

    private String technicianId;

    /** PENDING | ACCEPTED | REJECTED */
    @Builder.Default
    private String status = "PENDING";

    @CreatedDate
    private LocalDateTime assignedAt;

    private LocalDateTime respondedAt;
}
