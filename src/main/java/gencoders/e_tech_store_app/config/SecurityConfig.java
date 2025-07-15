package gencoders.e_tech_store_app.config;

import gencoders.e_tech_store_app.jwt.JwtAuthEntryPoint;
import gencoders.e_tech_store_app.jwt.JwtAuthFilter;
import gencoders.e_tech_store_app.user.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthEntryPoint authEntryPoint;
    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsServiceImpl userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * CORS now uses *origin patterns* with credentials explicitly disabled –
     * browsers will attach **no cookies**, leaving us fully stateless.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*")); // accept every origin during dev
        config.setAllowCredentials(false);             // <- key line: disable cookies
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setExposedHeaders(List.of("Authorization", "Content-Disposition"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(e -> e.authenticationEntryPoint(authEntryPoint))
                // _Always_ STATeless with JWT
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .userDetailsService(userDetailsService)
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers(
                                "/", "/error", "/favicon.ico",
                                "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
                                "/swagger-resources/**", "/webjars/**",
                                "/api/auth/**", "/api/products/**", "/api/categories/**",
                                "/api/blog/**", "/api/public/**",
                                "/api/wishlist/shared/**", "/api/users/public/**"
                        ).permitAll()

                        // File upload endpoints - explicit configuration (alternative approach)
                        .requestMatchers("/api/uploads/profile-picture").authenticated()
                        .requestMatchers("/api/uploads/product-image").hasRole("ADMIN")
                        .requestMatchers("/api/uploads/product-images").hasRole("ADMIN")
                        .requestMatchers("/api/uploads/image").hasRole("ADMIN")
                        .requestMatchers("/api/uploads/test-connection").hasRole("ADMIN")

                        // User‑only endpoints
                        .requestMatchers(
                                "/api/users/**", "/api/addresses/**", "/api/wishlist/**",
                                "/api/cart/**", "/api/orders/**", "/api/payments/**",
                                "/api/reviews/**", "/api/users/me/**"
                        ).hasRole("USER")

                        // Admin‑only endpoints
                        .requestMatchers(
                                "/api/admin/**", "/api/products/admin/**", "/api/blog/**/admin/**",
                                "/api/management/**", "/api/categories/**/admin/**", "/actuator/**"
                        ).hasRole("ADMIN")

                        // Everything else needs auth
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}