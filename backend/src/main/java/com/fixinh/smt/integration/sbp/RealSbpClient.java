
package com.fixinh.smt.integration.sbp;

import com.fixinh.smt.integration.sbp.dto.SbpCustomerDto;
import com.fixinh.smt.integration.sbp.dto.SbpRatingDto;
import com.fixinh.smt.integration.sbp.dto.SbpRatingStatsDto;
import com.fixinh.smt.modules.servicetype.dto.external.ServiceTypeRef;
import com.fixinh.smt.modules.servicetype.service.external.ServiceTypeExternalService;
import com.fixinh.smt.modules.technician.dto.external.TechnicianRef;
import com.fixinh.smt.modules.technician.service.external.TechnicianExternalService;
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
    private final TechnicianExternalService technicianExternalService;

    public RealSbpClient(@Qualifier("sbpRestClient") RestClient restClient, SbpServiceTokenProvider tokenProvider,
                          ServiceTypeExternalService serviceTypeExternalService,
                          TechnicianExternalService technicianExternalService) {
        this.restClient = restClient;
        this.tokenProvider = tokenProvider;
        this.serviceTypeExternalService = serviceTypeExternalService;
        this.technicianExternalService = technicianExternalService;
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

    /**
     * 3.4.U1 — SBP serves ONE service type per request, keyed by SBP's own service-type id
     * (SBP stores our id on every Rating row, per AD-6 — SBP has no service-type table of its
     * own). Sending the NAME instead (the original "Plan 2" agreed 15/09) turned out not to be
     * live on SBP's side yet: their endpoint still parses the path segment as a UUID, and a
     * name crashes it with an uncaught parse exception (500, not a clean 400). Using the id we
     * pinned to match SBP's registry (ServiceTypeSeeder / service_types.json) works today.
     *
     * <p>The id is what goes in the path; the NAME is what goes back as the display value,
     * because that is what the Statistics table renders — see {@code fetchStats}.</p>
     */
    @Override
    public List<SbpRatingStatsDto> getServiceTypeRatingStats(String period) {
        return serviceTypeExternalService.listRefs().stream()
                .map(ref -> fetchStats("/integration/ratings/statistics/service-types/{id}",
                        ref.id(), ref.name(), "SERVICE_TYPE", period))
                .flatMap(Optional::stream)
                .toList();
    }

    /**
     * 3.4.U2 — same fan-out, keyed by STAFF id.
     *
     * <p>{@link TechnicianRef} carries the staff id deliberately: the booking-event webhook
     * sends {@code Technician::getStaffId}, SBP persists that on the notification row, and it
     * aggregates ratings against it. Passing the Technician document id here would return a
     * clean 200 with zeroes in it — the worst kind of wrong.</p>
     */
    @Override
    public List<SbpRatingStatsDto> getTechnicianRatingStats(String period) {
        return technicianExternalService.listRefs().stream()
                .map(ref -> fetchStats("/integration/ratings/statistics/technicians/{id}",
                        ref.staffId(), ref.fullName(), "TECHNICIAN", period))
                .flatMap(Optional::stream)
                .toList();
    }

    /**
     * One statistics call, shared by both getServiceTypeRatingStats and getTechnicianRatingStats.
     * Deserializes into {@link RatingStatsWireDto} rather than straight into
     * {@link SbpRatingStatsDto} — SBP's JSON field is {@code averageOverallStars}, not
     * {@code averageOverall}, so mapping directly used to leave the headline number silently at
     * 0.0 while the three sub-scores (which happen to match by name) looked correct.
     *
     * <p>A subject with no ratings yet comes back as a zero aggregate rather than a 404, so an
     * empty Optional here means the subject id is genuinely unknown to SBP — usually an id from
     * the wrong namespace — and is dropped rather than shown as a misleading zero row.</p>
     */
    private Optional<SbpRatingStatsDto> fetchStats(String path, String subjectId, String displayName,
                                                    String subjectType, String period) {
        try {
            RatingStatsWireDto wire = authorized(restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(path)
                            .queryParam("period", period)
                            .build(subjectId)))
                    .retrieve()
                    .body(RatingStatsWireDto.class);

            if (wire == null) {
                return Optional.empty();
            }
            return Optional.of(new SbpRatingStatsDto(displayName, subjectType, wire.averageOverallStars(),
                    wire.averageIntegrity(), wire.averageQuality(), wire.averageFriendliness()));
        } catch (HttpClientErrorException.NotFound ex) {
            return Optional.empty();
        }
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

    /**
     * SBP's exact JSON for a rating aggregate. Field names matter: mapping straight into
     * {@link SbpRatingStatsDto} used to leave the headline number at 0.0 because SBP sends
     * {@code averageOverallStars}, not {@code averageOverall} — the three sub-scores matched by
     * name and looked fine, which is what made the bug easy to miss.
     */
    private record RatingStatsWireDto(String serviceTypeId, String technicianId, double averageOverallStars,
                                       long totalRatings, double averageIntegrity, double averageQuality,
                                       double averageFriendliness) {
    }

    private record BookingEventRequest(String bookingId, String customerId, String status, String serviceName,
                                        String technicianId, String technicianName, LocalDateTime scheduledAt) {
    }
}
