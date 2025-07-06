package gencoders.e_tech_store_app.config;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.SecretKey;
import java.util.Base64;

@Configuration
public class JwtConfig {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private int jwtExpirationMs;

    @Value("${app.jwt.issuer}")
    private String jwtIssuer;

    @Bean
    public SecretKey secretKey() {
        if (jwtSecret == null || jwtSecret.length() < 32) {
            return Keys.secretKeyFor(SignatureAlgorithm.HS512);
        }
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret));
    }

    @Bean
    public int jwtExpirationMs() {
        return jwtExpirationMs;
    }

    @Bean
    public String jwtIssuer() {
        return jwtIssuer;
    }

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
}