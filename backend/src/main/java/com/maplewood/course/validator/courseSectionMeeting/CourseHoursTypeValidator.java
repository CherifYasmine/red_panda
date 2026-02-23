package com.maplewood.course.validator.courseSectionMeeting;

import org.springframework.stereotype.Component;

import com.maplewood.common.enums.CourseType;
import com.maplewood.course.entity.CourseSectionMeeting;

/**
 * Validator for course hours type constraints
 * Ensures Core courses are 4-6 hours/week, Elective courses are 2-4 hours/week
 */
@Component
public class CourseHoursTypeValidator {
    
    public void validate(CourseSectionMeeting meeting) {
        if (meeting.getSection() == null || meeting.getSection().getCourse() == null) {
            throw new IllegalArgumentException("Section and course must be provided");
        }
        
        Integer hoursPerWeek = meeting.getSection().getCourse().getHoursPerWeek();
        CourseType courseType = meeting.getSection().getCourse().getCourseType();
        
        if (hoursPerWeek == null || courseType == null) {
            return;  // Skip validation if not defined
        }
        
        if (CourseType.CORE.equals(courseType)) {
            if (hoursPerWeek < 4 || hoursPerWeek > 6) {
                throw new IllegalArgumentException(
                    "Core courses must be 4-6 hours per week. " +
                    "Course is defined as " + hoursPerWeek + " hours/week"
                );
            }
        } else if (CourseType.ELECTIVE.equals(courseType)) {
            if (hoursPerWeek < 2 || hoursPerWeek > 4) {
                throw new IllegalArgumentException(
                    "Elective courses must be 2-4 hours per week. " +
                    "Course is defined as " + hoursPerWeek + " hours/week"
                );
            }
        }
    }
}
