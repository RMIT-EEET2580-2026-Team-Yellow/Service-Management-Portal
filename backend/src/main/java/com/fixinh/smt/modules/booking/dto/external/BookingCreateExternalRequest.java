package com.fixinh.smt.modules.booking.dto.external;

import java.time.LocalDate;
import java.time.LocalTime;

/** External request DTO (A.3.2) — body SBP sends to POST /api/v1/integration/bookings (4.M2),
 *  mirrors the fields buildAndSaveBooking() validates for the Receptionist-facing create(). */
public record BookingCreateExternalRequest(String customerId, String serviceType, String issueDescription,
                                            String serviceAddress, LocalDate preferredDate,
                                            LocalTime availableStart, LocalTime availableEnd) {
}
