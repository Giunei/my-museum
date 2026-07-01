package com.giunei.my_museum.core.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    public static final String JWT_ERROR_ATTRIBUTE = "jwt_error";

    @Override
    public void commence(HttpServletRequest request,
                         @NonNull HttpServletResponse response,
                         @NonNull AuthenticationException authException) throws IOException {
        String jwtError = (String) request.getAttribute(JWT_ERROR_ATTRIBUTE);

        String message = switch (jwtError) {
            case null -> "Não autenticado";
            case "TOKEN_EXPIRED" -> "Access token expirado";
            case "TOKEN_INVALID" -> "Access token inválido";
            default -> "Não autenticado";
        };

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(buildBody(message, request.getRequestURI()));
    }

    private String buildBody(String message, String path) {
        String timestamp = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        String safeMessage = escapeJson(message);
        String safePath = escapeJson(path);

        return "{"
                + "\"timestamp\":\"" + timestamp + "\","
                + "\"status\":" + HttpServletResponse.SC_UNAUTHORIZED + ","
                + "\"error\":\"" + safeMessage + "\","
                + "\"path\":\"" + safePath + "\""
                + "}";
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}


