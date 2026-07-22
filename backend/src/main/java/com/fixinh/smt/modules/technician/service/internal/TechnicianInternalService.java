package com.fixinh.smt.modules.technician.service.internal;

import com.fixinh.smt.modules.technician.dto.internal.TechnicianResponse;
import com.fixinh.smt.modules.technician.dto.internal.TechnicianScheduleRequest;

import java.util.List;

/** Internal Interface (A.3.1/A.3.2) — consumed only by {@code TechnicianController}. */
public interface TechnicianInternalService {

    TechnicianResponse setSchedule(TechnicianScheduleRequest request);

    List<TechnicianResponse> myBookings(String technicianId);

    TechnicianResponse startWork(String technicianId, String bookingId);

    TechnicianResponse complete(String technicianId, String bookingId, double serviceFee,
                                 double materialCost, String signedBillImageUrl);

    TechnicianResponse abort(String technicianId, String bookingId, String reason);
}
