package com.fixinh.smt.modules.technician.service.internal;

import com.fixinh.smt.modules.technician.dto.internal.TechnicianResponse;
import com.fixinh.smt.modules.technician.dto.internal.TechnicianScheduleRequest;
import com.fixinh.smt.modules.technician.dto.external.TechnicianExternalDto;
import com.fixinh.smt.modules.booking.dto.external.BookingExternalDto;
import com.fixinh.smt.modules.technician.dto.internal.AvailabilityPeriodRequest;
import com.fixinh.smt.modules.technician.dto.internal.AvailabilityPeriodResponse;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;

/** Internal Interface (A.3.1/A.3.2) — consumed only by {@code TechnicianController}. */
public interface TechnicianInternalService {

    TechnicianResponse setSchedule(TechnicianScheduleRequest request);

    List<BookingExternalDto> myBookings(String technicianId);

    TechnicianResponse startWork(String technicianId, String bookingId);

    TechnicianResponse complete(String technicianId, String bookingId, double serviceFee,
                                 double materialCost, String signedBillImageUrl);

    TechnicianResponse abort(String technicianId, String bookingId, String reason);

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
}
