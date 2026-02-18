package com.maplewood.common.exception;

/**
 * Base exception for resource not found errors (404)
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: %s", resourceName, fieldName, fieldValue));
    }
    
    public ResourceNotFoundException(String resourceName, Long id) {
        super(String.format("%s not found with ID: %s", resourceName, id));
    }
}
