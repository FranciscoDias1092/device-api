package com.francisco.deviceapi.exception;

public abstract class AlreadyExistsException extends RuntimeException {

    public AlreadyExistsException(String message) {
        super(message);
    }
}
