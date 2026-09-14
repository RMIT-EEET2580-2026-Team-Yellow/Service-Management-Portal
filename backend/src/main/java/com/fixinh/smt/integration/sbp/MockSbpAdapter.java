// Path in repo: backend/src/main/java/com/fixinh/smt/integration/sbp/MockSbpAdapter.java
// CHANGE: getCustomer/getRatingForBooking params Long -> String, matching SbpIntegrationPort.
package com.fixinh.smt.integration.sbp;

import com.fixinh.smt.integration.sbp.dto.SbpCustomerDto;
import com.fixinh.smt.integration.sbp.dto.SbpRatingDto;
import com.fixinh.smt.integration.sbp.dto.SbpRatingStatsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Mock Rating/Statistics/Customer Data API (4.S2) used for Milestone 1 until SBP is available.
 *  Active by default (and whenever smt.integration.sbp.use-mock=true); see {@link RealSbpClient}
 *  for the counterpart activated when use-mock=false. */
@Component
@ConditionalOnProperty(prefix = "smt.integration.sbp", name = "use-mock", havingValue = "true", matchIfMissing = true)
public class MockSbpAdapter implements SbpIntegrationPort {

    private static final Logger log = LoggerFactory.getLogger(MockSbpAdapter.class);

    @Override
    public Optional<SbpCustomerDto> getCustomer(String customerId) {
        return Optional.of(new SbpCustomerDto(customerId, "Mock Customer", "0900000000"));
    }

    @Override
    public Optional<SbpRatingDto> getRatingForBooking(String bookingId) {
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

    @Override
    public void notifyBookingEvent(String bookingId, String customerId, String status, String serviceName,
                                    String technicianId, String technicianName, LocalDateTime scheduledAt) {
        log.info("[MockSbpAdapter] would notify SBP: booking={} customer={} status={} service={} "
                        + "technicianId={} technicianName={} scheduledAt={}",
                bookingId, customerId, status, serviceName, technicianId, technicianName, scheduledAt);
    }
}
