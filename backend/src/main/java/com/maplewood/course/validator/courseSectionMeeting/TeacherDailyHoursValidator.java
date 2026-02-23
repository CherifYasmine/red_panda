package com.maplewood.course.validator.courseSectionMeeting;

import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maplewood.common.exception.ScheduleConflictException;
import com.maplewood.course.entity.CourseSectionMeeting;
import com.maplewood.course.repository.CourseSectionMeetingRepository;
import com.maplewood.school.entity.Teacher;

/**
 * Validator for teacher daily hour limits
 * Ensures teacher does not exceed maxDailyHours on any single day
 */
@Component
public class TeacherDailyHoursValidator {
    
    @Autowired
    private CourseSectionMeetingRepository repository;
    
    public void validate(CourseSectionMeeting meeting) {
        Teacher teacher = meeting.getSection().getTeacher();
        if (teacher == null || teacher.getMaxDailyHours() == null) {
            return;  // No limit defined
        }
        
        int maxDaily = teacher.getMaxDailyHours();
        
        // Get all meetings for this teacher on this day of week
        List<CourseSectionMeeting> dailyMeetings = repository.findBySection_TeacherAndDayOfWeek(
            teacher,
            meeting.getDayOfWeek()
        );
        // Exclude current meeting if updating
        if (meeting.getId() != null) {
            dailyMeetings = dailyMeetings.stream()
                .filter(m -> !m.getId().equals(meeting.getId()))
                .toList();
        }
        
        // Calculate total minutes from existing meetings on this day
        long totalMinutesOnDay = dailyMeetings.stream()
            .mapToLong(m -> Duration.between(m.getStartTime(), m.getEndTime()).toMinutes())
            .sum();
        
        // Add new meeting
        long newMeetingMinutes = Duration.between(meeting.getStartTime(), meeting.getEndTime()).toMinutes();
        long totalWithNew = totalMinutesOnDay + newMeetingMinutes;
        
        int totalHoursOnDay = (int) Math.ceil(totalWithNew / 60.0);
        
        if (totalHoursOnDay > maxDaily) {
            throw new ScheduleConflictException(
                "Teacher would exceed maximum daily hours (" + totalHoursOnDay + 
                " > " + maxDaily + ") on " + meeting.getDayOfWeekEnum()
            );
        }
    }
}
