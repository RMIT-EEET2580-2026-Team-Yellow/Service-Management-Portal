package com.fixinh.smt.modules.booking.dto.external;

import java.time.LocalDate;

/**
 * External DTO (A.3.1/A.3.2). Exposed to SBP through the Booking/Technician Data API (4.S1);
 * SBP must NOT reach the Booking module's repository or internal service (A.2.4, 4.M2).
 */
public record BookingExternalDto(String bookingId, String customerId, String status,
                                  LocalDate preferredDate, Double totalCost,
                                  String serviceType, Double serviceFee) {
}
