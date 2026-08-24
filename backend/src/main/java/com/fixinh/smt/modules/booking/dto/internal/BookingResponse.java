package com.fixinh.smt.modules.booking.dto.internal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record BookingResponse(String id, String customerId, String serviceType, String status,
                               LocalDate preferredDate, LocalDateTime createdAt,
                               Double estimatedServiceFee, Integer estimatedDurationMinutes,
                               List<String> assignedTechnicianNames,
                               Double serviceFee, Double materialCost, Double totalCost) {
}
