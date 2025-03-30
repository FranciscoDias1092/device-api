package com.francisco.deviceapi.exception;

import com.francisco.deviceapi.dto.CustomErrorMessageDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CustomErrorMessageDTO handleNotFoundException(NotFoundException e) {
        return new CustomErrorMessageDTO(e.getMessage());
    }

    @ExceptionHandler(DeviceInUseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CustomErrorMessageDTO handleDeviceInUseException(DeviceInUseException e) {
        return new CustomErrorMessageDTO(e.getMessage());
    }
}
