// Path in repo: backend/src/main/java/com/fixinh/smt/modules/booking/service/external/BookingExternalService.java
// CHANGE: createFromCustomer's customerId param Long -> String (UUID), per 30/7 agreement with SBT.
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

    /** SBP's "my bookings" list (their RealSmpClient.findBookingsByCustomer, EPIC3.4) - GET
     *  /api/v1/integration/bookings?customerId={id}, SERVICE-role only. */
    List<BookingExternalDto> findByCustomerId(String customerId);

    List<BookingExternalDto> findCompletedBetween(java.time.LocalDate from, java.time.LocalDate to);

    /** Called by SBP to create a booking (mirrors SBP-side 3.3.S1/S2) — 4.S1.
     *  customerId is a UUID string (was Long) per the 30/7 agreement with SBT. */
    BookingExternalDto createFromCustomer(String customerId, String serviceType, String issueDescription,
                                           String serviceAddress, java.time.LocalDate preferredDate,
                                           java.time.LocalTime availableStart, java.time.LocalTime availableEnd);

    Optional<BookingExternalDto> tryMarkScheduled(String bookingId);

    /** SCHEDULED -> WORKING (3.3.M6), called by the Technician module when its owning technician
     *  starts the job. Booking must never be written to directly from Technician (A.2.4). */
    BookingExternalDto startWork(String bookingId);

    /** WORKING -> COMPLETED (3.3.M7), records the final serviceFee/materialCost/signedBillImageUrl. */
    BookingExternalDto completeWork(String bookingId, double serviceFee, double materialCost, String signedBillImageUrl);

    /** WORKING -> "ABORTED BY TECHNICIAN" (3.3.M8). */
    BookingExternalDto abortWork(String bookingId, String reason);
}
