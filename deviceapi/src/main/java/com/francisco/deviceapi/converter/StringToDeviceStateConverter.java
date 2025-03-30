package com.francisco.deviceapi.converter;

import com.francisco.deviceapi.domain.enums.DeviceState;
import com.francisco.deviceapi.exception.InvalidStateException;
import org.springframework.core.convert.converter.Converter;

public class StringToDeviceStateConverter implements Converter<String, DeviceState> {

    @Override
    public DeviceState convert(String state) {
        try {
            return DeviceState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidStateException("State " + state + " not recognized!");
        }
    }
}
