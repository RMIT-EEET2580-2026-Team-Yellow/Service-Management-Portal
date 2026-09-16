package com.fixinh.smt.modules.booking.service.internal;

import com.fixinh.smt.modules.booking.dto.internal.*;

import java.util.List;

/** Internal Interface (A.3.1/A.3.2) — consumed only by {@code BookingController}. */
public interface BookingInternalService {

    /** Receptionist creates a booking for exactly one Repair Service (3.3.S1/S2). */
    BookingResponse create(BookingRequest request);

    List<BookingResponse> listActive();

    List<BookingResponse> listPast();

    List<BookingResponse> search(String query);

    List<BookingResponse> filter(String serviceType, java.time.LocalDate from, java.time.LocalDate to, String sort);

    BookingResponse confirm(String bookingId, ConfirmBookingRequest request);

    BookingResponse assignTechnicians(String bookingId, List<String> technicianIds);

    BookingResponse cancel(String bookingId, CancelBookingRequest request);

    /** Looks up a customer by id against SBP so the Receptionist can confirm they have the right
     *  person before creating a booking on their behalf (4.M1). Throws if not found. */
    CustomerLookupResponse lookupCustomer(String customerId);
}
