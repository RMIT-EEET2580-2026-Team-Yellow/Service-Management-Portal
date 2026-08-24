// Path in repo: backend/src/main/java/com/fixinh/smt/modules/booking/service/BookingServiceImpl.java
// FIX (this round): removed the direct TechnicianAssignmentRepository dependency — that broke
// A.2.4 (a module may only depend on another module's External Service, never its Repository).
// Booking now goes through TechnicianExternalService.createAssignments(...) and
// .areAllAssignmentsAccepted(...) instead.
package com.fixinh.smt.modules.booking.service;

import com.fixinh.smt.common.exception.ApiException;
import com.fixinh.smt.modules.booking.dto.external.BookingExternalDto;
import com.fixinh.smt.modules.booking.dto.internal.*;
import com.fixinh.smt.modules.booking.entity.Booking;
import com.fixinh.smt.modules.booking.mapper.BookingMapper;
import com.fixinh.smt.modules.booking.repository.BookingRepository;
import com.fixinh.smt.modules.booking.service.external.BookingExternalService;
import com.fixinh.smt.modules.booking.service.internal.BookingInternalService;
import com.fixinh.smt.modules.notification.service.external.NotificationExternalService;
import com.fixinh.smt.modules.technician.service.external.TechnicianExternalService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Implements both interfaces (A.3.1/A.3.2). Depends ONLY on other modules' External Interfaces
 * (A.2.4): {@link TechnicianExternalService} for auto-assignment AND for creating/checking
 * TechnicianAssignment rows (3.3.M3–M5), and {@link NotificationExternalService} for
 * status-change alerts (3.3.U2, 4.M3). No direct Repository access into another module.
 */
@Service
public class BookingServiceImpl implements BookingExternalService, BookingInternalService {

    private final BookingRepository bookingRepository;
    private final TechnicianExternalService technicianExternalService;
    private final NotificationExternalService notificationExternalService;

    public BookingServiceImpl(BookingRepository bookingRepository,
                               TechnicianExternalService technicianExternalService,
                               NotificationExternalService notificationExternalService) {
        this.bookingRepository = bookingRepository;
        this.technicianExternalService = technicianExternalService;
        this.notificationExternalService = notificationExternalService;
    }

    // ---- BookingExternalService (consumed by Statistics module + SBP integration, 4.S1) ----

    @Override
    public Optional<BookingExternalDto> findById(String bookingId) {
        throw new UnsupportedOperationException("Milestone 2");
    }

    @Override
    public List<BookingExternalDto> findCompletedBetween(LocalDate from, LocalDate to) {
        throw new UnsupportedOperationException("Milestone 2");
    }

    @Override
    public BookingExternalDto createFromCustomer(String customerId, String serviceType, String issueDescription,
                                                   String serviceAddress, LocalDate preferredDate,
                                                   LocalTime availableStart, LocalTime availableEnd) {
        Booking booking = buildAndSaveBooking(customerId, serviceType, issueDescription, serviceAddress,
                preferredDate, availableStart, availableEnd);
        return BookingMapper.toExternalDto(booking);
    }

    // ---- BookingInternalService (controller-only, 3.3) ----

    @Override
    public BookingResponse create(BookingRequest request) {
        Booking booking = buildAndSaveBooking(request.customerId(), request.serviceType(), request.issueDescription(),
                request.serviceAddress(), request.preferredDate(), request.availableStart(), request.availableEnd());
        return BookingMapper.toResponse(booking);
    }

    /**
     * Shared by the Receptionist-facing create() and the SBP-facing createFromCustomer() (3.3.S1:
     * "Receptionists AND SBP" create a booking the same way). Validates the fields a customer/
     * Receptionist supplies (3.3.S2), then persists with status "Created".
     */
    private Booking buildAndSaveBooking(String customerId, String serviceType, String issueDescription,
                                         String serviceAddress, LocalDate preferredDate,
                                         LocalTime availableStart, LocalTime availableEnd) {
        if (!StringUtils.hasText(customerId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "customerId is required: the booking must reference an existing customer.");
        }
        if (!StringUtils.hasText(serviceType)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "serviceType is required, e.g. \"Plumbing\", \"Electrical\", \"Electronics\".");
        }
        if (!StringUtils.hasText(serviceAddress)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "serviceAddress is required, e.g. \"22 Le Loi, District 1, HCMC\".");
        }
        if (!StringUtils.hasText(issueDescription)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "issueDescription is required, e.g. \"Sink pipe is leaking\".");
        }
        if (preferredDate == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "preferredDate is required and must be in the format YYYY-MM-DD.");
        }
        if (preferredDate.isBefore(LocalDate.now())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "preferredDate cannot be in the past: " + preferredDate + " is before today.");
        }
        if (availableStart == null || availableEnd == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "availableStart and availableEnd are both required, e.g. \"18:00\" to \"20:00\".");
        }
        if (!availableStart.isBefore(availableEnd)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "availableStart (" + availableStart + ") must be earlier than availableEnd (" + availableEnd + ").");
        }

        Booking booking = Booking.builder()
                .customerId(customerId)
                .serviceType(serviceType)
                .issueDescription(issueDescription)
                .serviceAddress(serviceAddress)
                .preferredDate(preferredDate)
                .availableStart(availableStart)
                .availableEnd(availableEnd)
                .status("Created")
                .build();
        return bookingRepository.save(booking);
    }

    @Override
    public List<BookingResponse> listActive() {
        throw new UnsupportedOperationException("Milestone 2");
    }

    @Override
    public List<BookingResponse> listPast() {
        throw new UnsupportedOperationException("Milestone 2");
    }

    @Override
    public List<BookingResponse> search(String query) {
        // TODO Milestone 2: case-insensitive match on Booking ID or Technician name (3.3.M9).
        throw new UnsupportedOperationException("Milestone 2");
    }

    @Override
    public List<BookingResponse> filter(String serviceType, LocalDate from, LocalDate to, String sort) {
        // TODO Milestone 2: filter by Service Type + Creation Date Range, sort asc/desc (3.3.M10).
        throw new UnsupportedOperationException("Milestone 2");
    }

    @Override
    public BookingResponse confirm(String bookingId, ConfirmBookingRequest request) {
        // TODO Milestone 2: set status "Confirmed" (3.3.S6), then notify SBP via SbpIntegrationPort
        // with the enriched DTO (scheduledDateTime, estimatedDuration, technician names) per the
        // 30/7 agreement — SBT enriches on their side, SMT just needs to send a fuller payload,
        // no separate GET endpoint required. Also notify via notificationExternalService (4.M3).
        throw new UnsupportedOperationException("Milestone 2");
    }

    @Override
    public BookingResponse assignTechnicians(String bookingId, List<String> technicianIds) {
        // TODO Milestone 2:
        //   1. technicianExternalService.findAvailable(...) (3.3.M3) to pick candidates.
        //   2. technicianExternalService.createAssignments(bookingId, technicianIds) — Booking
        //      never touches TechnicianAssignmentRepository directly (A.2.4).
        //   3. When technicianExternalService.areAllAssignmentsAccepted(bookingId) becomes true
        //      (checked on a later call, e.g. after each accept webhook), set status "Scheduled"
        //      (3.3.M5) and notify (3.3.U2, 4.M3) with technician full names in the payload.
        throw new UnsupportedOperationException("Milestone 2");
    }

    @Override
    public BookingResponse cancel(String bookingId, CancelBookingRequest request) {
        // TODO Milestone 2: 3.3.M1 status transition rules (Created/Confirmed/Scheduled -> Cancelled
        // by Customer; Working -> Terminated by Customer), then notify (3.3.U2).
        throw new UnsupportedOperationException("Milestone 2");
    }
}
