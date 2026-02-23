package com.maplewood.course.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maplewood.course.entity.CourseSectionMeeting;
import com.maplewood.course.validator.courseSectionMeeting.CourseHoursTypeValidator;
import com.maplewood.course.validator.courseSectionMeeting.HoursValidationValidator;
import com.maplewood.course.validator.courseSectionMeeting.NoLunchHourValidator;
import com.maplewood.course.validator.courseSectionMeeting.ScheduleConflictMeetingValidator;
import com.maplewood.course.validator.courseSectionMeeting.TeacherDailyHoursValidator;
import com.maplewood.course.validator.courseSectionMeeting.TimeWindowValidator;
import com.maplewood.course.validator.courseSectionMeeting.UniquenessValidator;

/**
 * Orchestrator for CourseSectionMeeting validations
 * Delegates to specialized validators for each validation rule
 * 
 * Validations (in order):
 * 1. Uniqueness - No duplicate meetings for same section/day/time
 * 2. Time Window - Start < End
 * 3. No Lunch Hour - Meetings cannot overlap with 12:00 PM - 1:00 PM
 * 4. Course Hours Type - Core courses 4-6 hours/week, Elective 2-4 hours/week
 * 5. Hours Validation - Total meeting hours matches course.hoursPerWeek
 * 6. Schedule Conflicts - No teacher or classroom conflicts
 * 7. Teacher Daily Hours - Teacher daily hours <= maxDailyHours
 */
@Component
public class CourseSectionMeetingValidator {
    
    @Autowired
    private UniquenessValidator uniquenessValidator;
    
    @Autowired
    private TimeWindowValidator timeWindowValidator;
    
    @Autowired
    private NoLunchHourValidator noLunchHourValidator;
    
    @Autowired
    private CourseHoursTypeValidator courseHoursTypeValidator;
    
    @Autowired
    private HoursValidationValidator hoursValidationValidator;
    
    @Autowired
    private ScheduleConflictMeetingValidator scheduleConflictMeetingValidator;
    
    @Autowired
    private TeacherDailyHoursValidator teacherDailyHoursValidator;
    
    /**
     * Main validation method - delegates to all specialized validators
     */
    public void validate(CourseSectionMeeting meeting) {
        uniquenessValidator.validate(meeting);
        timeWindowValidator.validate(meeting);
        noLunchHourValidator.validate(meeting);
        courseHoursTypeValidator.validate(meeting);
        hoursValidationValidator.validate(meeting);
        scheduleConflictMeetingValidator.validate(meeting);
        teacherDailyHoursValidator.validate(meeting);
    }
}
