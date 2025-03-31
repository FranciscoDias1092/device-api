package com.francisco.deviceapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.francisco.deviceapi.domain.enums.DeviceState;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PatchDeviceRequestDTO(String name,
                                    String brand,
                                    DeviceState state) {}
