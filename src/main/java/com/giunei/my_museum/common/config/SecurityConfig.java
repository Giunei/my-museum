package com.giunei.my_museum.common.config;

import com.giunei.my_museum.common.ratelimit.AuthRateLimitFilter;
import com.giunei.my_museum.common.ratelimit.AuthRateLimitService;
import com.giunei.my_museum.common.security.JwtAuthenticationEntryPoint;
import com.giunei.my_museum.common.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CorsConfigurationSource corsConfigurationSource;
    private final AuthRateLimitService authRateLimitService;

    @Bean
    public AuthRateLimitFilter authRateLimitFilter() {
        return new AuthRateLimitFilter(authRateLimitService);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthRateLimitFilter authRateLimitFilter) {

        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/api/ws/**").permitAll()
                        .requestMatchers("/books/search").permitAll()
                        .requestMatchers("/movies/search").permitAll()
                        .requestMatchers("/series/search").permitAll()
                        .requestMatchers("/games/search").permitAll()
                        .requestMatchers("/books/curated").permitAll()
                        .requestMatchers("/reactive/books/search").permitAll()
                        .requestMatchers("/reactive/books/curated/stream").permitAll()
                        .requestMatchers("/reactive/movies/curated/stream").permitAll()
                        .requestMatchers("/reactive/series/curated/stream").permitAll()
                        .requestMatchers("/reactive/games/curated/stream").permitAll()
                        .requestMatchers("/steam/callback", "/steam/callback/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users/username/*/profile").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users/*/achievements", "/users/*/achievements/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users/*/activities/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users/*/books/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users/*/movies/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users/*/series/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users/*/games/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users/*/goals").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users/*/preferences").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users/*/collections", "/users/*/collections/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users/*/media", "/users/*/media/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users/*/lol/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users/*/steam/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/profile/*/comments").permitAll()
                        .requestMatchers(HttpMethod.GET, "/home/**").permitAll()
                        .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
                        .requestMatchers("/actuator/**").denyAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                .addFilterBefore(authRateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
