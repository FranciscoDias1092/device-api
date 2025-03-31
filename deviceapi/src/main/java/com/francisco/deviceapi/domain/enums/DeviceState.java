package com.francisco.deviceapi.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.francisco.deviceapi.domain.Device;
import com.francisco.deviceapi.exception.InvalidStateException;

import java.util.Locale;

public enum DeviceState {
    AVAILABLE,
    IN_USE,
    INACTIVE;

    @JsonCreator
    public static DeviceState fromString(String state) {
        try {
            return DeviceState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidStateException("Invalid state!");
        }
    }
}
