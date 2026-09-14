package com.fixinh.smt.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/** Shared HTTP client for the outbound SBP integration (4.M1-M3) — used by both
 *  {@code RealSbpClient} (calls SBP's data/webhook endpoints) and
 *  {@code SbpServiceTokenProvider} (calls SBP's /auth/service-token exchange). */
@Configuration
public class SbpClientConfig {

    @Bean("sbpRestClient")
    public RestClient sbpRestClient(@Value("${smt.integration.sbp.base-url}") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}
