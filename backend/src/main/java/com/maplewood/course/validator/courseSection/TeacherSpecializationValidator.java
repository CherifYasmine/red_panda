package com.maplewood.course.validator.courseSection;

import org.springframework.stereotype.Component;

import com.maplewood.course.entity.Course;
import com.maplewood.school.entity.Teacher;

/**
 * Validator for teacher specialization business rule
 * Ensures teacher's specialization matches course's specialization requirement
 */
@Component
public class TeacherSpecializationValidator {
    
    /**
     * Validate that teacher's specialization matches course requirement
     * @param course The course being assigned
     * @param teacher The teacher being assigned to teach the course
     * @throws IllegalArgumentException if specializations don't match
     */
    public void validate(Course course, Teacher teacher) {
        if (course.getSpecialization() == null || teacher.getSpecialization() == null) {
            throw new IllegalArgumentException("Course and teacher must have specializations defined");
        }
        
        if (!course.getSpecialization().getId().equals(teacher.getSpecialization().getId())) {
            throw new IllegalArgumentException(
                "Teacher " + teacher.getFirstName() + " " + teacher.getLastName() + 
                " specializes in " + teacher.getSpecialization().getName() + 
                " but this course is in " + course.getSpecialization().getName()
            );
        }
    }
}
