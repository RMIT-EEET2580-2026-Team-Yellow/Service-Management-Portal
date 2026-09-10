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
        // SBP-created bookings don't have a "how many technicians" concept on their side — default to 1.
        Booking booking = buildAndSaveBooking(customerId, serviceType, issueDescription, serviceAddress,
                preferredDate, availableStart, availableEnd, 1);
        return BookingMapper.toExternalDto(booking);
    }

    // ---- BookingInternalService (controller-only, 3.3) ----

    @Override
    public BookingResponse create(BookingRequest request) {
        Booking booking = buildAndSaveBooking(request.customerId(), request.serviceType(), request.issueDescription(),
                request.serviceAddress(), request.preferredDate(), request.availableStart(), request.availableEnd(),
                request.requiredTechnicianCount());
        return BookingMapper.toResponse(booking);
    }

    /**
     * Shared by the Receptionist-facing create() and the SBP-facing createFromCustomer() (3.3.S1:
     * "Receptionists AND SBP" create a booking the same way). Validates the fields a customer/
     * Receptionist supplies (3.3.S2), then persists with status "CREATED".
     */
    private Booking buildAndSaveBooking(String customerId, String serviceType, String issueDescription,
                                         String serviceAddress, LocalDate preferredDate,
                                         LocalTime availableStart, LocalTime availableEnd,
                                         Integer requiredTechnicianCount) {
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
        if (requiredTechnicianCount != null && requiredTechnicianCount < 1) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "requiredTechnicianCount must be at least 1.");
        }

        Booking booking = Booking.builder()
                .customerId(customerId)
                .serviceType(serviceType)
                .issueDescription(issueDescription)
                .serviceAddress(serviceAddress)
                .preferredDate(preferredDate)
                .availableStart(availableStart)
                .availableEnd(availableEnd)
                .requiredTechnicianCount(requiredTechnicianCount != null ? requiredTechnicianCount : 1)
                .status("CREATED")
                .build();
        return bookingRepository.save(booking);
    }

    /** CREATED, CONFIRMED, SCHEDULED, WORKING (3.3.M2). */
    private static final List<String> ACTIVE_STATUSES = List.of("CREATED", "CONFIRMED", "SCHEDULED", "WORKING");

    /** COMPLETED, CANCELLED BY CUSTOMER, TERMINATED BY CUSTOMER, ABORTED BY TECHNICIAN (3.3.M2). */
    private static final List<String> PAST_STATUSES = List.of(
            "COMPLETED", "CANCELLED BY CUSTOMER", "TERMINATED BY CUSTOMER", "ABORTED BY TECHNICIAN");

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
                .map(this::toResponseWithAssignments)
                .toList();
    }

    /** 3.3.M9 — case-insensitive match on Booking ID or an assigned technician's full name. */
    @Override
    public List<BookingResponse> search(String query) {
        if (!StringUtils.hasText(query)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "query is required.");
        }
        String needle = query.trim().toLowerCase();

        return bookingRepository.findAll().stream()
                .filter(b -> matchesIdOrTechnician(b, needle))
                .sorted(Comparator.comparing(Booking::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(this::toResponseWithAssignments)
                .toList();
    }

    private BookingResponse toResponseWithAssignments(Booking booking) {
        return BookingMapper.toResponse(
                booking,
                technicianExternalService.findAssignedTechnicianNames(booking.getId()),
                technicianExternalService.countActiveAssignments(booking.getId())
        );
    }

    private boolean matchesIdOrTechnician(Booking booking, String lowercaseNeedle) {
        if (booking.getId() != null && booking.getId().toLowerCase().contains(lowercaseNeedle)) {
            return true;
        }
        return technicianExternalService.findAssignedTechnicianNames(booking.getId()).stream()
                .anyMatch(name -> name.toLowerCase().contains(lowercaseNeedle));
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
                .map(this::toResponseWithAssignments)
                .toList();
    }

    @Override
    public BookingResponse confirm(String bookingId, ConfirmBookingRequest request) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));

        if (!"CREATED".equals(booking.getStatus())) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Only a booking with status \"CREATED\" can be confirmed; this booking is \"" + booking.getStatus() + "\".");
        }
        if (request.estimatedServiceFee() <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "estimatedServiceFee must be greater than 0.");
        }
        if (request.estimatedDurationMinutes() <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "estimatedDurationMinutes must be greater than 0.");
        }

        booking.setEstimatedServiceFee(request.estimatedServiceFee());
        booking.setEstimatedDurationMinutes(request.estimatedDurationMinutes());
        booking.setStatus("CONFIRMED");
        // TODO Milestone 2: inform SBP that the booking is CONFIRMED (4.M3) — SbpIntegrationPort is
        // currently inbound-only (SMT calling SBP's Rating/Statistics/Customer APIs); there is no
        // outbound "push to SBP" client yet, so this still needs its own adapter to be built.
        Booking saved = bookingRepository.save(booking);

        // Auto-assign as many available technicians as this job needs (up to
        // requiredTechnicianCount) right away — the Receptionist only needs "Assign Technician"
        // as a manual top-up when auto-assign can't fully staff the job.
        autoAssignTechnicians(saved);

        return toResponseWithAssignments(saved);
    }

    /** Best-effort — silently assigns whoever is available up to requiredTechnicianCount and
     *  leaves the rest for manual "Assign Technician" if nobody (or not enough) is available. */
    private void autoAssignTechnicians(Booking booking) {
        int required = booking.getRequiredTechnicianCount() != null ? booking.getRequiredTechnicianCount() : 1;

        List<String> candidateIds = technicianExternalService
                .findAvailable(booking.getPreferredDate(), booking.getAvailableStart(), booking.getAvailableEnd(), booking.getEstimatedDurationMinutes())
                .stream()
                .map(TechnicianExternalDto::technicianId)
                .limit(required)
                .toList();

        if (!candidateIds.isEmpty()) {
            technicianExternalService.createAssignments(booking.getId(), candidateIds);
        }
    }

    @Override
    public BookingResponse assignTechnicians(String bookingId, List<String> technicianIds) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));

        if (!"CONFIRMED".equals(booking.getStatus())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Only a booking with status \"CONFIRMED\" can be assigned technicians; this booking is \"" + booking.getStatus() + "\".");
        }

        if (technicianIds == null || technicianIds.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "At least one technicianId is required.");
        }

        int required = booking.getRequiredTechnicianCount() != null ? booking.getRequiredTechnicianCount() : 1;
        int alreadyAssigned = technicianExternalService.countActiveAssignments(bookingId);

        if (alreadyAssigned + technicianIds.size() > required) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "This booking only needs " + required + " technician(s); " + alreadyAssigned
                            + " already assigned, so at most " + (required - alreadyAssigned) + " more can be added.");
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

        return toResponseWithAssignments(booking);
    }

    @Override
    public Optional<BookingExternalDto> tryMarkScheduled(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));

        if (!"CONFIRMED".equals(booking.getStatus())) {
            return Optional.empty(); // Booking is already SCHEDULED/WORKING or not assigned
        }

        if (!technicianExternalService.areAllAssignmentsAccepted(bookingId)) {
            return Optional.empty(); // Still waiting for technician(s) to accept assignment
        }

        booking.setStatus("SCHEDULED");
        Booking saved = bookingRepository.save(booking);
        
        return Optional.of(BookingMapper.toExternalDto(saved));
    }

    /** CREATED, CONFIRMED, SCHEDULED -> "CANCELLED BY CUSTOMER" (3.3.M1). */
    private static final List<String> CANCELLABLE_TO_CANCELLED = List.of("CREATED", "CONFIRMED", "SCHEDULED");

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
            newStatus = "CANCELLED BY CUSTOMER";
        } else if ("WORKING".equals(currentStatus)) {
            newStatus = "TERMINATED BY CUSTOMER";
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

        return toResponseWithAssignments(saved);
    }

    @Override
    public BookingExternalDto startWork(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));

        if (!"SCHEDULED".equals(booking.getStatus())) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Only a booking with status \"SCHEDULED\" can be started; this booking is \"" + booking.getStatus() + "\".");
        }

        booking.setStatus("WORKING");
        Booking saved = bookingRepository.save(booking);

        return BookingMapper.toExternalDto(saved);
    }

    @Override
    public BookingExternalDto completeWork(String bookingId, double serviceFee, double materialCost, String signedBillImageUrl) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));

        if (!"WORKING".equals(booking.getStatus())) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Only a booking with status \"WORKING\" can be completed; this booking is \"" + booking.getStatus() + "\".");
        }
        if (serviceFee < 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "serviceFee cannot be negative.");
        }
        if (materialCost < 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "materialCost cannot be negative.");
        }
        if (!StringUtils.hasText(signedBillImageUrl)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "signedBillImageUrl is required to complete a booking.");
        }

        booking.setStatus("COMPLETED");
        booking.setServiceFee(serviceFee);
        booking.setMaterialCost(materialCost);
        booking.setSignedBillImageUrl(signedBillImageUrl);
        Booking saved = bookingRepository.save(booking);

        return BookingMapper.toExternalDto(saved);
    }

    @Override
    public BookingExternalDto abortWork(String bookingId, String reason) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));

        if (!"WORKING".equals(booking.getStatus())) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Only a booking with status \"WORKING\" can be aborted; this booking is \"" + booking.getStatus() + "\".");
        }
        if (!StringUtils.hasText(reason)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "reason is required to abort a booking.");
        }

        booking.setStatus("ABORTED BY TECHNICIAN");
        booking.setCancelReason(reason);
        Booking saved = bookingRepository.save(booking);

        return BookingMapper.toExternalDto(saved);
    }
}
