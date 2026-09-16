package com.fixinh.smt.modules.booking.dto.internal;

/** Result of looking up a customer by id against SBP before creating a booking on their behalf —
 *  lets the Receptionist confirm they have the right person, not just a well-formed id. */
public record CustomerLookupResponse(String customerId, String fullName, String phoneNumber) {
}
