package com.maplewood.enrollment.validator.enrollment;

import org.springframework.stereotype.Component;

import com.maplewood.common.exception.ScheduleConflictException;
import com.maplewood.course.entity.CourseSection;
import com.maplewood.enrollment.entity.CurrentEnrollment;

/**
 * Validator for section capacity limits
 * Ensures section hasn't reached maximum capacity
 */
@Component
public class CapacityValidator {
    
    public void validate(CurrentEnrollment enrollment) {
        if (enrollment.getCourseSection() == null) {
            throw new IllegalArgumentException("Section must be provided");
        }
        
        CourseSection section = enrollment.getCourseSection();
        Integer capacity = section.getCapacity();
        Integer enrollmentCount = section.getEnrollmentCount();
        
        if (capacity == null) {
            throw new IllegalArgumentException("Section must have capacity defined");
        }
        if (enrollmentCount == null) {
            throw new IllegalArgumentException("Section must have enrollmentCount defined");
        }
        
        if (enrollmentCount >= capacity) {
            throw new ScheduleConflictException(
                "Section has reached maximum capacity (" + capacity + " students)"
            );
        }
    }
}
