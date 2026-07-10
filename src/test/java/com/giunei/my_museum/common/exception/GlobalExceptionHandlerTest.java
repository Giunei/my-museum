package com.giunei.my_museum.common.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = new MockHttpServletRequest();
        request.setRequestURI("/test");
    }

    @Test
    void should_return404_when_notFoundExceptionIsThrown() {
        ResponseEntity<ErrorResponse> response = handler.handleNotFound(
                new NotFoundException("Recurso não encontrado"),
                request
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().error()).isEqualTo("Recurso não encontrado");
        assertThat(response.getBody().path()).isEqualTo("/test");
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    @Test
    void should_return401_when_noAuthenticatedExceptionIsThrown() {
        ResponseEntity<ErrorResponse> response = handler.handleNoAuthenticated(
                new NoAuthenticatedException("No authenticated user"),
                request
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().status()).isEqualTo(401);
    }

    @Test
    void should_return400_when_businessExceptionIsThrown() {
        ResponseEntity<ErrorResponse> response = handler.handleBusinessException(
                new BusinessException("Operação inválida"),
                request
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().error()).isEqualTo("Operação inválida");
    }

    @Test
    void should_return500_when_genericExceptionIsThrown() {
        ResponseEntity<ErrorResponse> response = handler.handleGeneric(
                new RuntimeException("unexpected"),
                request
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().error()).isEqualTo("Erro interno do servidor");
    }

    @Test
    void should_resetContentType_when_eventStreamRequestFails() {
        request.addHeader("Accept", MediaType.TEXT_EVENT_STREAM_VALUE);

        ResponseEntity<ErrorResponse> response = handler.handleNotFound(
                new NotFoundException("Stream error"),
                request
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }
}
