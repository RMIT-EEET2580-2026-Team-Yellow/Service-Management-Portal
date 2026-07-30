// Path in repo: backend/src/main/java/com/fixinh/smt/modules/technician/service/TechnicianServiceImpl.java
// CHANGE: implements the 2 new TechnicianExternalService methods (createAssignments,
// areAllAssignmentsAccepted) so TechnicianAssignmentRepository access stays fully inside this
// module (A.2.4) — BookingServiceImpl no longer injects that repository directly.
package com.fixinh.smt.modules.technician.service;

import com.fixinh.smt.modules.technician.dto.external.TechnicianExternalDto;
import com.fixinh.smt.modules.technician.dto.internal.TechnicianResponse;
import com.fixinh.smt.modules.technician.dto.internal.TechnicianScheduleRequest;
import com.fixinh.smt.modules.technician.entity.TechnicianAssignment;
import com.fixinh.smt.modules.technician.repository.TechnicianAssignmentRepository;
import com.fixinh.smt.modules.technician.repository.TechnicianAvailabilityRepository;
import com.fixinh.smt.modules.technician.repository.TechnicianRepository;
import com.fixinh.smt.modules.technician.service.external.TechnicianExternalService;
import com.fixinh.smt.modules.technician.service.internal.TechnicianInternalService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class TechnicianServiceImpl implements TechnicianExternalService, TechnicianInternalService {

    private final TechnicianRepository technicianRepository;
    private final TechnicianAvailabilityRepository technicianAvailabilityRepository;
    private final TechnicianAssignmentRepository technicianAssignmentRepository;

    public TechnicianServiceImpl(TechnicianRepository technicianRepository,
                                  TechnicianAvailabilityRepository technicianAvailabilityRepository,
                                  TechnicianAssignmentRepository technicianAssignmentRepository) {
        this.technicianRepository = technicianRepository;
        this.technicianAvailabilityRepository = technicianAvailabilityRepository;
        this.technicianAssignmentRepository = technicianAssignmentRepository;
    }

    // ---- TechnicianExternalService (consumed by Booking module, 3.3.M3/M4) ----

    @Override
    public List<TechnicianExternalDto> findAvailable(LocalDate date, LocalTime start, LocalTime end, int durationMinutes) {
        // TODO Milestone 2: query TechnicianAvailabilityRepository for matching working days/hours,
        // then filter out technicians with an ACCEPTED/PENDING TechnicianAssignment that overlaps.
        throw new UnsupportedOperationException("Milestone 2");
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
