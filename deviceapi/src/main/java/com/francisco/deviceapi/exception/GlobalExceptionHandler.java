package com.francisco.deviceapi.exception;

import com.francisco.deviceapi.dto.CustomErrorMessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(DeviceAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public CustomErrorMessageDTO handleDeviceAlreadyExistsException(DeviceAlreadyExistsException e) {
        return new CustomErrorMessageDTO(e.getMessage());
    }

    @ExceptionHandler(DeviceInUseException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public CustomErrorMessageDTO handleDeviceInUseException(DeviceInUseException e) {
        return new CustomErrorMessageDTO(e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public CustomErrorMessageDTO handleNotFoundException(NotFoundException e) {
        return new CustomErrorMessageDTO(e.getMessage());
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public CustomErrorMessageDTO handleBindException(BindException e) {
        return new CustomErrorMessageDTO(e.getMessage());
    }

    @ExceptionHandler(InvalidStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public CustomErrorMessageDTO handleInvalidStateException(InvalidStateException e) {
        return new CustomErrorMessageDTO(e.getMessage());
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public CustomErrorMessageDTO handleHandlerMethodValidationException(HandlerMethodValidationException e) {
        return new CustomErrorMessageDTO("Argument validation failed!");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Function<FieldError, String> getErrorMessageOrDefaultMessage =
                error -> Objects.requireNonNullElse(error.getDefaultMessage(), "Unknown message!");

        return e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, getErrorMessageOrDefaultMessage));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public CustomErrorMessageDTO handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return new CustomErrorMessageDTO("Type mismatch!");
    }
}
