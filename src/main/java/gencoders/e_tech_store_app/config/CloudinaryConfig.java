package gencoders.e_tech_store_app.config;

import com.cloudinary.Cloudinary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class CloudinaryConfig {

    @Value("${cloudinary.cloud-name:}")
    private String cloudName;

    @Value("${cloudinary.api-key:}")
    private String apiKey;

    @Value("${cloudinary.api-secret:}")
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary() {
        log.info("Initializing Cloudinary configuration...");

        // Validate required properties
        validateCloudinaryProperties();

        // Create configuration map
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudName.trim());
        config.put("api_key", apiKey.trim());
        config.put("api_secret", apiSecret.trim());
        config.put("secure", "true");

        log.info("Cloudinary configured with cloud_name: {}", cloudName);

        return new Cloudinary(config);
    }

    private void validateCloudinaryProperties() {
        if (cloudName == null || cloudName.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Cloudinary cloud name is required. Please set 'cloudinary.cloud-name' in your application properties."
            );
        }

        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Cloudinary API key is required. Please set 'cloudinary.api-key' in your application properties."
            );
        }

        if (apiSecret == null || apiSecret.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Cloudinary API secret is required. Please set 'cloudinary.api-secret' in your application properties."
            );
        }

        log.info("All Cloudinary properties validated successfully");
    }
}