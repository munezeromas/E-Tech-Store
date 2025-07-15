package gencoders.e_tech_store_app.config;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * Health check for Cloudinary service
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CloudinaryHealthCheck implements HealthIndicator {

    private final Cloudinary cloudinary;

    @Override
    public Health health() {
        try {
            // Test Cloudinary connection
            cloudinary.api().ping(new HashMap<>());

            return Health.up()
                    .withDetail("cloudinary", "Connection successful")
                    .withDetail("status", "UP")
                    .build();
        } catch (Exception e) {
            log.error("Cloudinary health check failed", e);

            return Health.down()
                    .withDetail("cloudinary", "Connection failed")
                    .withDetail("error", e.getMessage())
                    .withDetail("status", "DOWN")
                    .build();
        }
    }
}