package com.maplewood.common.exception;

/**
 * Base exception for enrollment validation errors (400 Bad Request)
 */
public class EnrollmentValidationException extends RuntimeException {
    
    private final String errorType;
    private Object details;
    private Object alternatives; // Optional: suggested alternatives to fix the error
    
    public EnrollmentValidationException(String errorType, String message) {
        super(message);
        this.errorType = errorType;
    }
    
    public EnrollmentValidationException(String errorType, String message, Object details) {
        super(message);
        this.errorType = errorType;
        this.details = details;
    }
    
    public EnrollmentValidationException(String errorType, String message, Object details, Object alternatives) {
        super(message);
        this.errorType = errorType;
        this.details = details;
        this.alternatives = alternatives;
    }
    
    public String getErrorType() {
        return errorType;
    }
    
    public Object getDetails() {
        return details;
    }
    
    public Object getAlternatives() {
        return alternatives;
    }
}
