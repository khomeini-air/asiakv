package com.asia.asiakv.shared.exception;

import com.asia.asiakv.shared.dto.ApiResponse;
import com.asia.asiakv.shared.dto.Result;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleBadRequest(MethodArgumentNotValidException ex) {
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                Result.PARAM_ILLEGAL,
                ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse> handleConstraintViolation(ConstraintViolationException ex) {
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                Result.PARAM_ILLEGAL,
                ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleNotFound(ResourceNotFoundException ex) {
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                Result.RESOURCE_NOT_FOUND,
                ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> handleInvalidEnumValue(HttpMessageNotReadableException ex) {
        String message = ex.getMessage();

        if (ex.getCause() instanceof InvalidFormatException ife) {
            if (ife.getTargetType().isEnum()) {
                message = String.format("Invalid value '%s' for %s. Allowed values: %s",
                        ife.getValue(),
                        ife.getTargetType().getSimpleName(),
                        Arrays.toString(ife.getTargetType().getEnumConstants()));
            }
        }

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                Result.PARAM_ILLEGAL,
                message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGenericException(Exception ex) {
        log.error("Internal Error: {}", ex.getMessage());
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                Result.INTERNAL_ERROR,
                ex.getMessage());
    }

    private ResponseEntity<ApiResponse> buildErrorResponse(HttpStatus status, Result result,  String message) {
        return ResponseEntity
                .status(status)
                .body(ApiResponse
                        .builder()
                        .result(result)
                        .message(message).
                        build());
    }
}
