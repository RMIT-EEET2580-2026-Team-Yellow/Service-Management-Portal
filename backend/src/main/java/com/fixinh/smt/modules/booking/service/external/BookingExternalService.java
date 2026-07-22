package com.fixinh.smt.modules.booking.service.external;

import com.fixinh.smt.modules.booking.dto.external.BookingExternalDto;

import java.util.List;
import java.util.Optional;

/**
 * External Interface (A.3.1/A.3.2) — consumed by the Statistics module (revenue calculation,
 * 3.4.S3) and by the REST layer that exposes Booking/Technician data to SBP (4.S1). SBP itself
 * must only reach this through HTTPS APIs, never by querying SMT's shard directly (4.U1).
 */
public interface BookingExternalService {

    Optional<BookingExternalDto> findById(String bookingId);

    List<BookingExternalDto> findCompletedBetween(java.time.LocalDate from, java.time.LocalDate to);

    /** Called by SBP to create a booking (mirrors SBP-side 3.3.S1/S2) — 4.S1. */
    BookingExternalDto createFromCustomer(Long customerId, String serviceType, String issueDescription,
                                           String serviceAddress, java.time.LocalDate preferredDate,
                                           java.time.LocalTime availableStart, java.time.LocalTime availableEnd);
}
