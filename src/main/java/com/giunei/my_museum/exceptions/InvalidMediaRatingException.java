package com.giunei.my_museum.exceptions;

public class InvalidMediaRatingException extends IllegalArgumentException {

    public InvalidMediaRatingException(String message) {
        super(message);
    }
}

