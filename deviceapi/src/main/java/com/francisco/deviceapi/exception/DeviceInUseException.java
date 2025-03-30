package com.francisco.deviceapi.exception;

public class DeviceInUseException extends RuntimeException {

    public DeviceInUseException(String message) {
        super(message);
    }
}
