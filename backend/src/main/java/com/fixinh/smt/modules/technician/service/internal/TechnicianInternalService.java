package com.fixinh.smt.modules.technician.service.internal;

import com.fixinh.smt.modules.technician.dto.internal.TechnicianResponse;
import com.fixinh.smt.modules.technician.dto.internal.TechnicianScheduleRequest;
import com.fixinh.smt.modules.technician.dto.external.TechnicianExternalDto;
import com.fixinh.smt.modules.booking.dto.external.BookingExternalDto;
import com.fixinh.smt.modules.technician.dto.internal.AvailabilityPeriodRequest;
import com.fixinh.smt.modules.technician.dto.internal.AvailabilityPeriodResponse;
import com.fixinh.smt.modules.technician.dto.internal.ScheduleChangeRequestDto;
import com.fixinh.smt.modules.technician.dto.internal.ScheduleChangeResponse;
import com.fixinh.smt.modules.technician.dto.internal.TechnicianBookingResponse;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;

/** Internal Interface (A.3.1/A.3.2) — consumed only by {@code TechnicianController}. */
public interface TechnicianInternalService {

    TechnicianResponse setSchedule(TechnicianScheduleRequest request);

    TechnicianResponse getByStaffId(String staffId);

    List<TechnicianBookingResponse> myBookings(String technicianId);

    boolean acceptAssignment(String technicianId, String bookingId);

    boolean rejectAssignment(String technicianId, String bookingId);

    BookingExternalDto startWork(String technicianId, String bookingId);

    BookingExternalDto complete(String technicianId, String bookingId, double serviceFee,
                                 double materialCost, String signedBillImageUrl);

    BookingExternalDto abort(String technicianId, String bookingId, String reason);

    List<TechnicianExternalDto> findAvailableTechnicians(
        LocalDate date, LocalTime start, LocalTime end, int durationMinutes
    );

    // Periods
    AvailabilityPeriodResponse createAvailabilityPeriod(
        String technicianId,
        AvailabilityPeriodRequest request
    );

    List<AvailabilityPeriodResponse> listAvailabilityPeriods(
        String technicianId,
        LocalDate from,
        LocalDate to
    );

    void deleteAvailabilityPeriod(
        String technicianId,
        String periodId
    );

    // Schedule change requests (Technician proposes, Admin approves/rejects)
    ScheduleChangeResponse submitScheduleChangeRequest(
        String technicianId,
        ScheduleChangeRequestDto request
    );

    List<ScheduleChangeResponse> listScheduleChangeRequests(String technicianId);

    List<ScheduleChangeResponse> listPendingScheduleChangeRequests();

    ScheduleChangeResponse approveScheduleChangeRequest(String requestId);

    ScheduleChangeResponse rejectScheduleChangeRequest(String requestId, String reason);
}
