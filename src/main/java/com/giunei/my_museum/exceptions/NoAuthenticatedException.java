package com.giunei.my_museum.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class NoAuthenticatedException extends RuntimeException {

    public NoAuthenticatedException(String message) {
        super(message);
    }
}
