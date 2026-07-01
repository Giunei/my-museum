package com.giunei.my_museum.core.websocket;

import com.giunei.my_museum.features.auth.service.JwtService;
import com.giunei.my_museum.features.user.UserRepository;
import com.giunei.my_museum.features.user.entity.User;
import io.jsonwebtoken.JwtException;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
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

        // Require token for user-specific destinations
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return message;
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
            // Allow connection but without user authentication
            return message;
        } catch (Exception ex) {
            // Allow connection but without user authentication
            return message;
        }
    }
}


