package com.fixinh.smt.integration.sbp;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Instant;

/**
 * Fetches and caches the SERVICE JWT SMT needs to call SBP's {@code /integration/*} endpoints
 * (4.M2) — client-credentials exchange against SBP's {@code POST /auth/service-token}. This is
 * the OUTBOUND half of the SERVICE-token handshake: it is separate from
 * {@code AuthServiceImpl.issueServiceToken}, which is the INBOUND half (SBP calling into SMT).
 *
 * clientId/clientSecret must be provisioned once between the two teams out-of-band (per
 * "smt.service.auth-secret" convention) and never committed for real use.
 */
@Component
public class SbpServiceTokenProvider {

    private final RestClient restClient;
    private final String clientId;
    private final String clientSecret;

    private volatile String cachedToken;
    private volatile Instant expiresAt = Instant.EPOCH;

    public SbpServiceTokenProvider(
            @Qualifier("sbpRestClient") RestClient restClient,
            @Value("${smt.integration.sbp.client-id:CHANGE_ME_provisioned_with_SBT}") String clientId,
            @Value("${smt.integration.sbp.client-secret:CHANGE_ME_provisioned_with_SBT}") String clientSecret) {
        this.restClient = restClient;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    /** Returns a valid Bearer token, fetching a new one if missing or within 60s of expiry. */
    public synchronized String getToken() {
        if (cachedToken == null || Instant.now().isAfter(expiresAt.minusSeconds(60))) {
            refresh();
        }
        return cachedToken;
    }

    private void refresh() {
        ServiceTokenResponse response = restClient.post()
                .uri("/auth/service-token")
                .body(new ServiceTokenRequest(clientId, clientSecret))
                .retrieve()
                .body(ServiceTokenResponse.class);

        if (response == null || response.accessToken() == null) {
            throw new IllegalStateException("SBP did not return an access token from /auth/service-token");
        }

        cachedToken = response.accessToken();
        int expiresInMinutes = response.expiresInMinutes() != null ? response.expiresInMinutes() : 60;
        expiresAt = Instant.now().plusSeconds(expiresInMinutes * 60L);
    }

    private record ServiceTokenRequest(String clientId, String clientSecret) {
    }

    private record ServiceTokenResponse(String accessToken, Integer expiresInMinutes) {
    }
}
