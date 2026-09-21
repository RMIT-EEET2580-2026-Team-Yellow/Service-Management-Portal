package com.fixinh.smt.modules.technician.dto.internal;

import java.time.LocalDate;
import java.time.LocalTime;

/** A booking as seen on a Technician's "My Bookings" page (3.3.M3-M8) — combines this
 *  technician's own assignment status with the booking fields exposed by BookingExternalDto. */
public record TechnicianBookingResponse(
    String assignmentId,
    String bookingId,
    String assignmentStatus, // PENDING | ACCEPTED | REJECTED
    String bookingStatus,    // CONFIRMED | SCHEDULED | WORKING | COMPLETED | ...
    String serviceType,
    String issueDescription,
    String serviceAddress,
    LocalDate preferredDate,
    LocalTime availableStart,
    LocalTime availableEnd,
    Double totalCost
) {
}
