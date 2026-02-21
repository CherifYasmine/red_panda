package com.maplewood.common.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard error response format for all API errors
 * Provides consistent error information to API clients
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    
    /**
     * HTTP status code (400, 409, 404, 500, etc.)
     */
    private Integer status;
    
    /**
     * Error category (e.g., "Validation Error", "Conflict", "Not Found")
     */
    private String error;
    
    /**
     * Detailed error message
     */
    private String message;
    
    /**
     * Additional details about the error (optional)
     * Can include field names, conflicting courses, etc.
     */
    private Object details;
    
    /**
     * Suggested alternatives to fix the error (optional)
     * Can include alternative courses, time slots, prerequisites, etc.
     */
    private Object alternatives;
    
    /**
     * Timestamp when error occurred
     */
    private LocalDateTime timestamp;
    
    /**
     * Request path that caused the error (for debugging)
     */
    private String path;
    
    /**
     * Builder convenience method
     */
    public static ErrorResponse of(Integer status, String error, String message) {
        return ErrorResponse.builder()
            .status(status)
            .error(error)
            .message(message)
            .timestamp(LocalDateTime.now())
            .build();
    }
    
    /**
     * Builder convenience method with details
     */
    public static ErrorResponse of(Integer status, String error, String message, Object details) {
        return ErrorResponse.builder()
            .status(status)
            .error(error)
            .message(message)
            .details(details)
            .timestamp(LocalDateTime.now())
            .build();
    }
}
