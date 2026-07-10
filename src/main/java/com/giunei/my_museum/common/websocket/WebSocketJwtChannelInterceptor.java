package com.giunei.my_museum.common.websocket;

import com.giunei.my_museum.auth.service.JwtService;
import com.giunei.my_museum.user.entity.User;
import com.giunei.my_museum.user.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketJwtChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    @Nullable
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) {
            return message;
        }

        StompCommand command = accessor.getCommand();
        if (command == null || !StompCommand.CONNECT.equals(command)) {
            return message;
        }

        String authorization = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null) {
            authorization = accessor.getFirstNativeHeader("authorization");
        }

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            log.warn("WebSocket CONNECT rejeitado: token ausente");
            return null;
        }

        String token = authorization.substring(7);

        try {
            String username = jwtService.extractUsername(token);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

            if (!jwtService.isTokenValid(token, user)) {
                throw new IllegalArgumentException("Token JWT inválido no WebSocket");
            }

            accessor.setUser(new UsernamePasswordAuthenticationToken(user.getUsername(), null, List.of()));
            return message;
        } catch (JwtException | IllegalArgumentException ex) {
            log.warn("WebSocket CONNECT rejeitado: {}", ex.getMessage());
            return null;
        } catch (Exception ex) {
            log.warn("WebSocket CONNECT rejeitado por erro inesperado", ex);
            return null;
        }
    }
}
