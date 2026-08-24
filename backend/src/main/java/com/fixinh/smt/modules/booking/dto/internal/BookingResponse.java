package com.fixinh.smt.modules.booking.dto.internal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/** 3.3.S3/S4 — status, requested service, preferred date, creation date, plus the address/issue
 *  captured at creation (3.3.S2) and technician names once assigned (3.3.S4). */
public record BookingResponse(String id, String customerId, String serviceType, String status,
                               String serviceAddress, String issueDescription,
                               LocalDate preferredDate, LocalTime availableStart, LocalTime availableEnd,
                               LocalDateTime createdAt,
                               Double estimatedServiceFee, Integer estimatedDurationMinutes,
                               List<String> assignedTechnicianNames,
                               Double serviceFee, Double materialCost, Double totalCost) {
}
