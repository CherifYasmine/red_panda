package com.maplewood.course.validator.courseSection;

import org.springframework.stereotype.Component;

import com.maplewood.course.entity.Course;
import com.maplewood.school.entity.Semester;

/**
 * Validator for semester order business rule
 * Ensures course is created in correct season semester (Fall or Spring)
 */
@Component
public class SemesterOrderValidator {
    
    /**
     * Validate that course semester order matches the given semester's order
     * Prevents creating Spring courses in Fall semester and vice versa
     * @param course The course being offered (has semester requirement)
     * @param semester The semester when the course section is being created
     * @throws IllegalArgumentException if semester orders don't match
     */
    public void validate(Course course, Semester semester) {
        if (course.getSemesterOrder() == null || semester.getOrderInYear() == null) {
            throw new IllegalArgumentException("Course and semester must have semester order defined");
        }
        
        if (!course.getSemesterOrder().equals(semester.getOrderInYear())) {
            String courseSeasonName = course.getSemesterOrder() == 1 ? "Fall" : "Spring";
            String semesterSeasonName = semester.getOrderInYear() == 1 ? "Fall" : "Spring";
            throw new IllegalArgumentException(
                "Course " + course.getCode() + " is a " + courseSeasonName + " course, " +
                "but the active semester is " + semesterSeasonName + ". Please create this course section in a " + courseSeasonName + " semester."
            );
        }
    }
}
