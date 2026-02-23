package com.maplewood.course.validator.courseSectionMeeting;

import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maplewood.course.entity.CourseSection;
import com.maplewood.course.entity.CourseSectionMeeting;
import com.maplewood.course.repository.CourseSectionMeetingRepository;

/**
 * Validator for meeting hours totals
 * Ensures total meeting hours matches course.hoursPerWeek requirement
 */
@Component
public class HoursValidationValidator {
    
    @Autowired
    private CourseSectionMeetingRepository repository;
    
    public void validate(CourseSectionMeeting meeting) {
        if (meeting.getSection() == null) {
            throw new IllegalArgumentException("Section must be provided");
        }
        
        CourseSection section = meeting.getSection();
        if (section.getCourse() == null) {
            throw new IllegalArgumentException("Section must have a course");
        }
        
        Integer maxHoursPerWeek = section.getCourse().getHoursPerWeek();
        if (maxHoursPerWeek == null) {
            throw new IllegalArgumentException("Course must have hoursPerWeek defined");
        }
        
        // Get all existing meetings for this section
        List<CourseSectionMeeting> existingMeetings = repository.findBySection(section);
        
        // For updates, exclude the current meeting being updated from the total
        // (so we don't double-count it)
        if (meeting.getId() != null) {
            existingMeetings = existingMeetings.stream()
                .filter(m -> !m.getId().equals(meeting.getId()))
                .toList();
        }
        
        // Calculate total minutes from existing meetings
        long totalMinutes = existingMeetings.stream()
            .mapToLong(m -> Duration.between(m.getStartTime(), m.getEndTime()).toMinutes())
            .sum();
        
        // Add new meeting duration
        long newMeetingMinutes = Duration.between(meeting.getStartTime(), meeting.getEndTime()).toMinutes();
        long totalWithNew = totalMinutes + newMeetingMinutes;
        
        // Convert to hours
        int totalHours = (int) Math.ceil(totalWithNew / 60.0);
        
        if (totalHours > maxHoursPerWeek) {
            throw new IllegalArgumentException(
                "Total meeting hours (" + totalHours + ") would exceed course requirement (" + 
                maxHoursPerWeek + " hours/week)"
            );
        }
    }
}
