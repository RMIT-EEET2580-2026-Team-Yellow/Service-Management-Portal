// Path in repo: backend/src/main/java/com/fixinh/smt/modules/technician/service/external/TechnicianExternalService.java
// CHANGE: added createAssignments(...) and areAllAssignmentsAccepted(...) so Booking module can
// create/check TechnicianAssignment rows WITHOUT touching TechnicianAssignmentRepository
// directly (A.2.4 — modules may only depend on another module's External Service).
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

    /** Creates a PENDING TechnicianAssignment row for each technicianId against this booking
     *  (3.3.M3). Called by BookingServiceImpl — Booking must never write to
     *  TechnicianAssignmentRepository directly (A.2.4). */
    void createAssignments(String bookingId, List<String> technicianIds);

    /** True once every TechnicianAssignment for this booking has status ACCEPTED (3.3.M5) —
     *  Booking uses this to decide when to flip status to "Scheduled". */
    boolean areAllAssignmentsAccepted(String bookingId);

    /** Full names of technicians assigned to this booking, any assignment status (3.3.S4) —
     *  used by Booking's list endpoints. Booking must never read TechnicianAssignment directly (A.2.4). */
    List<String> findAssignedTechnicianNames(String bookingId);
}
