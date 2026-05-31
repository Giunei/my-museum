package com.giunei.my_museum.core.websocket;

import com.giunei.my_museum.features.auth.service.JwtService;
import com.giunei.my_museum.features.user.UserRepository;
import com.giunei.my_museum.features.user.entity.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
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
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null || !StompCommand.CONNECT.equals(accessor.getCommand())) {
            return message;
        }

        String authorization = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION);

        if (authorization == null) {
            authorization = accessor.getFirstNativeHeader("authorization");
        }

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Token JWT ausente no WebSocket");
        }

        String token = authorization.substring(7);

        try {
            String username = jwtService.extractUsername(token);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

            if (!jwtService.isTokenValid(token, user)) {
                throw new IllegalArgumentException("Token JWT inválido no WebSocket");
            }

            accessor.setUser(new UsernamePasswordAuthenticationToken(user, null, List.of()));
            return message;
        } catch (ExpiredJwtException | JwtException | IllegalArgumentException ex) {
            throw new IllegalArgumentException("Falha ao autenticar conexão WebSocket", ex);
        }
    }
}
