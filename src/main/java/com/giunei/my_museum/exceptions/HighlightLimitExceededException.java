package com.giunei.my_museum.exceptions;

public class HighlightLimitExceededException extends IllegalStateException {

    public HighlightLimitExceededException(String message) {
        super(message);
    }
}

