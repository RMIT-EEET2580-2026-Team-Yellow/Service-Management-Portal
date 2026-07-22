package com.fixinh.smt.modules.technician.service.external;

import com.fixinh.smt.modules.technician.dto.external.TechnicianExternalDto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/** External Interface (A.3.1/A.3.2) — the ONLY way the Booking module reaches Technician data. */
public interface TechnicianExternalService {

    List<TechnicianExternalDto> findAvailable(LocalDate date, LocalTime start, LocalTime end, int durationMinutes);

    boolean acceptAssignment(String technicianId, String bookingId);

    boolean rejectAssignment(String technicianId, String bookingId);
}
