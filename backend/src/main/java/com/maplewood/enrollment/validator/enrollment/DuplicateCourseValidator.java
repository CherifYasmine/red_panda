package com.maplewood.enrollment.validator.enrollment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maplewood.common.exception.DuplicateResourceException;
import com.maplewood.enrollment.entity.CurrentEnrollment;
import com.maplewood.enrollment.repository.CurrentEnrollmentRepository;

/**
 * Validator for duplicate course enrollment
 * Ensures student not already enrolled in this course (any section) in this semester
 */
@Component
public class DuplicateCourseValidator {
    
    @Autowired
    private CurrentEnrollmentRepository enrollmentRepository;
    
    public void validate(CurrentEnrollment enrollment) {
        if (enrollment.getStudent() == null || enrollment.getStudent().getId() == null) {
            throw new IllegalArgumentException("Student must be provided");
        }
        if (enrollment.getCourseSection() == null || enrollment.getCourseSection().getId() == null) {
            throw new IllegalArgumentException("Section must be provided");
        }
        if (enrollment.getCourseSection().getCourse() == null) {
            throw new IllegalArgumentException("Section must have course defined");
        }
        if (enrollment.getCourseSection().getSemester() == null) {
            throw new IllegalArgumentException("Section must have semester defined");
        }
        
        long courseCount = enrollmentRepository.countByStudent_IdAndCourse_IdAndSemester_Id(
            enrollment.getStudent().getId(),
            enrollment.getCourseSection().getCourse().getId(),
            enrollment.getCourseSection().getSemester().getId()
        );
        
        if (courseCount > 0) {
            throw new DuplicateResourceException(
                "Already enrolled in " + enrollment.getCourseSection().getCourse().getName() + 
                 " in this semester. Cannot take the same course twice per semester."
            );
        }
    }
}
