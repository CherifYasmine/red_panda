package com.maplewood.enrollment.validator.enrollment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maplewood.common.exception.ScheduleConflictException;
import com.maplewood.enrollment.entity.CurrentEnrollment;
import com.maplewood.enrollment.repository.CurrentEnrollmentRepository;

/**
 * Validator for course enrollment limits
 * Student cannot exceed 5 courses per semester
 */
@Component
public class CourseLimitValidator {
    
    @Autowired
    private CurrentEnrollmentRepository enrollmentRepository;
    
    public void validate(CurrentEnrollment enrollment) {
        if (enrollment.getStudent() == null || enrollment.getCourseSection() == null) {
            throw new IllegalArgumentException("Student and section must be provided");
        }
        
        if (enrollment.getCourseSection().getSemester() == null) {
            throw new IllegalArgumentException("Section must have semester defined");
        }
        
        long currentCourses = enrollmentRepository.countByStudent_IdAndCourseSection_Semester_Id(
            enrollment.getStudent().getId(),
            enrollment.getCourseSection().getSemester().getId()
        );
        
        if (currentCourses >= 5) {
            throw new ScheduleConflictException(
                "Cannot enroll in more than 5 courses per semester (already enrolled in 5)"
            );
        }
    }
}
