// Path in repo: backend/src/main/java/com/fixinh/smt/integration/sbp/SbpIntegrationPort.java
// CHANGES:
//   - getCustomer(Long) -> getCustomer(String): customerId is now UUID, per 30/7 agreement.
//   - getRatingForBooking(Long) -> getRatingForBooking(String): this was ALREADY inconsistent
//     even before the UUID change — Booking.id has always been a String, so passing a Long
//     bookingId here could never have matched a real booking. Fixed while touching this file.
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

    Optional<SbpCustomerDto> getCustomer(String customerId);

    Optional<SbpRatingDto> getRatingForBooking(String bookingId);

    List<SbpRatingStatsDto> getServiceTypeRatingStats(String period);

    List<SbpRatingStatsDto> getTechnicianRatingStats(String period);
}
