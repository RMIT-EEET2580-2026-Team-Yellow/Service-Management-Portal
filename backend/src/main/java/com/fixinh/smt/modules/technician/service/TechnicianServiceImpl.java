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
import com.fixinh.smt.common.exception.ApiException;
import com.fixinh.smt.common.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.Set;

@Service
public class TechnicianServiceImpl implements TechnicianExternalService, TechnicianInternalService {

    private final TechnicianRepository technicianRepository;
    private final TechnicianAvailabilityRepository technicianAvailabilityRepository;
    private final TechnicianAvailabilityPeriodRepository technicianAvailabilityPeriodRepository;
    private final TechnicianAssignmentRepository technicianAssignmentRepository;
    private final StaffExternalService staffExternalService;
    private final com.fixinh.smt.modules.booking.service.external.BookingExternalService bookingExternalService;

    public TechnicianServiceImpl(TechnicianRepository technicianRepository,
        TechnicianAvailabilityRepository technicianAvailabilityRepository,
        TechnicianAvailabilityPeriodRepository technicianAvailabilityPeriodRepository,
        TechnicianAssignmentRepository technicianAssignmentRepository,
        StaffExternalService staffExternalService,
        @org.springframework.context.annotation.Lazy // To prevent circular constructor dependency
        BookingExternalService bookingExternalService) { 
        this.technicianRepository = technicianRepository;
        this.technicianAvailabilityRepository = technicianAvailabilityRepository;
        this.technicianAvailabilityPeriodRepository = technicianAvailabilityPeriodRepository;
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

        List<TechnicianAvailabilityPeriod> periods = technicianAvailabilityPeriodRepository.findByDateAndActiveTrue(date);

        return periods.stream()
            .filter(period -> !period.getStartTime().isAfter(start))
            .filter(period -> !period.getEndTime().isBefore(end))
            .map(TechnicianAvailabilityPeriod::getTechnicianId)
            .distinct()
            .map(technicianRepository::findById)
            .flatMap(Optional::stream)
            .map(this::toExternalDto)
            .toList();
    }

    @Override 
    public List<TechnicianExternalDto> findAvailableTechnicians(LocalDate date, LocalTime start, LocalTime end, int durationMinutes) {
        return findAvailable(date, start, end, durationMinutes);
    }

    @Override
    public boolean acceptAssignment(String technicianId, String bookingId) {
        // TODO Milestone 2: find the TechnicianAssignment row for (bookingId, technicianId),
        // set status="ACCEPTED" + respondedAt=now, save.
        throw new UnsupportedOperationException("Milestone 2");
    }

    @Override
    public boolean rejectAssignment(String technicianId, String bookingId) {
        // TODO Milestone 2: same lookup as acceptAssignment, set status="REJECTED" + respondedAt=now.
        throw new UnsupportedOperationException("Milestone 2");
    }

    @Override
    public void createAssignments(String bookingId, List<String> technicianIds) {
        if (technicianIds == null || technicianIds.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Technician IDs are required");
        }

        List<TechnicianAssignment> assignments = technicianIds.stream()
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

        Technician technician = technicianRepository.findByStaffId(request.staffId())
            .orElseGet(() -> technicianRepository.save(Technician.builder().staffId(request.staffId()).build()));

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

    private List<String> validateWorkingDays(List<String> workingDays) {
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

    @Override
    public List<BookingExternalDto> myBookings(String technicianId) {
        // TODO: Filter more bookings with other status as needed (Right now it's only scheduled bookings filtered)
        technicianRepository.findById(technicianId)
            .orElseThrow(() -> new ResourceNotFoundException("Technician not found with ID: " + technicianId));

        return technicianAssignmentRepository.findByTechnicianId(technicianId).stream()
            .map(TechnicianAssignment::getBookingId)
            .distinct()
            .map(bookingExternalService::findById)
            .flatMap(Optional::stream)
            .filter(booking -> "Scheduled".equals(booking.status()))
            .toList();
    }

    @Override
    public TechnicianResponse startWork(String technicianId, String bookingId) {
        throw new UnsupportedOperationException("Milestone 2");
    }

    @Override
    public TechnicianResponse complete(String technicianId, String bookingId, double serviceFee,
                                        double materialCost, String signedBillImageUrl) {
        throw new UnsupportedOperationException("Milestone 2");
    }

    @Override
    public TechnicianResponse abort(String technicianId, String bookingId, String reason) {
        throw new UnsupportedOperationException("Milestone 2");
    }
}
