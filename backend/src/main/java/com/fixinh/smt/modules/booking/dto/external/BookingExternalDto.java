package com.fixinh.smt.modules.booking.dto.external;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * External DTO (A.3.1/A.3.2). Exposed to SBP through the Booking/Technician Data API (4.S1);
 * SBP must NOT reach the Booking module's repository or internal service (A.2.4, 4.M2).
 * serviceType/issueDescription/serviceAddress/availableStart/availableEnd are also what the
 * Technician module surfaces to a technician on their "My Bookings" page (3.3.M6-M8); serviceFee
 * is consumed by Statistics for revenue-by-service-type reporting (3.4.S3/M3).
 */
public record BookingExternalDto(String bookingId, String customerId, String status,
                                  String serviceType, String issueDescription, String serviceAddress,
                                  LocalDate preferredDate, LocalTime availableStart, LocalTime availableEnd,
                                  Double totalCost, Double serviceFee,
                                  LocalDateTime createdAt, List<String> assignedTechnicianNames,
                                  Double materialCost, Integer estimatedDurationMinutes,
                                  LocalDateTime scheduledDateTime,
                                  String signedBillImageUrl, List<String> cancelEvidenceImageUrls) {
}
