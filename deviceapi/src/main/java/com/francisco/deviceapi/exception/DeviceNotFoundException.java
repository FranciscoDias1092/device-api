package com.francisco.deviceapi.exception;

public class DeviceNotFoundException extends NotFoundException {

    public DeviceNotFoundException() {
        super("Device not found!");
    }
}
