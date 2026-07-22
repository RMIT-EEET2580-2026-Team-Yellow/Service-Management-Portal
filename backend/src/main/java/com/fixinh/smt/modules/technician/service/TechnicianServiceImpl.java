package com.fixinh.smt.modules.technician.service;

import com.fixinh.smt.modules.technician.dto.external.TechnicianExternalDto;
import com.fixinh.smt.modules.technician.dto.internal.TechnicianResponse;
import com.fixinh.smt.modules.technician.dto.internal.TechnicianScheduleRequest;
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

    public TechnicianServiceImpl(TechnicianRepository technicianRepository) {
        this.technicianRepository = technicianRepository;
    }

    // ---- TechnicianExternalService (consumed by Booking module, 3.3.M3/M4) ----

    @Override
    public List<TechnicianExternalDto> findAvailable(LocalDate date, LocalTime start, LocalTime end, int durationMinutes) {
        throw new UnsupportedOperationException("Milestone 2");
    }

    @Override
    public boolean acceptAssignment(String technicianId, String bookingId) {
        throw new UnsupportedOperationException("Milestone 2");
    }

    @Override
    public boolean rejectAssignment(String technicianId, String bookingId) {
        throw new UnsupportedOperationException("Milestone 2");
    }

    // ---- TechnicianInternalService (controller-only, 3.3.M6-M8) ----

    @Override
    public TechnicianResponse setSchedule(TechnicianScheduleRequest request) {
        throw new UnsupportedOperationException("Milestone 2");
    }

    @Override
    public List<TechnicianResponse> myBookings(String technicianId) {
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
