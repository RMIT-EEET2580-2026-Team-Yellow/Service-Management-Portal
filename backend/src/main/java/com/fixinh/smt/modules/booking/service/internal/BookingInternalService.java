package com.fixinh.smt.modules.booking.service.internal;

import com.fixinh.smt.modules.booking.dto.internal.*;

import java.util.List;

/** Internal Interface (A.3.1/A.3.2) — consumed only by {@code BookingController}. */
public interface BookingInternalService {

    List<BookingResponse> listActive();

    List<BookingResponse> listPast();

    List<BookingResponse> search(String query);

    List<BookingResponse> filter(String serviceType, java.time.LocalDate from, java.time.LocalDate to, String sort);

    BookingResponse confirm(String bookingId, ConfirmBookingRequest request);

    BookingResponse assignTechnicians(String bookingId, List<String> technicianIds);

    BookingResponse cancel(String bookingId, CancelBookingRequest request);
}
