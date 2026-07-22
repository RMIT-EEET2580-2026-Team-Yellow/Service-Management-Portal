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

/** Maps to the {@code bookings} collection. Mirrors the lifecycle defined in SRS 3.3. */
@Document(collection = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    private String id;

    private Long customerId; // owned by SBP; SMT only stores the reference (4.M1)

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

    private List<String> assignedTechnicianIds;

    private Double serviceFee;
    private Double materialCost;
    private String signedBillImageUrl;

    private String cancelReason;
    private List<String> cancelEvidenceImageUrls;

    @CreatedDate
    private LocalDateTime createdAt;
}
