package com.fixinh.smt.modules.booking.service;

import com.fixinh.smt.modules.booking.dto.external.BookingExternalDto;
import com.fixinh.smt.modules.booking.dto.internal.*;
import com.fixinh.smt.modules.booking.repository.BookingRepository;
import com.fixinh.smt.modules.booking.service.external.BookingExternalService;
import com.fixinh.smt.modules.booking.service.internal.BookingInternalService;
import com.fixinh.smt.modules.notification.service.external.NotificationExternalService;
import com.fixinh.smt.modules.technician.service.external.TechnicianExternalService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Implements both interfaces (A.3.1/A.3.2). Depends ONLY on other modules' External Interfaces
 * (A.2.4): {@link TechnicianExternalService} for auto-assignment (3.3.M3) and
 * {@link NotificationExternalService} for status-change alerts (3.3.U2, 4.M3).
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
    public BookingExternalDto createFromCustomer(Long customerId, String serviceType, String issueDescription,
                                                   String serviceAddress, LocalDate preferredDate,
                                                   java.time.LocalTime availableStart, java.time.LocalTime availableEnd) {
        // TODO Milestone 2: persist with status "Created" (3.3.S2, mirrors SBP-side booking creation).
        throw new UnsupportedOperationException("Milestone 2");
    }

    // ---- BookingInternalService (controller-only, 3.3) ----

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
        // TODO Milestone 2: set status "Confirmed" (3.3.S6), then notify via notificationExternalService (4.M3).
        throw new UnsupportedOperationException("Milestone 2");
    }

    @Override
    public BookingResponse assignTechnicians(String bookingId, List<String> technicianIds) {
        // TODO Milestone 2: call technicianExternalService.findAvailable(...) (3.3.M3), then set
        // status "Scheduled" once all Technicians accept (3.3.M5) and notify (3.3.U2, 4.M3).
        throw new UnsupportedOperationException("Milestone 2");
    }

    @Override
    public BookingResponse cancel(String bookingId, CancelBookingRequest request) {
        // TODO Milestone 2: 3.3.M1 status transition rules (Created/Confirmed/Scheduled -> Cancelled
        // by Customer; Working -> Terminated by Customer), then notify (3.3.U2).
        throw new UnsupportedOperationException("Milestone 2");
    }
}
