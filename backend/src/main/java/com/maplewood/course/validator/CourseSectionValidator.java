package com.maplewood.course.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maplewood.course.entity.Course;
import com.maplewood.course.validator.courseSection.ClassroomRoomTypeValidator;
import com.maplewood.course.validator.courseSection.SemesterOrderValidator;
import com.maplewood.course.validator.courseSection.TeacherSpecializationValidator;
import com.maplewood.school.entity.Classroom;
import com.maplewood.school.entity.Semester;
import com.maplewood.school.entity.Teacher;

/**
 * Orchestrator for CourseSection business rules
 * Delegates to specialized validators for each business rule
 */
@Component
public class CourseSectionValidator {
    
    @Autowired
    private TeacherSpecializationValidator teacherSpecializationValidator;
    
    @Autowired
    private ClassroomRoomTypeValidator classroomRoomTypeValidator;
    
    @Autowired
    private SemesterOrderValidator semesterOrderValidator;
    
    /**
     * Validate all business rules for creating a course section
     * Throws IllegalArgumentException if any validation fails
     */
    public void validate(Course course, Teacher teacher, Classroom classroom, Semester semester) {
        teacherSpecializationValidator.validate(course, teacher);
        classroomRoomTypeValidator.validate(course, classroom);
        semesterOrderValidator.validate(course, semester);
    }
}
