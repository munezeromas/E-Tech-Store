package gencoders.e_tech_store_app.payment.Mtn;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.time.Instant;
import java.util.Base64;
import java.util.Map;

@Slf4j
@Service
public class MomoAuthService {

    @Value("${momo.api.key}")
    private String apiKey;

    @Value("${momo.api.user}")
    private String apiUser;

    @Value("${momo.api.secret}")
    private String apiSecret;

    @Value("${momo.base-url}")
    private String baseUrl;

    private String accessToken;
    private Instant tokenExpiry;
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Gets valid access token, refreshes if expired
     */
    public String getAccessToken() {
        if (accessToken == null || Instant.now().isAfter(tokenExpiry)) {
            refreshToken();
        }
        return accessToken;
    }

    /**
     * Refreshes the access token from MTN MoMo API
     */
    private void refreshToken() {
        try {
            log.info("Refreshing MTN MoMo access token...");

            String authString = apiUser + ":" + apiSecret;
            String base64Auth = Base64.getEncoder().encodeToString(authString.getBytes());

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + base64Auth);
            headers.set("Ocp-Apim-Subscription-Key", apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/collection/token/",
                    HttpMethod.POST,
                    new HttpEntity<>(headers),
                    Map.class
            );

            if (response.getBody() != null && response.getBody().containsKey("access_token")) {
                this.accessToken = response.getBody().get("access_token").toString();
                this.tokenExpiry = Instant.now().plusSeconds(3600); // 1 hour expiry
                log.info("MTN MoMo token refreshed successfully");
            } else {
                throw new RuntimeException("Invalid response from MTN MoMo token endpoint");
            }

        } catch (HttpClientErrorException e) {
            log.error("Failed to refresh MTN MoMo token: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to authenticate with MTN MoMo API: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error during token refresh: {}", e.getMessage());
            throw new RuntimeException("Failed to authenticate with MTN MoMo API", e);
        }
    }

    /**
     * Validates if current token is still valid
     */
    public boolean isTokenValid() {
        return accessToken != null && Instant.now().isBefore(tokenExpiry);
    }

    /**
     * Forces token refresh
     */
    public void forceTokenRefresh() {
        this.accessToken = null;
        this.tokenExpiry = null;
        getAccessToken();
    }
}