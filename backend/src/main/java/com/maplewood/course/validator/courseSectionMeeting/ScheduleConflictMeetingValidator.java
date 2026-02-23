package com.maplewood.course.validator.courseSectionMeeting;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maplewood.common.exception.ScheduleConflictException;
import com.maplewood.course.entity.CourseSectionMeeting;
import com.maplewood.course.repository.CourseSectionMeetingRepository;
import com.maplewood.school.entity.Teacher;

/**
 * Validator for schedule conflicts
 * Ensures teacher cannot teach 2 sections at same time
 * Ensures classroom cannot have 2 sections at same time
 */
@Component
public class ScheduleConflictMeetingValidator {
    
    @Autowired
    private CourseSectionMeetingRepository repository;
    
    public void validate(CourseSectionMeeting meeting) {
        if (meeting.getSection() == null) {
            throw new IllegalArgumentException("Section must be provided");
        }
        
        // Check teacher conflicts
        Teacher teacher = meeting.getSection().getTeacher();
        if (teacher != null) {
            List<CourseSectionMeeting> teacherMeetings = repository.findBySection_Teacher(teacher);
            // Exclude current meeting if updating
            if (meeting.getId() != null) {
                teacherMeetings = teacherMeetings.stream()
                    .filter(m -> !m.getId().equals(meeting.getId()))
                    .toList();
            }
            boolean hasConflict = teacherMeetings.stream()
                .anyMatch(m -> m.overlaps(meeting));
            
            if (hasConflict) {
                throw new ScheduleConflictException(
                    "Teacher " + teacher.getFirstName() + " " + teacher.getLastName() + 
                    " already has a conflicting meeting at this time"
                );
            }
        }
        
        // Check classroom conflicts
        if (meeting.getSection().getClassroom() != null) {
            List<CourseSectionMeeting> classroomMeetings = repository.findBySection_Classroom(
                meeting.getSection().getClassroom()
            );
            // Exclude current meeting if updating
            if (meeting.getId() != null) {
                classroomMeetings = classroomMeetings.stream()
                    .filter(m -> !m.getId().equals(meeting.getId()))
                    .toList();
            }
            boolean hasConflict = classroomMeetings.stream()
                .anyMatch(m -> m.overlaps(meeting));
            
            if (hasConflict) {
                throw new ScheduleConflictException(
                    "Classroom " + meeting.getSection().getClassroom().getName() + 
                    " is already booked at this time"
                );
            }
        }
    }
}
