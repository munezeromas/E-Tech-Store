package gencoders.e_tech_store_app.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import gencoders.e_tech_store_app.user.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule()) // ✅ FIX FOR LocalDateTime
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) // optional: format as ISO-8601
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .addMixIn(User.class, UserMixin.class);
    }

    @JsonIgnoreProperties({"password", "roles"})
    private abstract class UserMixin {}
}
