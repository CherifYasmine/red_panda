package com.maplewood.common.exception;

/**
 * Base exception for enrollment validation errors (400 Bad Request)
 * Extended by: PrerequisiteNotMetException, CourseLimitExceededException, etc.
 */
public class EnrollmentValidationException extends RuntimeException {
    
    private final String errorType;
    private Object details;
    
    public EnrollmentValidationException(String errorType, String message) {
        super(message);
        this.errorType = errorType;
    }
    
    public EnrollmentValidationException(String errorType, String message, Object details) {
        super(message);
        this.errorType = errorType;
        this.details = details;
    }
    
    public String getErrorType() {
        return errorType;
    }
    
    public Object getDetails() {
        return details;
    }
}
