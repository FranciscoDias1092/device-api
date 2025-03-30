package com.francisco.deviceapi.dto;

import com.francisco.deviceapi.domain.enums.DeviceState;

import java.time.LocalDate;

public record DeviceDTO(Long id,
                        String name,
                        String brand,
                        DeviceState state,
                        LocalDate creationTime) {}
