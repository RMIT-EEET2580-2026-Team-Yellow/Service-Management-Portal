// Path in repo: backend/src/main/java/com/fixinh/smt/modules/technician/service/TechnicianServiceImpl.java
// CHANGE: implements the 2 new TechnicianExternalService methods (createAssignments,
// areAllAssignmentsAccepted) so TechnicianAssignmentRepository access stays fully inside this
// module (A.2.4) — BookingServiceImpl no longer injects that repository directly.
package com.fixinh.smt.modules.technician.service;

import com.fixinh.smt.common.security.RoleConfig;
import com.fixinh.smt.modules.booking.dto.external.BookingExternalDto;
import com.fixinh.smt.modules.booking.service.external.BookingExternalService;
import com.fixinh.smt.modules.staff.dto.external.StaffExternalDto;
import com.fixinh.smt.modules.staff.service.external.StaffExternalService;
import com.fixinh.smt.modules.technician.dto.external.TechnicianExternalDto;
import com.fixinh.smt.modules.technician.dto.external.TechnicianRef;
import com.fixinh.smt.modules.technician.dto.internal.TechnicianResponse;
import com.fixinh.smt.modules.technician.dto.internal.TechnicianScheduleRequest;
import com.fixinh.smt.modules.technician.entity.Technician;
import com.fixinh.smt.modules.technician.entity.TechnicianAssignment;
import com.fixinh.smt.modules.technician.entity.TechnicianAvailability;
import com.fixinh.smt.modules.technician.repository.TechnicianAssignmentRepository;
import com.fixinh.smt.modules.technician.repository.TechnicianAvailabilityRepository;
import com.fixinh.smt.modules.technician.repository.TechnicianRepository;
import com.fixinh.smt.modules.technician.service.external.TechnicianExternalService;
import com.fixinh.smt.modules.technician.service.internal.TechnicianInternalService;
import org.springframework.stereotype.Service;
import com.fixinh.smt.modules.technician.dto.internal.AvailabilityPeriodRequest;
import com.fixinh.smt.modules.technician.dto.internal.AvailabilityPeriodResponse;
import com.fixinh.smt.modules.technician.entity.TechnicianAvailabilityPeriod;
import com.fixinh.smt.modules.technician.repository.TechnicianAvailabilityPeriodRepository;
import com.fixinh.smt.modules.technician.dto.internal.ScheduleChangeRequestDto;
import com.fixinh.smt.modules.technician.dto.internal.ScheduleChangeResponse;
import com.fixinh.smt.modules.technician.dto.internal.TechnicianBookingResponse;
import com.fixinh.smt.modules.technician.entity.TechnicianScheduleChangeRequest;
import com.fixinh.smt.modules.technician.repository.TechnicianScheduleChangeRequestRepository;
import com.fixinh.smt.common.exception.ApiException;
import com.fixinh.smt.common.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.Set;

@Service
public class TechnicianServiceImpl implements TechnicianExternalService, TechnicianInternalService {

    /** A Technician-submitted schedule change request always uses the full 08:00-20:00 shift —
     *  the Technician only picks which 5 of 7 days they work, not custom hours. */
    private static final LocalTime FIXED_DAILY_START = LocalTime.of(8, 0);
    private static final LocalTime FIXED_DAILY_END = LocalTime.of(20, 0);

    /** Default base schedule a new Technician profile is created with (Mon-Fri, 08:00-20:00) —
     *  they can request a change afterwards via the schedule-change-request flow. */
    private static final List<String> DEFAULT_WORKING_DAYS = List.of("MON", "TUE", "WED", "THU", "FRI");

    private final TechnicianRepository technicianRepository;
    private final TechnicianAvailabilityRepository technicianAvailabilityRepository;
    private final TechnicianAvailabilityPeriodRepository technicianAvailabilityPeriodRepository;
    private final TechnicianScheduleChangeRequestRepository technicianScheduleChangeRequestRepository;
    private final TechnicianAssignmentRepository technicianAssignmentRepository;
    private final StaffExternalService staffExternalService;
    private final com.fixinh.smt.modules.booking.service.external.BookingExternalService bookingExternalService;

    public TechnicianServiceImpl(TechnicianRepository technicianRepository,
        TechnicianAvailabilityRepository technicianAvailabilityRepository,
        TechnicianAvailabilityPeriodRepository technicianAvailabilityPeriodRepository,
        TechnicianScheduleChangeRequestRepository technicianScheduleChangeRequestRepository,
        TechnicianAssignmentRepository technicianAssignmentRepository,
        StaffExternalService staffExternalService,
        @org.springframework.context.annotation.Lazy // To prevent circular constructor dependency
        BookingExternalService bookingExternalService) { 
        this.technicianRepository = technicianRepository;
        this.technicianAvailabilityRepository = technicianAvailabilityRepository;
        this.technicianAvailabilityPeriodRepository = technicianAvailabilityPeriodRepository;
        this.technicianScheduleChangeRequestRepository = technicianScheduleChangeRequestRepository;
        this.technicianAssignmentRepository = technicianAssignmentRepository;
        this.staffExternalService = staffExternalService;
        this.bookingExternalService = bookingExternalService;
    }

    private TechnicianExternalDto toExternalDto(Technician technician) {
        String fullName = staffExternalService.findById(technician.getStaffId())
            .map(StaffExternalDto::fullName)
            .orElse("Unknown technician");

        return new TechnicianExternalDto(
            technician.getId(),
            fullName,
            true
        );
    }

    @Override
    public AvailabilityPeriodResponse createAvailabilityPeriod(
        String technicianId,
        AvailabilityPeriodRequest request
    ) {
        Technician technician = technicianRepository.findById(technicianId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Technician not found: " + technicianId
            ));

        if (request.startTime() == null || request.endTime() == null) {
            throw new ApiException(
                HttpStatus.BAD_REQUEST,
                "startTime and endTime are required"
            );
        }

        if (!request.startTime().isBefore(request.endTime())) {
            throw new ApiException(
                HttpStatus.BAD_REQUEST,
                "startTime must be earlier than endTime"
            );
        }

        if (request.date().isBefore(LocalDate.now())) {
            throw new ApiException(
                HttpStatus.BAD_REQUEST,
                "Availability date cannot be in the past"
            );
        }

        TechnicianAvailability schedule = technicianAvailabilityRepository
            .findByTechnicianId(technicianId)
            .orElseThrow(() -> new ApiException(
                HttpStatus.BAD_REQUEST,
                "Technician does not have a base schedule"
            ));

        validateWorkingDay(schedule, request.date());
        validateInsideSchedule(schedule, request.startTime(), request.endTime());

        boolean hasOverlap = !technicianAvailabilityPeriodRepository.findOverlapping(
            technicianId,
            request.date(),
            request.startTime(),
            request.endTime()
        ).isEmpty();

        if (hasOverlap) {
            throw new ApiException(
                HttpStatus.CONFLICT,
                "Availability period overlaps an existing period"
            );
        }

        TechnicianAvailabilityPeriod period = TechnicianAvailabilityPeriod.builder()
            .technicianId(technician.getId())
            .date(request.date())
            .startTime(request.startTime())
            .endTime(request.endTime())
            .active(true)
            .build();

        TechnicianAvailabilityPeriod saved =
                technicianAvailabilityPeriodRepository.save(period);

        return toAvailabilityPeriodResponse(saved);
    }

    @Override
    public List<AvailabilityPeriodResponse> listAvailabilityPeriods(
        String technicianId,
        LocalDate from,
        LocalDate to
    ) {
        technicianRepository.findById(technicianId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Technician not found: " + technicianId
            ));

        List<TechnicianAvailabilityPeriod> periods;

        if (from != null && to != null) {
            if (from.isAfter(to)) {
                throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "from must be earlier than or equal to to"
                );
            }

            periods = technicianAvailabilityPeriodRepository
                .findByTechnicianIdAndDateBetweenOrderByDateAscStartTimeAsc(
                    technicianId,
                    from,
                    to
                );
        } else {
            periods = technicianAvailabilityPeriodRepository
                .findByTechnicianIdAndDateOrderByStartTimeAsc(
                    technicianId,
                    LocalDate.now()
                );
        }

        return periods.stream()
            .filter(TechnicianAvailabilityPeriod::isActive)
            .map(this::toAvailabilityPeriodResponse)
            .toList();
    }

    @Override
    public void deleteAvailabilityPeriod(
        String technicianId,
        String periodId
    ) {
        TechnicianAvailabilityPeriod period =
            technicianAvailabilityPeriodRepository.findById(periodId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Availability period not found: " + periodId
                ));

        if (!technicianId.equals(period.getTechnicianId())) {
            throw new ApiException(
                HttpStatus.NOT_FOUND,
                "Availability period does not belong to this technician"
            );
        }

        period.setActive(false);
        technicianAvailabilityPeriodRepository.save(period);
    }

    private AvailabilityPeriodResponse toAvailabilityPeriodResponse(
        TechnicianAvailabilityPeriod period
    ) {
        return new AvailabilityPeriodResponse(
            period.getId(),
            period.getTechnicianId(),
            period.getDate(),
            period.getStartTime(),
            period.getEndTime(),
            period.isActive()
        );
    }

    private void validateWorkingDay(
        TechnicianAvailability schedule,
        LocalDate date
    ) {
        String dayCode = toDayCode(date.getDayOfWeek());

        boolean isWorkingDay = schedule.getWorkingDays() != null
            && schedule.getWorkingDays().stream()
            .anyMatch(day -> dayCode.equalsIgnoreCase(day));

        if (!isWorkingDay) {
            throw new ApiException(
                HttpStatus.BAD_REQUEST,
                "The selected date is not a technician working day"
            );
        }
    }

    private String toDayCode(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> "MON";
            case TUESDAY -> "TUE";
            case WEDNESDAY -> "WED";
            case THURSDAY -> "THU";
            case FRIDAY -> "FRI";
            case SATURDAY -> "SAT";
            case SUNDAY -> "SUN";
        };
    }

    private void validateInsideSchedule(
            TechnicianAvailability schedule,
            LocalTime startTime,
            LocalTime endTime
    ) {
        if (schedule.getDailyStart() == null || schedule.getDailyEnd() == null) {
            throw new ApiException(
                HttpStatus.BAD_REQUEST,
                "Technician schedule has no working hours"
            );
        }

        boolean startsAfterSchedule =
            !startTime.isBefore(schedule.getDailyStart());

        boolean endsBeforeSchedule =
            !endTime.isAfter(schedule.getDailyEnd());

        if (!startsAfterSchedule || !endsBeforeSchedule) {
            throw new ApiException(
                HttpStatus.BAD_REQUEST,
                "Availability period must be inside the technician daily schedule"
            );
        }
    }

    // ---- TechnicianExternalService (consumed by Booking module, 3.3.M3/M4) ----

    @Override
    public List<TechnicianExternalDto> findAvailable(LocalDate date, LocalTime start, LocalTime end, int durationMinutes) {
        if (date == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Date is required");
        }

        if (start == null || end == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Start and end times are required");
        }

        if (!start.isBefore(end)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Start time must be earlier than end time");
        }

        if (durationMinutes <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Duration must be greater than 0");
        }

        long requestedWindowMinutes = java.time.Duration.between(start, end).toMinutes();

        if (requestedWindowMinutes < durationMinutes) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Requested time window is shorter than the required duration");
        }

        String dayCode = toDayCode(date.getDayOfWeek());

        // Base signal: the technician's recurring weekly schedule covers this day-of-week and
        // the requested window fits inside their daily hours.
        Set<String> scheduleMatches = technicianAvailabilityRepository.findAll().stream()
            .filter(availability -> availability.getWorkingDays() != null
                && availability.getWorkingDays().stream().anyMatch(day -> dayCode.equalsIgnoreCase(day)))
            .filter(availability -> availability.getDailyStart() != null && availability.getDailyEnd() != null)
            .filter(availability -> !availability.getDailyStart().isAfter(start) && !availability.getDailyEnd().isBefore(end))
            .map(TechnicianAvailability::getTechnicianId)
            .collect(java.util.stream.Collectors.toSet());

        // Extra signal: a one-off TechnicianAvailabilityPeriod for this exact date — covers a
        // technician picking up hours outside their normal weekly schedule.
        Set<String> periodMatches = technicianAvailabilityPeriodRepository.findByDateAndActiveTrue(date).stream()
            .filter(period -> !period.getStartTime().isAfter(start))
            .filter(period -> !period.getEndTime().isBefore(end))
            .map(TechnicianAvailabilityPeriod::getTechnicianId)
            .collect(java.util.stream.Collectors.toSet());

        Set<String> candidateIds = new HashSet<>(scheduleMatches);
        candidateIds.addAll(periodMatches);

        return candidateIds.stream()
            .filter(technicianId -> !isAlreadyBooked(technicianId, date, start, end))
            .map(technicianRepository::findById)
            .flatMap(Optional::stream)
            .map(this::toExternalDto)
            .toList();
    }

    /** True if this technician already has a PENDING/ACCEPTED assignment whose booking overlaps
     *  the requested date+window — keeps findAvailable from double-booking someone. */
    private boolean isAlreadyBooked(String technicianId, LocalDate date, LocalTime start, LocalTime end) {
        return technicianAssignmentRepository.findByTechnicianId(technicianId).stream()
            .filter(assignment -> "PENDING".equals(assignment.getStatus()) || "ACCEPTED".equals(assignment.getStatus()))
            .map(assignment -> bookingExternalService.findById(assignment.getBookingId()))
            .flatMap(Optional::stream)
            .anyMatch(booking -> date.equals(booking.preferredDate())
                && timeRangesOverlap(booking.availableStart(), booking.availableEnd(), start, end));
    }

    private boolean timeRangesOverlap(LocalTime aStart, LocalTime aEnd, LocalTime bStart, LocalTime bEnd) {
        if (aStart == null || aEnd == null) {
            return false;
        }
        return aStart.isBefore(bEnd) && bStart.isBefore(aEnd);
    }

    @Override 
    public List<TechnicianExternalDto> findAvailableTechnicians(LocalDate date, LocalTime start, LocalTime end, int durationMinutes) {
        return findAvailable(date, start, end, durationMinutes);
    }

    @Override
    public boolean acceptAssignment(String technicianId, String bookingId) {
        TechnicianAssignment assignment = requirePendingAssignment(technicianId, bookingId);

        assignment.setStatus("ACCEPTED");
        assignment.setRespondedAt(LocalDateTime.now());
        technicianAssignmentRepository.save(assignment);

        if (areAllAssignmentsAccepted(bookingId)) {
            // Flips the booking CONFIRMED -> SCHEDULED once every assigned technician has
            // accepted (3.3.M5) — a no-op (empty Optional) if some other assignment is still
            // pending, which is expected and not an error here.
            bookingExternalService.tryMarkScheduled(bookingId);
        }

        return true;
    }

    @Override
    public boolean rejectAssignment(String technicianId, String bookingId) {
        TechnicianAssignment assignment = requirePendingAssignment(technicianId, bookingId);

        assignment.setStatus("REJECTED");
        assignment.setRespondedAt(LocalDateTime.now());
        technicianAssignmentRepository.save(assignment);

        return true;
    }

    private TechnicianAssignment requirePendingAssignment(String technicianId, String bookingId) {
        TechnicianAssignment assignment = technicianAssignmentRepository
            .findByBookingIdAndTechnicianId(bookingId, technicianId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "No assignment found for technician " + technicianId + " on booking " + bookingId));

        if (!"PENDING".equals(assignment.getStatus())) {
            throw new ApiException(HttpStatus.CONFLICT,
                "This assignment has already been " + assignment.getStatus().toLowerCase());
        }

        return assignment;
    }

    /** Confirms this technician was actually assigned and accepted before letting them drive the
     *  booking's Working/Completed/Aborted lifecycle (defends against forging a bookingId onto
     *  someone else's job — role/ownership of the technicianId itself is already enforced by
     *  {@code @PreAuthorize(OWN_TECHNICIAN)} at the controller). */
    private void requireAcceptedAssignment(String technicianId, String bookingId) {
        TechnicianAssignment assignment = technicianAssignmentRepository
            .findByBookingIdAndTechnicianId(bookingId, technicianId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "No assignment found for technician " + technicianId + " on booking " + bookingId));

        if (!"ACCEPTED".equals(assignment.getStatus())) {
            throw new ApiException(HttpStatus.FORBIDDEN,
                "Technician " + technicianId + " has not accepted booking " + bookingId);
        }
    }

    @Override
    public void createAssignments(String bookingId, List<String> technicianIds) {
        if (technicianIds == null || technicianIds.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Technician IDs are required");
        }

        // Idempotent: skip anyone who already has a live (PENDING/ACCEPTED) assignment on this
        // booking, so calling this twice (e.g. auto-assign then a manual top-up) never double-books
        // the same technician onto the same job.
        Set<String> alreadyAssigned = technicianAssignmentRepository.findByBookingId(bookingId).stream()
            .filter(a -> "PENDING".equals(a.getStatus()) || "ACCEPTED".equals(a.getStatus()))
            .map(TechnicianAssignment::getTechnicianId)
            .collect(java.util.stream.Collectors.toSet());

        List<TechnicianAssignment> assignments = technicianIds.stream()
            .distinct()
            .filter(technicianId -> !alreadyAssigned.contains(technicianId))
            .map(technicianId -> TechnicianAssignment.builder()
                .bookingId(bookingId)
                .technicianId(technicianId)
                .status("PENDING")
                .build()
            ).toList();

        technicianAssignmentRepository.saveAll(assignments);
    }

    @Override
    public boolean areAllAssignmentsAccepted(String bookingId) {
        List<TechnicianAssignment> rows = technicianAssignmentRepository.findByBookingId(bookingId);
        return !rows.isEmpty() && rows.stream().allMatch(a -> "ACCEPTED".equals(a.getStatus()));
    }

    @Override
    public int countActiveAssignments(String bookingId) {
        return (int) technicianAssignmentRepository.findByBookingId(bookingId).stream()
            .filter(a -> "PENDING".equals(a.getStatus()) || "ACCEPTED".equals(a.getStatus()))
            .count();
    }

    @Override
    public List<String> findAssignedTechnicianNames(String bookingId) {
        return technicianAssignmentRepository.findByBookingId(bookingId).stream()
                .map(TechnicianAssignment::getTechnicianId)
                .map(technicianRepository::findById)
                .flatMap(Optional::stream)
                .map(Technician::getStaffId)
                .map(staffExternalService::findById)
                .flatMap(Optional::stream)
                .map(StaffExternalDto::fullName)
                .toList();
    }

    @Override
    public List<String> findAssignedTechnicianIds(String bookingId) {
        return technicianAssignmentRepository.findByBookingId(bookingId).stream()
                .map(TechnicianAssignment::getTechnicianId)
                .map(technicianRepository::findById)
                .flatMap(Optional::stream)
                .map(Technician::getStaffId)
                .toList();
    }

    @Override
    public List<TechnicianRef> listRefs() {
        return technicianRepository.findAll().stream()
                .map(technician -> new TechnicianRef(
                        technician.getStaffId(),
                        staffExternalService.findById(technician.getStaffId())
                                .map(StaffExternalDto::fullName)
                                .orElse("Unknown technician")))
                .toList();
    }

    // ---- TechnicianInternalService (controller-only, 3.3.M6-M8) ----

    @Override
    public TechnicianResponse setSchedule(TechnicianScheduleRequest request) {
        if (request.staffId() == null || request.staffId().isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "staffId is required");
        }

        StaffExternalDto staff = staffExternalService.findById(request.staffId())
            .orElseThrow(() -> new ResourceNotFoundException("Staff not found: " + request.staffId()));

        if (staff.role() != RoleConfig.TECHNICIAN) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Only TECHNICIAN role can have a schedule");
        }

        List<String> normalWorkingDays = validateWorkingDays(request.workingDays());
        validateDailyHours(request.dailyStart(), request.dailyEnd());

        Technician technician = getOrCreateTechnician(request.staffId());

        TechnicianAvailability availability = technicianAvailabilityRepository
            .findByTechnicianId(technician.getId())
            .orElseGet(() -> TechnicianAvailability.builder()
                .technicianId(technician.getId())
                .currentlyAvailable(true)
                .build());

        availability.setWorkingDays(normalWorkingDays);
        availability.setDailyStart(request.dailyStart());
        availability.setDailyEnd(request.dailyEnd());

        TechnicianAvailability saved = technicianAvailabilityRepository.save(availability);

        return toTechnicianResponse(technician, saved, staff.fullName());
    }

    @Override
    public TechnicianResponse getByStaffId(String staffId) {
        StaffExternalDto staff = staffExternalService.findById(staffId)
            .orElseThrow(() -> new ResourceNotFoundException("Staff not found: " + staffId));

        if (staff.role() != RoleConfig.TECHNICIAN) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Staff " + staffId + " is not a technician");
        }

        // Self-heal: a TECHNICIAN staff account may not have a Technician profile yet (e.g. it
        // was created before this account ever visited the schedule page, or before this
        // auto-provisioning existed) — provision one with the default Mon-Fri 08:00-20:00
        // schedule instead of failing with "Technician not found".
        Technician technician = getOrCreateTechnician(staffId);
        TechnicianAvailability availability = getOrCreateDefaultAvailability(technician.getId());

        return toTechnicianResponse(technician, availability, staff.fullName());
    }

    @Override
    public List<TechnicianResponse> listAll() {
        return technicianRepository.findAll().stream()
            .map(technician -> {
                TechnicianAvailability availability = getOrCreateDefaultAvailability(technician.getId());
                String fullName = staffExternalService.findById(technician.getStaffId())
                    .map(StaffExternalDto::fullName)
                    .orElse("Unknown technician");
                return toTechnicianResponse(technician, availability, fullName);
            })
            .toList();
    }

    @Override
    public void createDefaultProfile(String staffId) {
        Technician technician = getOrCreateTechnician(staffId);
        getOrCreateDefaultAvailability(technician.getId());
    }

    private Technician getOrCreateTechnician(String staffId) {
        return technicianRepository.findByStaffId(staffId)
            .orElseGet(() -> technicianRepository.save(Technician.builder().staffId(staffId).build()));
    }

    /** Provisions the Mon-Fri 08:00-20:00 default schedule the first time a Technician profile
     *  needs one — a Technician can move off this default via the schedule-change-request flow. */
    private TechnicianAvailability getOrCreateDefaultAvailability(String technicianId) {
        return technicianAvailabilityRepository.findByTechnicianId(technicianId)
            .orElseGet(() -> technicianAvailabilityRepository.save(
                TechnicianAvailability.builder()
                    .technicianId(technicianId)
                    .workingDays(DEFAULT_WORKING_DAYS)
                    .dailyStart(FIXED_DAILY_START)
                    .dailyEnd(FIXED_DAILY_END)
                    .currentlyAvailable(true)
                    .build()
            ));
    }

    private List<String> validateWorkingDays(List<String> workingDays) {
        List<String> normalized = validateFiveUniqueDays(workingDays);

        List<String> dayOrder = List.of("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN");
        int firstIndex = dayOrder.indexOf(normalized.get(0));

        for (int i = 1; i < normalized.size(); i++) {
            int expectedIndex = (firstIndex + i) % dayOrder.size();
            if (dayOrder.indexOf(normalized.get(i)) != expectedIndex) {
                throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "workingDays must be 5 consecutive days, e.g. [\"MON\",\"TUE\",\"WED\",\"THU\",\"FRI\"]"
                );
            }
        }

        return normalized;
    }

    /** Exactly 5 distinct valid day codes, with no consecutiveness check on its own —
     *  {@link #validateWorkingDays} layers the consecutiveness requirement on top of this. */
    private List<String> validateFiveUniqueDays(List<String> workingDays) {
        if (workingDays == null || workingDays.size() != 5) {
            throw new ApiException(
                HttpStatus.BAD_REQUEST,
                "workingDays must contain exactly 5 days"
            );
        }

        List<String> normalized = workingDays.stream()
            .map(day -> day == null ? null : day.trim().toUpperCase())
            .toList();

        Set<String> validCodes = Set.of("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN");

        if (normalized.stream().anyMatch(day -> day == null || !validCodes.contains(day))) {
            throw new ApiException(
                HttpStatus.BAD_REQUEST,
                "workingDays must only contain MON, TUE, WED, THU, FRI, SAT, SUN"
            );
        }

        if (new HashSet<>(normalized).size() != normalized.size()) {
            throw new ApiException(
                HttpStatus.BAD_REQUEST,
                "workingDays must not contain duplicates"
            );
        }

        return normalized;
    }

    private void validateDailyHours(LocalTime dailyStart, LocalTime dailyEnd) {
        if (dailyStart == null || dailyEnd == null) {
            throw new ApiException(
                HttpStatus.BAD_REQUEST,
                "dailyStart and dailyEnd are required"
            );
        }

        if (!dailyStart.isBefore(dailyEnd)) {
            throw new ApiException(
                HttpStatus.BAD_REQUEST,
                "dailyStart must be earlier than dailyEnd"
            );
        }

        if (dailyStart.isBefore(LocalTime.of(8, 0)) || dailyEnd.isAfter(LocalTime.of(20, 0))) {
            throw new ApiException(
                HttpStatus.BAD_REQUEST,
                "Working hours must be within 08:00-20:00"
            );
        }
    }

    private TechnicianResponse toTechnicianResponse(
        Technician technician,
        TechnicianAvailability availability,
        String fullName
    ) {
        return new TechnicianResponse(
            technician.getId(),
            technician.getStaffId(),
            fullName,
            availability.getWorkingDays(),
            availability.getDailyStart(),
            availability.getDailyEnd(),
            availability.isCurrentlyAvailable()
        );
    }

    // ---- Schedule change requests (Technician proposes, Admin approves/rejects) ----

    @Override
    public ScheduleChangeResponse submitScheduleChangeRequest(
        String technicianId,
        ScheduleChangeRequestDto request
    ) {
        technicianRepository.findById(technicianId)
            .orElseThrow(() -> new ResourceNotFoundException("Technician not found: " + technicianId));

        List<String> normalWorkingDays = validateWorkingDays(request.workingDays());

        technicianScheduleChangeRequestRepository.findByTechnicianIdAndStatus(technicianId, "PENDING")
            .ifPresent(existing -> {
                throw new ApiException(
                    HttpStatus.CONFLICT,
                    "A schedule change request is already pending approval"
                );
            });

        TechnicianScheduleChangeRequest saved = technicianScheduleChangeRequestRepository.save(
            TechnicianScheduleChangeRequest.builder()
                .technicianId(technicianId)
                .requestedWorkingDays(normalWorkingDays)
                .requestedDailyStart(FIXED_DAILY_START)
                .requestedDailyEnd(FIXED_DAILY_END)
                .build()
        );

        return toScheduleChangeResponse(saved);
    }

    @Override
    public List<ScheduleChangeResponse> listScheduleChangeRequests(String technicianId) {
        technicianRepository.findById(technicianId)
            .orElseThrow(() -> new ResourceNotFoundException("Technician not found: " + technicianId));

        return technicianScheduleChangeRequestRepository
            .findByTechnicianIdOrderByRequestedAtDesc(technicianId).stream()
            .map(this::toScheduleChangeResponse)
            .toList();
    }

    @Override
    public List<ScheduleChangeResponse> listPendingScheduleChangeRequests() {
        return technicianScheduleChangeRequestRepository
            .findByStatusOrderByRequestedAtAsc("PENDING").stream()
            .map(this::toScheduleChangeResponse)
            .toList();
    }

    @Override
    public ScheduleChangeResponse approveScheduleChangeRequest(String requestId) {
        TechnicianScheduleChangeRequest scheduleRequest = findPendingScheduleChangeRequest(requestId);

        Technician technician = technicianRepository.findById(scheduleRequest.getTechnicianId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Technician not found: " + scheduleRequest.getTechnicianId()
            ));

        TechnicianAvailability availability = technicianAvailabilityRepository
            .findByTechnicianId(technician.getId())
            .orElseGet(() -> TechnicianAvailability.builder()
                .technicianId(technician.getId())
                .currentlyAvailable(true)
                .build());

        availability.setWorkingDays(scheduleRequest.getRequestedWorkingDays());
        availability.setDailyStart(scheduleRequest.getRequestedDailyStart());
        availability.setDailyEnd(scheduleRequest.getRequestedDailyEnd());
        technicianAvailabilityRepository.save(availability);

        scheduleRequest.setStatus("APPROVED");
        scheduleRequest.setRespondedAt(LocalDateTime.now());
        TechnicianScheduleChangeRequest saved = technicianScheduleChangeRequestRepository.save(scheduleRequest);

        return toScheduleChangeResponse(saved);
    }

    @Override
    public ScheduleChangeResponse rejectScheduleChangeRequest(String requestId, String reason) {
        TechnicianScheduleChangeRequest scheduleRequest = findPendingScheduleChangeRequest(requestId);

        scheduleRequest.setStatus("REJECTED");
        scheduleRequest.setRejectReason(reason);
        scheduleRequest.setRespondedAt(LocalDateTime.now());
        TechnicianScheduleChangeRequest saved = technicianScheduleChangeRequestRepository.save(scheduleRequest);

        return toScheduleChangeResponse(saved);
    }

    private TechnicianScheduleChangeRequest findPendingScheduleChangeRequest(String requestId) {
        TechnicianScheduleChangeRequest scheduleRequest = technicianScheduleChangeRequestRepository
            .findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("Schedule change request not found: " + requestId));

        if (!"PENDING".equals(scheduleRequest.getStatus())) {
            throw new ApiException(
                HttpStatus.CONFLICT,
                "Schedule change request has already been " + scheduleRequest.getStatus().toLowerCase()
            );
        }

        return scheduleRequest;
    }

    private ScheduleChangeResponse toScheduleChangeResponse(TechnicianScheduleChangeRequest request) {
        Technician technician = technicianRepository.findById(request.getTechnicianId()).orElse(null);

        String fullName = technician == null
            ? "Unknown technician"
            : staffExternalService.findById(technician.getStaffId())
                .map(StaffExternalDto::fullName)
                .orElse("Unknown technician");

        TechnicianAvailability currentAvailability = technician == null
            ? null
            : technicianAvailabilityRepository.findByTechnicianId(technician.getId()).orElse(null);

        return new ScheduleChangeResponse(
            request.getId(),
            request.getTechnicianId(),
            fullName,
            currentAvailability == null ? null : currentAvailability.getWorkingDays(),
            currentAvailability == null ? null : currentAvailability.getDailyStart(),
            currentAvailability == null ? null : currentAvailability.getDailyEnd(),
            request.getRequestedWorkingDays(),
            request.getRequestedDailyStart(),
            request.getRequestedDailyEnd(),
            request.getStatus(),
            request.getRequestedAt(),
            request.getRespondedAt(),
            request.getRejectReason()
        );
    }

    @Override
    public List<TechnicianBookingResponse> myBookings(String technicianId) {
        technicianRepository.findById(technicianId)
            .orElseThrow(() -> new ResourceNotFoundException("Technician not found with ID: " + technicianId));

        return technicianAssignmentRepository.findByTechnicianId(technicianId).stream()
            .map(assignment -> bookingExternalService.findById(assignment.getBookingId())
                .map(booking -> toTechnicianBookingResponse(assignment, booking))
                .orElse(null))
            .filter(java.util.Objects::nonNull)
            .sorted(Comparator.comparing(
                TechnicianBookingResponse::preferredDate,
                Comparator.nullsLast(Comparator.naturalOrder())
            ).reversed())
            .toList();
    }

    private TechnicianBookingResponse toTechnicianBookingResponse(
        TechnicianAssignment assignment,
        BookingExternalDto booking
    ) {
        return new TechnicianBookingResponse(
            assignment.getId(),
            booking.bookingId(),
            assignment.getStatus(),
            booking.status(),
            booking.serviceType(),
            booking.issueDescription(),
            booking.serviceAddress(),
            booking.preferredDate(),
            booking.availableStart(),
            booking.availableEnd(),
            booking.totalCost()
        );
    }

    @Override
    public BookingExternalDto startWork(String technicianId, String bookingId) {
        requireAcceptedAssignment(technicianId, bookingId);
        return bookingExternalService.startWork(bookingId);
    }

    @Override
    public BookingExternalDto complete(String technicianId, String bookingId, double serviceFee,
                                        double materialCost, String signedBillImageUrl) {
        requireAcceptedAssignment(technicianId, bookingId);
        return bookingExternalService.completeWork(bookingId, serviceFee, materialCost, signedBillImageUrl);
    }

    @Override
    public BookingExternalDto abort(String technicianId, String bookingId, String reason) {
        requireAcceptedAssignment(technicianId, bookingId);
        return bookingExternalService.abortWork(bookingId, reason);
    }
}
