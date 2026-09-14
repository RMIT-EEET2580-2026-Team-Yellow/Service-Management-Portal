// Path in repo: backend/src/main/java/com/fixinh/smt/integration/sbp/RealSbpClient.java
// CHANGE: implemented against SBT's published "SBP API Reference" (Integration section, 2026-09-10):
//   - getCustomer / getRatingForBooking map field-by-field against SBT's documented response shape,
//     which does not match our SbpCustomerDto/SbpRatingDto field names 1:1 (see private wire DTOs below).
//   - notifyBookingEvent posts to /integration/bookings/events (the one endpoint SBT marked "live").
//   - getServiceTypeRatingStats/getTechnicianRatingStats are NOT implemented: SBT's endpoints take a
//     single {id} path segment and return ONE stats object, but SbpIntegrationPort's methods take no
//     id and return a List — SMT has no ServiceType registry to enumerate ids from either. This is a
//     real contract mismatch to resolve with SBT, not something to guess at silently.
package com.fixinh.smt.integration.sbp;

import com.fixinh.smt.integration.sbp.dto.SbpCustomerDto;
import com.fixinh.smt.integration.sbp.dto.SbpRatingDto;
import com.fixinh.smt.integration.sbp.dto.SbpRatingStatsDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Calls SBP's real HTTPS REST API (4.M1). Activated once smt.integration.sbp.use-mock=false. */
@Component
@ConditionalOnProperty(prefix = "smt.integration.sbp", name = "use-mock", havingValue = "false")
public class RealSbpClient implements SbpIntegrationPort {

    private final RestClient restClient;
    private final SbpServiceTokenProvider tokenProvider;

    public RealSbpClient(@Qualifier("sbpRestClient") RestClient restClient, SbpServiceTokenProvider tokenProvider) {
        this.restClient = restClient;
        this.tokenProvider = tokenProvider;
    }

    private RestClient.RequestHeadersSpec<?> authorized(RestClient.RequestHeadersSpec<?> spec) {
        return spec.header("Authorization", "Bearer " + tokenProvider.getToken());
    }

    @Override
    public Optional<SbpCustomerDto> getCustomer(String customerId) {
        try {
            CustomerWireDto wire = authorized(restClient.get()
                    .uri("/integration/customers/{customerId}", customerId))
                    .retrieve()
                    .body(CustomerWireDto.class);

            if (wire == null) {
                return Optional.empty();
            }
            return Optional.of(new SbpCustomerDto(wire.customerId(), wire.name(), wire.phoneNumber()));
        } catch (HttpClientErrorException.NotFound ex) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<SbpRatingDto> getRatingForBooking(String bookingId) {
        try {
            RatingWireDto wire = authorized(restClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/integration/ratings").queryParam("bookingId", bookingId).build()))
                    .retrieve()
                    .body(RatingWireDto.class);

            if (wire == null) {
                return Optional.empty();
            }
            return Optional.of(new SbpRatingDto(wire.bookingId(), wire.overallStars(), wire.integrityScore(),
                    wire.qualityScore(), wire.friendlinessScore(), wire.textReview(), wire.imageUrls()));
        } catch (HttpClientErrorException.NotFound ex) {
            return Optional.empty();
        }
    }

    @Override
    public List<SbpRatingStatsDto> getServiceTypeRatingStats(String period) {
        throw new UnsupportedOperationException(
                "SBP's GET /integration/ratings/statistics/service-types/{id} takes a single service-type "
                        + "id and returns one object, but SMT has no ServiceType registry to enumerate ids "
                        + "from, and this method's signature (period only, returns a List) predates that "
                        + "contract. Needs clarification with the SBT team before this can be implemented.");
    }

    @Override
    public List<SbpRatingStatsDto> getTechnicianRatingStats(String period) {
        throw new UnsupportedOperationException(
                "SBP's GET /integration/ratings/statistics/technicians/{id} takes a single technicianId "
                        + "and returns one object, but this method's signature (period only, returns a "
                        + "List) predates that contract. Needs clarification with the SBT team before this "
                        + "can be implemented.");
    }

    @Override
    public void notifyBookingEvent(String bookingId, String customerId, String status, String serviceName,
                                    String technicianId, String technicianName, LocalDateTime scheduledAt) {
        BookingEventRequest body = new BookingEventRequest(
                bookingId, customerId, status, serviceName, technicianId, technicianName, scheduledAt);

        authorized(restClient.post()
                .uri("/integration/bookings/events")
                .body(body))
                .retrieve()
                .toBodilessEntity();
    }

    // ---- Wire-format DTOs (SBT's exact JSON shape) - kept private, mapped into SMT's own DTOs
    //      above so the rest of the codebase never depends on SBT's field naming directly. ----

    private record CustomerWireDto(String customerId, String name, String email, String phoneNumber, boolean active) {
    }

    private record RatingWireDto(String ratingId, String bookingId, String customerId, String serviceTypeId,
                                  int overallStars, int integrityScore, int qualityScore, int friendlinessScore,
                                  String textReview, List<String> imageUrls, String createdAt) {
    }

    private record BookingEventRequest(String bookingId, String customerId, String status, String serviceName,
                                        String technicianId, String technicianName, LocalDateTime scheduledAt) {
    }
}
