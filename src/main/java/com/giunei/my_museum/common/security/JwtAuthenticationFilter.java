package com.giunei.my_museum.common.security;

import com.giunei.my_museum.auth.service.JwtService;
import com.giunei.my_museum.user.entity.User;
import com.giunei.my_museum.user.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        try {
            String username = jwtService.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                if (jwtService.isTokenValid(token, user)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    user,
                                    null,
                                    List.of()
                            );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (ExpiredJwtException e) {
            if (isPublicCuratedStream(request)) {
                SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
                return;
            }
            request.setAttribute(JwtAuthenticationEntryPoint.JWT_ERROR_ATTRIBUTE, "TOKEN_EXPIRED");
            SecurityContextHolder.clearContext();
            jwtAuthenticationEntryPoint.commence(request, response, new org.springframework.security.authentication.AuthenticationServiceException("Token expired"));
            return;
        } catch (JwtException | IllegalArgumentException | UsernameNotFoundException e) {
            if (isPublicCuratedStream(request)) {
                SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
                return;
            }
            request.setAttribute(JwtAuthenticationEntryPoint.JWT_ERROR_ATTRIBUTE, "TOKEN_INVALID");
            SecurityContextHolder.clearContext();
            jwtAuthenticationEntryPoint.commence(request, response, new org.springframework.security.authentication.AuthenticationServiceException("Token invalid"));
            return;
        }


        filterChain.doFilter(request, response);
    }

    private static boolean isPublicCuratedStream(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri != null && uri.matches(".*/reactive/(books|movies|series|games)/curated/stream");
    }
}