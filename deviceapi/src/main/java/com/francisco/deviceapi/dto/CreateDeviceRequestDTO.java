package com.francisco.deviceapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.francisco.deviceapi.domain.enums.DeviceState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CreateDeviceRequestDTO(@NotBlank String name,
                                     @NotBlank String brand,
                                     @NotNull DeviceState state,
                                     @NotNull @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate creationTime) {}

