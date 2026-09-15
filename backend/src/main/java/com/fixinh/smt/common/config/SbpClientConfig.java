package com.fixinh.smt.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.cert.X509Certificate;

/** Shared HTTP client for the outbound SBP integration (4.M1-M3) — used by both
 *  {@code RealSbpClient} (calls SBP's data/webhook endpoints) and
 *  {@code SbpServiceTokenProvider} (calls SBP's /auth/service-token exchange).
 *
 *  <p>SBT told us (15/09, not yet pushed to their repo at the time of writing) that their local
 *  dev stack now runs HTTPS with a self-signed certificate, so this client needs to be able to
 *  accept it or every call fails with a PKIX validation error. Because
 *  {@code BookingServiceImpl.notifySbp} catches and logs, that failure is silent: the booking
 *  status still changes, nothing appears on screen, and no notification reaches the customer.</p>
 *
 *  <p>The trust override is deliberately scoped to THIS bean rather than installed as a global
 *  {@code RestClientCustomizer}: it applies only to calls to SBP, and every other outbound
 *  request in the application keeps normal certificate validation. It is also behind a flag that
 *  defaults to false, so it cannot be switched on by accident outside local development — flip it
 *  (and smt.integration.sbp.base-url to https://) only once SBT's HTTPS setup is actually
 *  confirmed live on whatever host you're pointing at, not just claimed. </p>
 */
@Configuration
public class SbpClientConfig {

    @Bean("sbpRestClient")
    public RestClient sbpRestClient(
            @Value("${smt.integration.sbp.base-url}") String baseUrl,
            @Value("${smt.integration.sbp.trust-self-signed:false}") boolean trustSelfSigned) {

        RestClient.Builder builder = RestClient.builder().baseUrl(baseUrl);
        if (!trustSelfSigned) {
            return builder.build();
        }
        return builder.requestFactory(selfSignedTolerantFactory()).build();
    }

    private static SimpleClientHttpRequestFactory selfSignedTolerantFactory() {
        try {
            TrustManager[] trustSbpsSelfSignedCert = { new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                public void checkClientTrusted(X509Certificate[] chain, String authType) { }
                public void checkServerTrusted(X509Certificate[] chain, String authType) { }
            }};
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustSbpsSelfSignedCert, new java.security.SecureRandom());

            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory() {
                @Override
                protected void prepareConnection(HttpURLConnection connection, String httpMethod)
                        throws IOException {
                    if (connection instanceof HttpsURLConnection https) {
                        https.setSSLSocketFactory(sslContext.getSocketFactory());
                        // SBP's certificate carries localhost, backend and host.docker.internal in
                        // its SAN list, so this is belt-and-braces rather than load-bearing.
                        https.setHostnameVerifier((hostname, session) -> true);
                    }
                    super.prepareConnection(connection, httpMethod);
                }
            };
            factory.setConnectTimeout(5000);
            factory.setReadTimeout(10000);
            return factory;
        } catch (Exception ex) {
            throw new IllegalStateException("Could not build a TLS context for the SBP client", ex);
        }
    }
}
