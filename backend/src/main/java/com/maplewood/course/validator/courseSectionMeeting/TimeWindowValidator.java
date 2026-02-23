package com.maplewood.course.validator.courseSectionMeeting;

import java.time.LocalTime;

import org.springframework.stereotype.Component;

import com.maplewood.course.entity.CourseSectionMeeting;

/**
 * Validator for time window validity
 * Ensures start time is before end time
 */
@Component
public class TimeWindowValidator {
    
    public void validate(CourseSectionMeeting meeting) {
        LocalTime start = meeting.getStartTime();
        LocalTime end = meeting.getEndTime();
        
        // Start < End
        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
    }
}
