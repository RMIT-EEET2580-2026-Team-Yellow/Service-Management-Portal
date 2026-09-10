package com.fixinh.smt.modules.technician.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * A Technician's request to replace their {@link TechnicianAvailability} with a new working
 * schedule. Submitted by the Technician, takes effect only once an Admin approves it — the
 * schedule already in {@link TechnicianAvailability} stays live until then.
 */
@Document(collection = "technician_schedule_change_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TechnicianScheduleChangeRequest {

    @Id
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    private String technicianId;

    private List<String> requestedWorkingDays;

    private LocalTime requestedDailyStart;

    private LocalTime requestedDailyEnd;

    /** PENDING | APPROVED | REJECTED */
    @Builder.Default
    private String status = "PENDING";

    @Builder.Default
    private LocalDateTime requestedAt = LocalDateTime.now();

    private LocalDateTime respondedAt;

    private String rejectReason;
}
