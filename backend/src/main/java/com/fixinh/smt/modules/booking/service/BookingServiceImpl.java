// Path in repo: backend/src/main/java/com/fixinh/smt/modules/booking/service/BookingServiceImpl.java
// FIX (this round): removed the direct TechnicianAssignmentRepository dependency — that broke
// A.2.4 (a module may only depend on another module's External Service, never its Repository).
// Booking now goes through TechnicianExternalService.createAssignments(...) and
// .areAllAssignmentsAccepted(...) instead.
package com.fixinh.smt.modules.booking.service;

import com.fixinh.smt.common.exception.ApiException;
import com.fixinh.smt.common.exception.ResourceNotFoundException;
import com.fixinh.smt.modules.booking.dto.external.BookingExternalDto;
import com.fixinh.smt.modules.technician.dto.external.TechnicianExternalDto;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
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

    public BookingServiceImpl(
        BookingRepository bookingRepository,
        TechnicianExternalService technicianExternalService,
        NotificationExternalService notificationExternalService) {
        this.bookingRepository = bookingRepository;
        this.technicianExternalService = technicianExternalService;
        this.notificationExternalService = notificationExternalService;
    }

    // ---- BookingExternalService (consumed by Statistics module + SBP integration, 4.S1) ----

    @Override
    public Optional<BookingExternalDto> findById(String bookingId) {
        return bookingRepository.findById(bookingId)
            .map(BookingMapper::toExternalDto);
    }

    @Override
    public List<BookingExternalDto> findCompletedBetween(LocalDate from, LocalDate to) {
       
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.plusDays(1).atStartOfDay(); // exclusive upper bound, so `to` is inclusive
        return bookingRepository.findByStatusAndCreatedAtBetween("Completed", start, end).stream()
                .map(BookingMapper::toExternalDto)
                .toList();
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

    /** Created, Confirmed, Scheduled, Working (3.3.M2). */
    private static final List<String> ACTIVE_STATUSES = List.of("Created", "Confirmed", "Scheduled", "Working");

    /** Completed, Cancelled by Customer, Terminated by Customer, Aborted by Technician (3.3.M2). */
    private static final List<String> PAST_STATUSES = List.of(
            "Completed", "Cancelled by Customer", "Terminated by Customer", "Aborted by Technician");

    @Override
    public List<BookingResponse> listActive() {
        return listByStatuses(ACTIVE_STATUSES);
    }

    @Override
    public List<BookingResponse> listPast() {
        return listByStatuses(PAST_STATUSES);
    }

    /** 3.3.S3/S4/S5, M2 — newest first. Includes assigned technician full names (3.3.S4). */
    private List<BookingResponse> listByStatuses(List<String> statuses) {
        return bookingRepository.findByStatusIn(statuses).stream()
                .sorted(Comparator.comparing(Booking::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(booking -> BookingMapper.toResponse(booking, technicianExternalService.findAssignedTechnicianNames(booking.getId())))
                .toList();
    }

    @Override
    public List<BookingResponse> search(String query) {
        // TODO Milestone 2: case-insensitive match on Booking ID or Technician name (3.3.M9).
        throw new UnsupportedOperationException("Milestone 2");
    }

    @Override
    public List<BookingResponse> filter(String serviceType, LocalDate from, LocalDate to, String sort) {
        Comparator<Booking> byCreatedAt = Comparator.comparing(Booking::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()));
        if (!"asc".equalsIgnoreCase(sort)) {
            byCreatedAt = byCreatedAt.reversed();
        }

        return bookingRepository.findAll().stream()
                .filter(b -> !StringUtils.hasText(serviceType) || serviceType.equalsIgnoreCase(b.getServiceType()))
                .filter(b -> from == null || (b.getCreatedAt() != null && !b.getCreatedAt().toLocalDate().isBefore(from)))
                .filter(b -> to == null || (b.getCreatedAt() != null && !b.getCreatedAt().toLocalDate().isAfter(to)))
                .sorted(byCreatedAt)
                .map(booking -> BookingMapper.toResponse(booking, technicianExternalService.findAssignedTechnicianNames(booking.getId())))
                .toList();
    }

    @Override
    public BookingResponse confirm(String bookingId, ConfirmBookingRequest request) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));

        if (!"Created".equals(booking.getStatus())) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Only a booking with status \"Created\" can be confirmed; this booking is \"" + booking.getStatus() + "\".");
        }
        if (request.estimatedServiceFee() <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "estimatedServiceFee must be greater than 0.");
        }
        if (request.estimatedDurationMinutes() <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "estimatedDurationMinutes must be greater than 0.");
        }

        booking.setEstimatedServiceFee(request.estimatedServiceFee());
        booking.setEstimatedDurationMinutes(request.estimatedDurationMinutes());
        booking.setStatus("Confirmed");
        // TODO Milestone 2: inform SBP that the booking is Confirmed (4.M3) — SbpIntegrationPort is
        // currently inbound-only (SMT calling SBP's Rating/Statistics/Customer APIs); there is no
        // outbound "push to SBP" client yet, so this still needs its own adapter to be built.
        Booking saved = bookingRepository.save(booking);

        return BookingMapper.toResponse(saved, technicianExternalService.findAssignedTechnicianNames(bookingId));
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
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));

        if (!"Confirmed".equals(booking.getStatus())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Only a booking with status \"Confirmed\" can be assigned technicians; this booking is \"" + booking.getStatus() + "\".");
        }

        if (technicianIds == null || technicianIds.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "At least one technicianId is required.");
        }

        // 3.3.M3 — defensively re-check availability server-side (UI already filters this,
        // but never trust the client for the actual write).
        List<String> availableIds = technicianExternalService
                .findAvailable(booking.getPreferredDate(), booking.getAvailableStart(), booking.getAvailableEnd(), booking.getEstimatedDurationMinutes())
                .stream()
                .map(TechnicianExternalDto::technicianId)
                .toList();

        List<String> notAvailable = technicianIds.stream()
                .filter(id -> !availableIds.contains(id))
                .toList();

        if (!notAvailable.isEmpty()) {
            throw new ApiException(HttpStatus.CONFLICT, "These technicians are not available for the requested window: " + notAvailable);
        }

        // status stays "Confirmed" — only flips to "Scheduled" once ALL assignments are
        // ACCEPTED (3.3.M5, see tryMarkScheduled()).
        technicianExternalService.createAssignments(bookingId, technicianIds);

        return BookingMapper.toResponse(booking, technicianExternalService.findAssignedTechnicianNames(bookingId));
    }

    @Override
    public Optional<BookingExternalDto> tryMarkScheduled(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));

        if (!"Confirmed".equals(booking.getStatus())) {
            return Optional.empty(); // Booking is already Scheduled/Working or not assigned
        }

        if (!technicianExternalService.areAllAssignmentsAccepted(bookingId)) {
            return Optional.empty(); // Still waiting for technician(s) to accept assignment
        }

        booking.setStatus("Scheduled");
        Booking saved = bookingRepository.save(booking);
        
        return Optional.of(BookingMapper.toExternalDto(saved));
    }

    /** Created, Confirmed, Scheduled -> "Cancelled by Customer" (3.3.M1). */
    private static final List<String> CANCELLABLE_TO_CANCELLED = List.of("Created", "Confirmed", "Scheduled");

    @Override
    public BookingResponse cancel(String bookingId, CancelBookingRequest request) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));

        if (!StringUtils.hasText(request.reason())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "reason is required to cancel a booking.");
        }

        String currentStatus = booking.getStatus();
        String newStatus;
        if (CANCELLABLE_TO_CANCELLED.contains(currentStatus)) {
            newStatus = "Cancelled by Customer";
        } else if ("Working".equals(currentStatus)) {
            newStatus = "Terminated by Customer";
        } else {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Booking with status \"" + currentStatus + "\" cannot be cancelled.");
        }

        booking.setStatus(newStatus);
        booking.setCancelReason(request.reason());
        booking.setCancelEvidenceImageUrls(request.evidenceImageUrls());
        // TODO Milestone 2: notify (3.3.U2) once NotificationExternalService.notifyStatusChange
        // is implemented — calling it now would throw UnsupportedOperationException.
        Booking saved = bookingRepository.save(booking);

        return BookingMapper.toResponse(saved, technicianExternalService.findAssignedTechnicianNames(bookingId));
    }
}
