package com.maplewood.course.validator.courseSectionMeeting;

import java.time.LocalTime;

import org.springframework.stereotype.Component;

import com.maplewood.course.entity.CourseSectionMeeting;

/**
 * Validator to ensure no meetings during lunch hour
 * Prevents meetings from overlapping 12:00 PM - 1:00 PM
 */
@Component
public class NoLunchHourValidator {
    
    public void validate(CourseSectionMeeting meeting) {
        LocalTime lunchStart = LocalTime.of(12, 0);  // 12:00 PM
        LocalTime lunchEnd = LocalTime.of(13, 0);    // 1:00 PM
        
        LocalTime meetingStart = meeting.getStartTime();
        LocalTime meetingEnd = meeting.getEndTime();
        
        if (meetingStart.isBefore(lunchEnd) && meetingEnd.isAfter(lunchStart)) {
            throw new IllegalArgumentException(
                "Classes cannot be scheduled during lunch hour (12:00 PM - 1:00 PM). " +
                "Meeting scheduled from " + meetingStart + " to " + meetingEnd
            );
        }
    }
}
