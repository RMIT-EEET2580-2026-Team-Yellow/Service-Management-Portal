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
        return toResponse(booking, Collections.emptyList(), 0);
    }

    public static BookingResponse toResponse(Booking booking, List<String> assignedTechnicianNames, int activeAssignmentCount) {
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
                booking.getRequiredTechnicianCount() != null ? booking.getRequiredTechnicianCount() : 1,
                activeAssignmentCount,
                booking.getServiceFee(),
                booking.getMaterialCost(),
                totalCost
        );
    }

    /** Exposed to SBP (4.S1) and to the Technician module's "My Bookings" page (3.3.M6-M8), and
     *  consumed internally by Statistics via BookingExternalService (3.4.S3/M3) for revenue-by-
     *  service-type reporting — only the fields another module is allowed to see (A.2.3). */
    public static BookingExternalDto toExternalDto(Booking booking) {
        Double totalCost = booking.getServiceFee() != null && booking.getMaterialCost() != null
                ? booking.getServiceFee() + booking.getMaterialCost()
                : null;
        return new BookingExternalDto(
                booking.getId(),
                booking.getCustomerId(),
                booking.getStatus(),
                booking.getServiceType(),
                booking.getIssueDescription(),
                booking.getServiceAddress(),
                booking.getPreferredDate(),
                booking.getAvailableStart(),
                booking.getAvailableEnd(),
                totalCost,
                booking.getServiceFee()
        );
    }
}
