// Path in repo: backend/src/main/java/com/fixinh/smt/modules/technician/service/TechnicianServiceImpl.java
// CHANGE: implements the 2 new TechnicianExternalService methods (createAssignments,
// areAllAssignmentsAccepted) so TechnicianAssignmentRepository access stays fully inside this
// module (A.2.4) — BookingServiceImpl no longer injects that repository directly.
package com.fixinh.smt.modules.technician.service;

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

@Service
public class TechnicianServiceImpl implements TechnicianExternalService, TechnicianInternalService {

    private final TechnicianRepository technicianRepository;
    private final TechnicianAvailabilityRepository technicianAvailabilityRepository;
    private final TechnicianAvailabilityPeriodRepository technicianAvailabilityPeriodRepository;
    private final TechnicianAssignmentRepository technicianAssignmentRepository;
    private final StaffExternalService staffExternalService;

    public TechnicianServiceImpl(TechnicianRepository technicianRepository,
        TechnicianAvailabilityRepository technicianAvailabilityRepository,
        TechnicianAvailabilityPeriodRepository technicianAvailabilityPeriodRepository,
        TechnicianAssignmentRepository technicianAssignmentRepository,
        StaffExternalService staffExternalService) {
        this.technicianRepository = technicianRepository;
        this.technicianAvailabilityRepository = technicianAvailabilityRepository;
        this.technicianAvailabilityPeriodRepository = technicianAvailabilityPeriodRepository;
        this.technicianAssignmentRepository = technicianAssignmentRepository;
        this.staffExternalService = staffExternalService;
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
        // TODO Milestone 2: for each technicianId, technicianAssignmentRepository.save(
        //   TechnicianAssignment.builder().bookingId(bookingId).technicianId(technicianId).build());
        // This is the ONLY place that writes TechnicianAssignment rows — Booking module calls this
        // instead of touching TechnicianAssignmentRepository itself (A.2.4).
        throw new UnsupportedOperationException("Milestone 2");
    }

    @Override
    public boolean areAllAssignmentsAccepted(String bookingId) {
        // TODO Milestone 2: List<TechnicianAssignment> rows = technicianAssignmentRepository
        //   .findByBookingId(bookingId); return !rows.isEmpty() && rows.stream()
        //   .allMatch(a -> "ACCEPTED".equals(a.getStatus()));
        throw new UnsupportedOperationException("Milestone 2");
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
        // TODO Milestone 2: upsert into TechnicianAvailabilityRepository by technicianId.
        throw new UnsupportedOperationException("Milestone 2");
    }

    @Override
    public List<TechnicianResponse> myBookings(String technicianId) {
        // TODO Milestone 2: technicianAssignmentRepository.findByTechnicianId(technicianId), then
        // resolve each bookingId via BookingExternalService.findById(...).
        throw new UnsupportedOperationException("Milestone 2");
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
