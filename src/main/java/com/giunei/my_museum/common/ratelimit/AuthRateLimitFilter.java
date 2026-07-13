package com.giunei.my_museum.common.ratelimit;

import com.giunei.my_museum.common.exception.RateLimitExceededException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public class AuthRateLimitFilter extends OncePerRequestFilter {

    private final AuthRateLimitService authRateLimitService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!HttpMethod.POST.matches(request.getMethod())) {
            return true;
        }
        String path = normalizedPath(request);
        return !"/auth/login".equals(path) && !"/auth/register".equals(path);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String clientIp = ClientIpResolver.resolve(request);
        String path = normalizedPath(request);

        try {
            if ("/auth/login".equals(path)) {
                authRateLimitService.checkLogin(clientIp);
            } else if ("/auth/register".equals(path)) {
                authRateLimitService.checkRegister(clientIp);
            }
            filterChain.doFilter(request, response);
        } catch (RateLimitExceededException ex) {
            writeTooManyRequests(response, request, ex);
        }
    }

    private static String normalizedPath(HttpServletRequest request) {
        String path = request.getServletPath();
        if (path == null || path.isBlank()) {
            path = request.getRequestURI();
        }
        if (path != null && path.length() > 1 && path.endsWith("/")) {
            return path.substring(0, path.length() - 1);
        }
        return path;
    }

    private static void writeTooManyRequests(
            HttpServletResponse response,
            HttpServletRequest request,
            RateLimitExceededException ex
    ) throws IOException {
        response.setStatus(429);
        response.setHeader("Retry-After", String.valueOf(ex.getRetryAfterSeconds()));
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String body = """
                {"timestamp":"%s","status":429,"error":"%s","path":"%s"}
                """.formatted(
                LocalDateTime.now(),
                escapeJson(ex.getMessage()),
                escapeJson(request.getRequestURI())
        ).trim();
        response.getWriter().write(body);
    }

    private static String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
