package com.maplewood.common.exception;

/**
 * Exception for schedule conflicts (409 Conflict)
 * Thrown when a student tries to enroll in sections with conflicting meeting times
 */
public class ScheduleConflictException extends RuntimeException {
    
    private Object conflictDetails;
    
    public ScheduleConflictException(String message) {
        super(message);
    }
    
    public ScheduleConflictException(String message, Object conflictDetails) {
        super(message);
        this.conflictDetails = conflictDetails;
    }
    
    public Object getConflictDetails() {
        return conflictDetails;
    }
}
