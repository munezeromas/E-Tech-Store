package gencoders.e_tech_store_app.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private int jwtExpirationMs;

    /* ---------- key helper ---------- */

    private SecretKey key() {
        // needs javax.crypto.SecretKey for 0.12.x verifyWith(..)
        byte[] decoded = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(decoded);          // HS‑family key
    }

    /* ---------- token builders ---------- */

    public String generateToken(UserDetails user) {
        Map<String, Object> claims = new HashMap<>();

        Collection<? extends GrantedAuthority> roles = user.getAuthorities();
        if (roles != null && !roles.isEmpty()) {
            claims.put("roles",
                    roles.stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList()));
        }

        return Jwts.builder()
                .claims(claims)
                .subject(user.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key())                      // algorithm auto‑selected from key
                .compact();
    }

    public String generatePasswordResetToken(String username) {
        return Jwts.builder()
                .claim("reset", true)
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000))
                .signWith(key())
                .compact();
    }

    /* ---------- parsing / validation ---------- */

    private Claims parse(String token) {
        return Jwts.parser()                         // 0.12.x
                .verifyWith(key())                   // supply SecretKey to verify sig
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUserNameFromJwtToken(String token) {
        return parse(token).getSubject();
    }

    public boolean isResetTokenValid(String token) {
        try {
            return Boolean.TRUE.equals(parse(token).get("reset", Boolean.class));
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("Reset token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public boolean validateJwtToken(String token) {
        try {
            parse(token);           // will throw if invalid
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    /* ---------- convenience for Spring Security ---------- */

    public String generateJwtToken(Authentication auth) {
        return generateToken((UserDetails) auth.getPrincipal());
    }

    public String extractUsername(String token) {
        return getUserNameFromJwtToken(token);
    }

    public boolean validateToken(String token) {
        return validateJwtToken(token);
    }
}
