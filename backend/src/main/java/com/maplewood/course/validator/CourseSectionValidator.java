package com.maplewood.course.validator;

import org.springframework.stereotype.Component;

import com.maplewood.course.entity.Course;
import com.maplewood.school.entity.Classroom;
import com.maplewood.school.entity.Semester;
import com.maplewood.school.entity.Teacher;

/**
 * Validator for CourseSection business rules
 * Validates that a course section can be created with given teacher, classroom, and semester
 */
@Component
public class CourseSectionValidator {
    
    /**
     * Validate all business rules for creating a course section
     * Throws IllegalArgumentException if any validation fails
     */
    public void validate(Course course, Teacher teacher, Classroom classroom, Semester semester) {
        validateTeacherSpecialization(course, teacher);
        validateClassroomRoomType(course, classroom);
        validateSemesterOrder(course, semester);
    }
    
    /**
     * VALIDATION 1: Teacher's specialization must match course's specialization
     */
    private void validateTeacherSpecialization(Course course, Teacher teacher) {
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
    
    /**
     * VALIDATION 2: Classroom's room type must match course specialization's required room type
     */
    private void validateClassroomRoomType(Course course, Classroom classroom) {
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
    
    /**
     * VALIDATION 3: Course semester order must match active semester's order (Fall/Spring)
     * Ensures Spring courses aren't created in Fall semesters and vice versa
     */
    private void validateSemesterOrder(Course course, Semester semester) {
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
