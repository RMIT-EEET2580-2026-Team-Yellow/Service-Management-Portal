package com.fixinh.smt.modules.booking.dto.internal;

import java.time.LocalDate;
import java.time.LocalTime;

/** 3.3.S1/S2 — created by a Receptionist (SBP submits the equivalent via the API in 4.S1).
 *  customerId is a UUID string per the 30/7 agreement with SBT. */
public record BookingRequest(String customerId, String serviceType, String issueDescription,
                              String serviceAddress, LocalDate preferredDate,
                              LocalTime availableStart, LocalTime availableEnd) {
}
