package com.maplewood.course.validator.courseSectionMeeting;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maplewood.course.entity.CourseSectionMeeting;
import com.maplewood.course.repository.CourseSectionMeetingRepository;

/**
 * Validator for uniqueness of meeting times
 * Ensures no duplicate meetings for same section/day/time
 */
@Component
public class UniquenessValidator {
    
    @Autowired
    private CourseSectionMeetingRepository repository;
    
    public void validate(CourseSectionMeeting meeting) {
        if (meeting.getSection() == null || meeting.getSection().getId() == null) {
            throw new IllegalArgumentException("Section must be provided");
        }
        
        List<CourseSectionMeeting> conflictingMeetings = repository.findBySection_IdAndDayOfWeekAndStartTime(
            meeting.getSection().getId(),
            meeting.getDayOfWeek(),
            meeting.getStartTime()
        );
        
        if (meeting.getId() != null) {
            conflictingMeetings = conflictingMeetings.stream()
                .filter(m -> !m.getId().equals(meeting.getId()))
                .toList();
        }
        
        if (!conflictingMeetings.isEmpty()) {
            throw new IllegalArgumentException(
                "Meeting already exists for this section on " + meeting.getDayOfWeekEnum() + 
                " at " + meeting.getStartTime()
            );
        }
    }
}
