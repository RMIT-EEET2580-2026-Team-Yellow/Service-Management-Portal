// Path in repo: backend/src/main/java/com/fixinh/smt/integration/sbp/RealSbpClient.java
// CHANGE: getCustomer/getRatingForBooking params Long -> String, matching SbpIntegrationPort.
package com.fixinh.smt.integration.sbp;

import com.fixinh.smt.integration.sbp.dto.SbpCustomerDto;
import com.fixinh.smt.integration.sbp.dto.SbpRatingDto;
import com.fixinh.smt.integration.sbp.dto.SbpRatingStatsDto;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

/** Calls SBP's real HTTPS REST API (4.M1). Activated once SBP publishes its endpoints. */
public class RealSbpClient implements SbpIntegrationPort {

    private final RestClient restClient;

    public RealSbpClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public Optional<SbpCustomerDto> getCustomer(String customerId) {
        throw new UnsupportedOperationException("Milestone 2");
    }

    @Override
    public Optional<SbpRatingDto> getRatingForBooking(String bookingId) {
        throw new UnsupportedOperationException("Milestone 2");
    }

    @Override
    public List<SbpRatingStatsDto> getServiceTypeRatingStats(String period) {
        throw new UnsupportedOperationException("Milestone 2");
    }

    @Override
    public List<SbpRatingStatsDto> getTechnicianRatingStats(String period) {
        throw new UnsupportedOperationException("Milestone 2");
    }
}
