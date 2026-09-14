
package com.fixinh.smt.integration.sbp;

import com.fixinh.smt.integration.sbp.dto.SbpCustomerDto;
import com.fixinh.smt.integration.sbp.dto.SbpRatingDto;
import com.fixinh.smt.integration.sbp.dto.SbpRatingStatsDto;
import com.fixinh.smt.modules.servicetype.service.external.ServiceTypeExternalService;
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
    private final ServiceTypeExternalService serviceTypeExternalService;

    public RealSbpClient(@Qualifier("sbpRestClient") RestClient restClient, SbpServiceTokenProvider tokenProvider,
                          ServiceTypeExternalService serviceTypeExternalService) {
        this.restClient = restClient;
        this.tokenProvider = tokenProvider;
        this.serviceTypeExternalService = serviceTypeExternalService;
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
        return serviceTypeExternalService.listNames().stream()
                .map(name -> fetchServiceTypeStats(name, period))
                .flatMap(Optional::stream)
                .toList();
    }

    private Optional<SbpRatingStatsDto> fetchServiceTypeStats(String serviceTypeName, String period) {
        try {
            SbpRatingStatsDto stats = authorized(restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/integration/ratings/statistics/service-types/{id}")
                            .queryParam("period", period)
                            .build(serviceTypeName)))
                    .retrieve()
                    .body(SbpRatingStatsDto.class);

            if (stats == null) {
                return Optional.empty();
            }
            return Optional.of(new SbpRatingStatsDto(serviceTypeName, stats.subjectType(), stats.averageOverall(),
                    stats.averageIntegrity(), stats.averageQuality(), stats.averageFriendliness()));
        } catch (HttpClientErrorException.NotFound ex) {
            return Optional.empty();
        }
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
