package com.maplewood.enrollment.validator.enrollment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maplewood.common.enums.CourseHistoryStatus;
import com.maplewood.common.exception.EnrollmentValidationException;
import com.maplewood.enrollment.entity.CurrentEnrollment;
import com.maplewood.student.repository.StudentCourseHistoryRepository;

/**
 * Validator to ensure student hasn't already completed this course
 * Prevents retaking courses that have been passed
 */
@Component
public class AlreadyCompletedValidator {
    
    @Autowired
    private StudentCourseHistoryRepository courseHistoryRepository;
    
    public void validate(CurrentEnrollment enrollment) {
        if (enrollment.getStudent() == null || enrollment.getCourseSection() == null) {
            throw new IllegalArgumentException("Student and section must be provided");
        }
        
        if (enrollment.getCourseSection().getCourse() == null) {
            throw new IllegalArgumentException("Section must have course defined");
        }
        
        // Check if student has already passed this course
        boolean alreadyPassed = courseHistoryRepository.existsByStudentAndCourseAndStatus(
            enrollment.getStudent(),
            enrollment.getCourseSection().getCourse(),
            CourseHistoryStatus.PASSED
        );
        
        if (alreadyPassed) {
            throw new EnrollmentValidationException(
                "COURSE_ALREADY_COMPLETED",
                "Already completed " + enrollment.getCourseSection().getCourse().getName() + 
                ". Cannot retake a course that has been passed."
            );
        }
    }
}
