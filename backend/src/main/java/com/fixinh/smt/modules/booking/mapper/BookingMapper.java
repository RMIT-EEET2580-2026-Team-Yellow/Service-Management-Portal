package com.fixinh.smt.modules.booking.mapper;

import com.fixinh.smt.modules.booking.dto.external.BookingExternalDto;
import com.fixinh.smt.modules.booking.dto.internal.BookingResponse;
import com.fixinh.smt.modules.booking.entity.Booking;

import java.util.Collections;
import java.util.List;

public final class BookingMapper {

    private BookingMapper() {
    }

    /** No assigned technicians yet (e.g. right after creation) — use the overload once assignments exist. */
    public static BookingResponse toResponse(Booking booking) {
        return toResponse(booking, Collections.emptyList());
    }

    public static BookingResponse toResponse(Booking booking, List<String> assignedTechnicianNames) {
        Double totalCost = booking.getServiceFee() != null && booking.getMaterialCost() != null
                ? booking.getServiceFee() + booking.getMaterialCost()
                : null;
        return new BookingResponse(
                booking.getId(),
                booking.getCustomerId(),
                booking.getServiceType(),
                booking.getStatus(),
                booking.getServiceAddress(),
                booking.getIssueDescription(),
                booking.getPreferredDate(),
                booking.getAvailableStart(),
                booking.getAvailableEnd(),
                booking.getCreatedAt(),
                booking.getEstimatedServiceFee(),
                booking.getEstimatedDurationMinutes(),
                assignedTechnicianNames,
                booking.getServiceFee(),
                booking.getMaterialCost(),
                totalCost
        );
    }

    /** Exposed to SBP per 4.S1 — only the fields SBP is allowed to see (A.2.3). */
    public static BookingExternalDto toExternalDto(Booking booking) {
        Double totalCost = booking.getServiceFee() != null && booking.getMaterialCost() != null
                ? booking.getServiceFee() + booking.getMaterialCost()
                : null;
        return new BookingExternalDto(
                booking.getId(),
                booking.getCustomerId(),
                booking.getStatus(),
                booking.getPreferredDate(),
                totalCost
        );
    }
}
