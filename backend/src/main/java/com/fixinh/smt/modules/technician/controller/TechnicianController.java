package com.fixinh.smt.modules.technician.controller;

import com.fixinh.smt.common.web.ApiResponse;
import com.fixinh.smt.modules.booking.dto.external.BookingExternalDto;
import com.fixinh.smt.modules.technician.dto.internal.TechnicianResponse;
import com.fixinh.smt.modules.technician.dto.internal.TechnicianScheduleRequest;
import com.fixinh.smt.modules.technician.dto.external.TechnicianExternalDto;
import com.fixinh.smt.modules.technician.dto.internal.AvailabilityPeriodRequest;
import com.fixinh.smt.modules.technician.dto.internal.AvailabilityPeriodResponse;
import com.fixinh.smt.modules.technician.dto.internal.ScheduleChangeRequestDto;
import com.fixinh.smt.modules.technician.dto.internal.ScheduleChangeResponse;
import com.fixinh.smt.modules.technician.service.internal.TechnicianInternalService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.time.LocalDate;

/**
 * ADMIN can reach any technician's data; a TECHNICIAN may only reach their own (A.2.a) — enforced
 * by comparing the JWT's subject ({@code authentication.name}, set in JwtFilter) against the
 * {@code technicianId} path variable (or body field, for {@code setSchedule}).
 */
@RestController
@RequestMapping("/api/v1/technicians")
public class TechnicianController {

    private static final String OWN_TECHNICIAN =
            "hasRole('ADMIN') or (hasRole('TECHNICIAN') and authentication.name == #technicianId)";

    private final TechnicianInternalService technicianInternalService;

    public TechnicianController(TechnicianInternalService technicianInternalService) {
        this.technicianInternalService = technicianInternalService;
    }

    @PostMapping("/schedule")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('TECHNICIAN') and authentication.name == #request.staffId)")
    public ApiResponse<TechnicianResponse> setSchedule(@Valid @RequestBody TechnicianScheduleRequest request) {
        return ApiResponse.ok(technicianInternalService.setSchedule(request));
    }

    @GetMapping("/by-staff/{staffId}")
    public ApiResponse<TechnicianResponse> getByStaffId(@PathVariable String staffId) {
        return ApiResponse.ok(technicianInternalService.getByStaffId(staffId));
    }

    @GetMapping("/{technicianId}/bookings")
    @PreAuthorize(OWN_TECHNICIAN)
    public ApiResponse<List<TechnicianResponse>> myBookings(@PathVariable String technicianId) {
    public ApiResponse<List<BookingExternalDto>> myBookings(@PathVariable String technicianId) {
        return ApiResponse.ok(technicianInternalService.myBookings(technicianId));
    }

    @PatchMapping("/{technicianId}/bookings/{bookingId}/start")
    @PreAuthorize(OWN_TECHNICIAN)
    public ApiResponse<TechnicianResponse> startWork(@PathVariable String technicianId, @PathVariable String bookingId) {
        return ApiResponse.ok(technicianInternalService.startWork(technicianId, bookingId));
    }

    @PatchMapping("/{technicianId}/bookings/{bookingId}/complete")
    @PreAuthorize(OWN_TECHNICIAN)
    public ApiResponse<TechnicianResponse> complete(@PathVariable String technicianId, @PathVariable String bookingId,
                                                      @RequestParam double serviceFee, @RequestParam double materialCost,
                                                      @RequestParam String signedBillImageUrl) {
        return ApiResponse.ok(technicianInternalService.complete(technicianId, bookingId, serviceFee, materialCost, signedBillImageUrl));
    }

    @PatchMapping("/{technicianId}/bookings/{bookingId}/abort")
    @PreAuthorize(OWN_TECHNICIAN)
    public ApiResponse<TechnicianResponse> abort(@PathVariable String technicianId, @PathVariable String bookingId,
                                                   @RequestParam String reason) {
        return ApiResponse.ok(technicianInternalService.abort(technicianId, bookingId, reason));
    }

    @GetMapping("/available")
    public ApiResponse<List<TechnicianExternalDto>> findAvailable(
            @RequestParam java.time.LocalDate date,
            @RequestParam java.time.LocalTime start,
            @RequestParam java.time.LocalTime end,
            @RequestParam int durationMinutes) {
        return ApiResponse.ok(technicianInternalService.findAvailableTechnicians(date, start, end, durationMinutes));
    }

    // Periods
    @PostMapping("/{technicianId}/availability-periods")
    @PreAuthorize(OWN_TECHNICIAN)
    public ApiResponse<AvailabilityPeriodResponse> createAvailabilityPeriod(
        @PathVariable String technicianId,
        @Valid @RequestBody AvailabilityPeriodRequest request
    ) {
        return ApiResponse.ok(
            technicianInternalService.createAvailabilityPeriod(
                    technicianId,
                    request
            )
        );
    }

    @GetMapping("/{technicianId}/availability-periods")
    @PreAuthorize(OWN_TECHNICIAN)
    public ApiResponse<List<AvailabilityPeriodResponse>> listAvailabilityPeriods(
        @PathVariable String technicianId,
        @RequestParam(required = false) LocalDate from,
        @RequestParam(required = false) LocalDate to
    ) {
        return ApiResponse.ok(
            technicianInternalService.listAvailabilityPeriods(
                technicianId,
                from,
                to
            )
        );
    }

    @DeleteMapping("/{technicianId}/availability-periods/{periodId}")
    @PreAuthorize(OWN_TECHNICIAN)
    public ApiResponse<Void> deleteAvailabilityPeriod(
        @PathVariable String technicianId,
        @PathVariable String periodId
    ) {
        technicianInternalService.deleteAvailabilityPeriod(
            technicianId,
            periodId
        );

        return ApiResponse.ok(null);
    }

    // Schedule change requests (Technician proposes, Admin approves/rejects)
    @PostMapping("/{technicianId}/schedule-requests")
    @PreAuthorize(OWN_TECHNICIAN)
    public ApiResponse<ScheduleChangeResponse> submitScheduleChangeRequest(
        @PathVariable String technicianId,
        @Valid @RequestBody ScheduleChangeRequestDto request
    ) {
        return ApiResponse.ok(
            technicianInternalService.submitScheduleChangeRequest(technicianId, request)
        );
    }

    @GetMapping("/{technicianId}/schedule-requests")
    @PreAuthorize(OWN_TECHNICIAN)
    public ApiResponse<List<ScheduleChangeResponse>> listScheduleChangeRequests(
        @PathVariable String technicianId
    ) {
        return ApiResponse.ok(
            technicianInternalService.listScheduleChangeRequests(technicianId)
        );
    }

    @GetMapping("/schedule-requests")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<ScheduleChangeResponse>> listPendingScheduleChangeRequests() {
        return ApiResponse.ok(technicianInternalService.listPendingScheduleChangeRequests());
    }

    @PatchMapping("/schedule-requests/{requestId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ScheduleChangeResponse> approveScheduleChangeRequest(
        @PathVariable String requestId
    ) {
        return ApiResponse.ok(technicianInternalService.approveScheduleChangeRequest(requestId));
    }

    @PatchMapping("/schedule-requests/{requestId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ScheduleChangeResponse> rejectScheduleChangeRequest(
        @PathVariable String requestId,
        @RequestParam(required = false) String reason
    ) {
        return ApiResponse.ok(technicianInternalService.rejectScheduleChangeRequest(requestId, reason));
    }
}
