package com.maplewood.enrollment.validator.enrollment;

import org.springframework.stereotype.Component;

import com.maplewood.common.exception.EnrollmentValidationException;
import com.maplewood.enrollment.entity.CurrentEnrollment;

/**
 * Validator for grade level requirements
 * Ensures student's grade level is within course's min/max range
 */
@Component
public class GradeLevelValidator {
    
    public void validate(CurrentEnrollment enrollment) {
        if (enrollment.getStudent() == null || enrollment.getCourseSection() == null) {
            throw new IllegalArgumentException("Student and section must be provided");
        }
        
        if (enrollment.getCourseSection().getCourse() == null) {
            throw new IllegalArgumentException("Section must have course defined");
        }
        
        Integer studentGradeLevel = enrollment.getStudent().getGradeLevel();
        Integer courseGradeMin = enrollment.getCourseSection().getCourse().getGradeLevelMin();
        Integer courseGradeMax = enrollment.getCourseSection().getCourse().getGradeLevelMax();
        
        if (studentGradeLevel == null) {
            throw new IllegalArgumentException("Student must have grade level defined");
        }
        if (courseGradeMin == null || courseGradeMax == null) {
            throw new IllegalArgumentException("Course must have grade level range defined");
        }
        
        if (studentGradeLevel < courseGradeMin || studentGradeLevel > courseGradeMax) {
            String gradeRequirement;
            if (courseGradeMin.equals(courseGradeMax)) {
                gradeRequirement = "grade " + courseGradeMin;
            } else {
                gradeRequirement = "between grades " + courseGradeMin + " and " + courseGradeMax;
            }
            
            throw new EnrollmentValidationException(
                "GRADE_LEVEL_NOT_ALLOWED",
                "Grade level " + studentGradeLevel + 
                " is not allowed for this course (requires " + gradeRequirement + ")"
            );
        }
    }
}
