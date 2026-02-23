package com.maplewood.enrollment.validator.enrollment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maplewood.common.enums.CourseHistoryStatus;
import com.maplewood.common.exception.EnrollmentValidationException;
import com.maplewood.course.entity.Course;
import com.maplewood.enrollment.entity.CurrentEnrollment;
import com.maplewood.student.repository.StudentCourseHistoryRepository;

/**
 * Validator for prerequisite requirements
 * Student must have passed all prerequisite courses
 * Prerequisite must be from same or earlier semester
 */
@Component
public class PrerequisiteValidator {
    
    @Autowired
    private StudentCourseHistoryRepository courseHistoryRepository;
    
    public void validate(CurrentEnrollment enrollment) {
        if (enrollment.getStudent() == null || enrollment.getCourseSection() == null) {
            throw new IllegalArgumentException("Student and section must be provided");
        }
        
        Course course = enrollment.getCourseSection().getCourse();
        if (course == null) {
            throw new IllegalArgumentException("Section must have course defined");
        }
        
        // If no prerequisite, validation passes
        if (course.getPrerequisite() == null) {
            return;
        }
        
        // Check if student has passed the prerequisite
        boolean hasPrerequisite = courseHistoryRepository.existsByStudentAndCourseAndStatus(
            enrollment.getStudent(),
            course.getPrerequisite(),
            CourseHistoryStatus.PASSED
        );
        
        if (!hasPrerequisite) {
            throw new EnrollmentValidationException(
                "PREREQUISITE_NOT_MET",
                "Prerequisite not completed: " + course.getPrerequisite().getName() + " (code: " + course.getPrerequisite().getCode() + ")"
            );
        }
        
        // BONUS: Validate semester ordering
        // Prerequisite course's semester_order should be <= current course's semester_order
        if (course.getSemesterOrder() != null && course.getPrerequisite().getSemesterOrder() != null) {
            if (course.getPrerequisite().getSemesterOrder() > course.getSemesterOrder()) {
                throw new IllegalArgumentException(
                    "Prerequisite " + course.getPrerequisite().getName() + " is scheduled for later semester. " +
                    "Prerequisites must be from same or earlier semester."
                );
            }
        }
    }
}
