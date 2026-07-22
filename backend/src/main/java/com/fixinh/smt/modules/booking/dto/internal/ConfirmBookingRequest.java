package com.fixinh.smt.modules.booking.dto.internal;

/** 3.3.S6 — Receptionist confirms a Created booking with an estimated fee/duration. */
public record ConfirmBookingRequest(double estimatedServiceFee, int estimatedDurationMinutes) {
}
