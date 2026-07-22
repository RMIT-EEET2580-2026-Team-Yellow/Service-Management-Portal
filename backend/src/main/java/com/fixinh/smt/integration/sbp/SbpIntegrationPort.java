package com.fixinh.smt.integration.sbp;

import com.fixinh.smt.integration.sbp.dto.SbpCustomerDto;
import com.fixinh.smt.integration.sbp.dto.SbpRatingDto;
import com.fixinh.smt.integration.sbp.dto.SbpRatingStatsDto;

import java.util.List;
import java.util.Optional;

/**
 * Outbound port to the Service Booking Portal (SBP). SMT must call SBP's Rating/Statistics/
 * Customer APIs rather than querying SBP's database directly (3.4.S1/S5). Two implementations:
 * {@link RealSbpClient} (calls the live SBP REST API) and {@link MockSbpAdapter} (4.S2, used
 * until SBP's real endpoints are available).
 */
public interface SbpIntegrationPort {

    Optional<SbpCustomerDto> getCustomer(Long customerId);

    Optional<SbpRatingDto> getRatingForBooking(Long bookingId);

    List<SbpRatingStatsDto> getServiceTypeRatingStats(String period);

    List<SbpRatingStatsDto> getTechnicianRatingStats(String period);
}
