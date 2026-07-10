package com.giunei.my_museum.common.exception;

public class InvalidFileUploadException extends IllegalArgumentException {

    public InvalidFileUploadException(String message) {
        super(message);
    }
}

