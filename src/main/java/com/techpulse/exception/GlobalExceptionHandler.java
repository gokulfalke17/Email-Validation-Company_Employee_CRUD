package com.techpulse.exception;

import com.techpulse.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<ApiResponse> handleEmployeeNotFound(EmployeeNotFoundException exception) {
        return  new ResponseEntity<>(
                new ApiResponse(false, exception.getMessage(), null),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse> handleBadRequest(BadRequestException exception) {
        return new ResponseEntity<>(
                new ApiResponse(false, exception.getMessage(), null),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(CompanyNotAvailableException.class)
    public ResponseEntity<ApiResponse> handleCompanyNotFoundException(CompanyNotAvailableException exception) {
        return  new ResponseEntity<>(
                new ApiResponse(false, exception.getMessage(), null),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse> handleMissingParam(MissingServletRequestParameterException ex) {
        String msg = "Missing request parameter: " + ex.getParameterName();
        log.warn("Missing request parameter: {}", ex.getParameterName());
        return new ResponseEntity<>(new ApiResponse(false, msg, null), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse> handleConstraintViolation(ConstraintViolationException ex) {
        log.warn("Constraint violation: {}", ex.getMessage());
        return new ResponseEntity<>(new ApiResponse(false, "Invalid request parameter: " + ex.getMessage(), null), HttpStatus.BAD_REQUEST);
    }

    // Handle invalid JSON in request body
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> handleJsonParse(HttpMessageNotReadableException ex) {
        String msg = "Malformed JSON request";
        if (ex.getCause() != null) {
            String causeMsg = ex.getCause().getMessage();
            if (causeMsg != null && !causeMsg.isBlank()) {
                msg = "Malformed JSON request: " + causeMsg;
            }
        }
        log.warn("JSON parse error: {}", msg);
        return new ResponseEntity<>(new ApiResponse(false, msg, null), HttpStatus.BAD_REQUEST);
    }

    // Handle @Valid validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        log.warn("Validation failed: {}", errors);
        return new ResponseEntity<>(new ApiResponse(false, "Validation Failed", errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGeneral(Exception exception) {
        log.error("Unhandled exception: {}", exception.getMessage(), exception);
        return  new ResponseEntity<>(
                new ApiResponse(false, exception.getMessage(), null),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

}
