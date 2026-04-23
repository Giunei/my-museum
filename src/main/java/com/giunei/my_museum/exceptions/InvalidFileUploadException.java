package com.giunei.my_museum.exceptions;

public class InvalidFileUploadException extends IllegalArgumentException {

    public InvalidFileUploadException(String message) {
        super(message);
    }
}

