package com.maplewood.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.maplewood.common.dto.ErrorResponse;

/**
 * Global exception handler for all REST API endpoints
 * Catches exceptions and returns consistent error responses
 * Ensures all errors are returned in a standardized format
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Handle validation errors (400 Bad Request)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
        ErrorResponse error = ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Invalid Argument")
            .message(ex.getMessage())
            .timestamp(java.time.LocalDateTime.now())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * Handle resource not found errors (404 Not Found)
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
        ErrorResponse error = ErrorResponse.builder()
            .status(HttpStatus.NOT_FOUND.value())
            .error("Resource Not Found")
            .message(ex.getMessage())
            .timestamp(java.time.LocalDateTime.now())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    /**
     * Handle enrollment validation errors (400 Bad Request)
     * Catches: PrerequisiteNotMetException, GradeLevelInappropriateException, etc.
     */
    @ExceptionHandler(EnrollmentValidationException.class)
    public ResponseEntity<ErrorResponse> handleEnrollmentValidation(EnrollmentValidationException ex, WebRequest request) {
        ErrorResponse error = ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .error(ex.getErrorType())
            .message(ex.getMessage())
            .details(ex.getDetails())
            .timestamp(java.time.LocalDateTime.now())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * Handle schedule conflict errors (409 Conflict)
     */
    @ExceptionHandler(ScheduleConflictException.class)
    public ResponseEntity<ErrorResponse> handleScheduleConflict(ScheduleConflictException ex, WebRequest request) {
        ErrorResponse error = ErrorResponse.builder()
            .status(HttpStatus.CONFLICT.value())
            .error("Schedule Conflict")
            .message(ex.getMessage())
            .details(ex.getConflictDetails())
            .timestamp(java.time.LocalDateTime.now())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
    
    /**
     * Handle unexpected internal server errors (500)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        ErrorResponse error = ErrorResponse.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("Internal Server Error")
            .message("An unexpected error occurred: " + ex.getMessage())
            .timestamp(java.time.LocalDateTime.now())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
