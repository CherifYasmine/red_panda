package com.maplewood.course.validator.courseSection;

import org.springframework.stereotype.Component;

import com.maplewood.course.entity.Course;
import com.maplewood.school.entity.Classroom;

/**
 * Validator for classroom room type business rule
 * Ensures classroom's room type matches the course specialization requirement
 */
@Component
public class ClassroomRoomTypeValidator {
    
    /**
     * Validate that classroom's room type matches course specialization requirement
     * @param course The course being offered
     * @param classroom The classroom where the course will be held
     * @throws IllegalArgumentException if room types don't match
     */
    public void validate(Course course, Classroom classroom) {
        if (course.getSpecialization().getRoomType() == null || classroom.getRoomType() == null) {
            throw new IllegalArgumentException("Course specialization and classroom must have room types defined");
        }
        
        if (!course.getSpecialization().getRoomType().getId().equals(classroom.getRoomType().getId())) {
            throw new IllegalArgumentException(
                "Classroom " + classroom.getName() + " has room type '" + classroom.getRoomType().getName() + 
                "' but this course requires '" + course.getSpecialization().getRoomType().getName() + "' room type"
            );
        }
    }
}
