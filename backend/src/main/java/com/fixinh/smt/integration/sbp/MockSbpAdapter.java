package com.fixinh.smt.integration.sbp;

import com.fixinh.smt.integration.sbp.dto.SbpCustomerDto;
import com.fixinh.smt.integration.sbp.dto.SbpRatingDto;
import com.fixinh.smt.integration.sbp.dto.SbpRatingStatsDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/** Mock Rating/Statistics/Customer Data API (4.S2) used for Milestone 1 until SBP is available. */
@Component
public class MockSbpAdapter implements SbpIntegrationPort {

    @Override
    public Optional<SbpCustomerDto> getCustomer(Long customerId) {
        return Optional.of(new SbpCustomerDto(customerId, "Mock Customer", "0900000000"));
    }

    @Override
    public Optional<SbpRatingDto> getRatingForBooking(Long bookingId) {
        return Optional.of(new SbpRatingDto(bookingId, 5, 5, 5, 5, "Great service!", List.of()));
    }

    @Override
    public List<SbpRatingStatsDto> getServiceTypeRatingStats(String period) {
        return List.of(new SbpRatingStatsDto("PLUMBING", "SERVICE_TYPE", 4.5, 4.5, 4.5, 4.5));
    }

    @Override
    public List<SbpRatingStatsDto> getTechnicianRatingStats(String period) {
        return List.of(new SbpRatingStatsDto("TECH-001", "TECHNICIAN", 4.7, 4.7, 4.7, 4.7));
    }
}
