package com.maplewood.common.exception;

/**
 * Exception for schedule conflicts (409 Conflict)
 */
public class ScheduleConflictException extends RuntimeException {
    
    private Object conflictDetails;
    private Object alternatives; // Optional: alternative sections without conflicts
    
    public ScheduleConflictException(String message) {
        super(message);
    }
    
    public ScheduleConflictException(String message, Object conflictDetails) {
        super(message);
        this.conflictDetails = conflictDetails;
    }
    
    public ScheduleConflictException(String message, Object conflictDetails, Object alternatives) {
        super(message);
        this.conflictDetails = conflictDetails;
        this.alternatives = alternatives;
    }
    
    public Object getConflictDetails() {
        return conflictDetails;
    }
    
    public Object getAlternatives() {
        return alternatives;
    }
}
