package gencoders.e_tech_store_app.jwt;

import gencoders.e_tech_store_app.user.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private int jwtExpirationMs;

    private SecretKey key() {
        byte[] decoded = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(decoded);
    }

    public String generateToken(UserDetails user) {
        Map<String, Object> claims = new HashMap<>();
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        List<String> roles = new ArrayList<>();

        if (authorities != null && !authorities.isEmpty()) {
            roles = authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            claims.put("roles", roles);
        }

        boolean isAdmin = roles.contains("ROLE_ADMIN");
        boolean isUser = roles.contains("ROLE_USER");
        claims.put("isAdmin", isAdmin);
        claims.put("isUser", isUser);
        claims.put("userType", isAdmin ? "ADMIN" : "USER");
        claims.put("tokenGeneratedAt", System.currentTimeMillis());

        return Jwts.builder()
                .claims(claims)
                .subject(user.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key())
                .compact();
    }

    public String generatePasswordResetToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("reset", true);
        claims.put("tokenType", "PASSWORD_RESET");

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000))
                .signWith(key())
                .compact();
    }

    private Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUserNameFromJwtToken(String token) {
        return parse(token).getSubject();
    }

    public List<String> getRolesFromJwtToken(String token) {
        try {
            Claims claims = parse(token);
            @SuppressWarnings("unchecked")
            List<String> roles = claims.get("roles", List.class);
            return roles != null ? roles : new ArrayList<>();
        } catch (Exception e) {
            logger.error("Error extracting roles from JWT token: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean isAdminToken(String token) {
        try {
            Claims claims = parse(token);
            return Boolean.TRUE.equals(claims.get("isAdmin", Boolean.class));
        } catch (Exception e) {
            logger.error("Error checking admin status from JWT token: {}", e.getMessage());
            return false;
        }
    }

    public boolean isUserToken(String token) {
        try {
            Claims claims = parse(token);
            return Boolean.TRUE.equals(claims.get("isUser", Boolean.class));
        } catch (Exception e) {
            logger.error("Error checking user status from JWT token: {}", e.getMessage());
            return false;
        }
    }

    public String getUserTypeFromToken(String token) {
        try {
            Claims claims = parse(token);
            return claims.get("userType", String.class);
        } catch (Exception e) {
            logger.error("Error extracting user type from JWT token: {}", e.getMessage());
            return "UNKNOWN";
        }
    }

    public boolean hasRole(String token, String role) {
        try {
            List<String> roles = getRolesFromJwtToken(token);
            return roles.contains(role);
        } catch (Exception e) {
            logger.error("Error checking role from JWT token: {}", e.getMessage());
            return false;
        }
    }

    public boolean isResetTokenValid(String token) {
        try {
            Claims claims = parse(token);
            return Boolean.TRUE.equals(claims.get("reset", Boolean.class));
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("Reset token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public boolean validateJwtToken(String token) {
        try {
            Claims claims = parse(token); // Fixed typo: was 'claims宣告'
            if (Boolean.TRUE.equals(claims.get("reset", Boolean.class))) {
                logger.warn("Attempted to use reset token as regular JWT token");
                return false;
            }
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    public String generateJwtToken(Authentication auth) {
        return generateToken((UserDetails) auth.getPrincipal());
    }

    public String extractUsername(String token) {
        return getUserNameFromJwtToken(token);
    }

    public boolean validateToken(String token) {
        return validateJwtToken(token);
    }

    public boolean canAccessAdminResources(String token) {
        return validateJwtToken(token) && isAdminToken(token);
    }

    public boolean canAccessUserResources(String token) {
        return validateJwtToken(token) && (isUserToken(token) || isAdminToken(token));
    }

    public Map<String, Object> getTokenInfo(String token) {
        try {
            Claims claims = parse(token);
            Map<String, Object> info = new HashMap<>();
            info.put("username", claims.getSubject());
            info.put("roles", claims.get("roles", List.class));
            info.put("isAdmin", claims.get("isAdmin", Boolean.class));
            info.put("isUser", claims.get("isUser", Boolean.class));
            info.put("userType", claims.get("userType", String.class));
            info.put("issuedAt", claims.getIssuedAt());
            info.put("expiration", claims.getExpiration());
            return info;
        } catch (Exception e) {
            logger.error("Error extracting token info: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean isUserAuthenticated(HttpServletRequest request) {
        String token = getJwtFromRequest(request);
        return token != null && validateJwtToken(token);
    }

    public Long getUserIdFromJwtToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        Object userId = claims.get("userId");
        return userId != null ? Long.parseLong(userId.toString()) : null;
    }

    public String generateTokenWithUserId(UserDetailsImpl userPrincipal) {
        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .claim("userId", userPrincipal.getId())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key())
                .compact();
    }
}