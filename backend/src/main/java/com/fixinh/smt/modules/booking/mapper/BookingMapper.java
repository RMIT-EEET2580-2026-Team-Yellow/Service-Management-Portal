package com.fixinh.smt.modules.booking.mapper;

import com.fixinh.smt.modules.booking.dto.external.BookingExternalDto;
import com.fixinh.smt.modules.booking.dto.internal.BookingResponse;
import com.fixinh.smt.modules.booking.entity.Booking;

public final class BookingMapper {

    private BookingMapper() {
    }

    public static BookingResponse toResponse(Booking booking) {
        // TODO Milestone 2
        throw new UnsupportedOperationException("Milestone 2");
    }

    public static BookingExternalDto toExternalDto(Booking booking) {
        // TODO Milestone 2 (exposed to SBP per 4.S1)
        throw new UnsupportedOperationException("Milestone 2");
    }
}
