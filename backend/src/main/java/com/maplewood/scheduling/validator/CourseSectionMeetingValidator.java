package com.maplewood.scheduling.validator;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maplewood.scheduling.entity.CourseSection;
import com.maplewood.scheduling.entity.CourseSectionMeeting;
import com.maplewood.scheduling.repository.CourseSectionMeetingRepository;
import com.maplewood.school.entity.Teacher;

/**
 * Validator for CourseSectionMeeting
 * Enforces all business rules for meeting creation/update
 * 
 * Validations (in order):
 * 1. Uniqueness - No duplicate meetings for same section/day/time
 * 2. Time Window - Start < End
 * 3. Hours Validation - Total meeting hours <= course.hoursPerWeek
 * 4. Schedule Conflicts - No teacher or classroom conflicts
 * 5. Teacher Daily Hours - Teacher daily hours <= maxDailyHours
 */
@Component
public class CourseSectionMeetingValidator {
    
    @Autowired
    private CourseSectionMeetingRepository repository;
    
    /**
     * Main validation method - runs all validations
     */
    public void validate(CourseSectionMeeting meeting) {
        validateUniqueness(meeting);
        validateTimeWindow(meeting);
        validateHours(meeting);
        validateScheduleConflicts(meeting);
        validateTeacherDailyHours(meeting);
    }
    
    /**
     * VALIDATION 1: Uniqueness Check
     * Ensures same meeting time doesn't already exist for this section
     */
    private void validateUniqueness(CourseSectionMeeting meeting) {
        if (meeting.getSection() == null || meeting.getSection().getId() == null) {
            throw new IllegalArgumentException("Section must be provided");
        }
        
        boolean exists = repository.existsBySection_IdAndDayOfWeekAndStartTime(
            meeting.getSection().getId(),
            meeting.getDayOfWeek(),
            meeting.getStartTime()
        );
        
        if (exists) {
            throw new IllegalArgumentException(
                "Meeting already exists for this section on day " + meeting.getDayOfWeek() + 
                " at " + meeting.getStartTime()
            );
        }
    }
    
    /**
     * VALIDATION 2: Time Window Validation
     * - Start time must be before end time
     */
    private void validateTimeWindow(CourseSectionMeeting meeting) {
        LocalTime start = meeting.getStartTime();
        LocalTime end = meeting.getEndTime();
        
        // Start < End
        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
    }
    
    /**
     * VALIDATION 3: Hours Validation (CRITICAL)
     * Total hours of all meetings in this section <= course.hoursPerWeek
     */
    private void validateHours(CourseSectionMeeting meeting) {
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
    
    /**
     * VALIDATION 4: Schedule Conflicts
     * - Teacher cannot teach 2 sections at same time
     * - Classroom cannot have 2 sections at same time
     */
    private void validateScheduleConflicts(CourseSectionMeeting meeting) {
        if (meeting.getSection() == null) {
            throw new IllegalArgumentException("Section must be provided");
        }
        
        // Check teacher conflicts
        Teacher teacher = meeting.getSection().getTeacher();
        if (teacher != null) {
            List<CourseSectionMeeting> teacherMeetings = repository.findBySection_Teacher(teacher);
            boolean hasConflict = teacherMeetings.stream()
                .anyMatch(m -> m.overlaps(meeting));
            
            if (hasConflict) {
                throw new IllegalArgumentException(
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
            boolean hasConflict = classroomMeetings.stream()
                .anyMatch(m -> m.overlaps(meeting));
            
            if (hasConflict) {
                throw new IllegalArgumentException(
                    "Classroom " + meeting.getSection().getClassroom().getName() + 
                    " is already booked at this time"
                );
            }
        }
    }
    
    /**
     * VALIDATION 5: Teacher Max Daily Hours
     * Teacher cannot exceed maxDailyHours on any single day
     */
    private void validateTeacherDailyHours(CourseSectionMeeting meeting) {
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
        
        // Calculate total minutes from existing meetings on this day
        long totalMinutesOnDay = dailyMeetings.stream()
            .mapToLong(m -> Duration.between(m.getStartTime(), m.getEndTime()).toMinutes())
            .sum();
        
        // Add new meeting
        long newMeetingMinutes = Duration.between(meeting.getStartTime(), meeting.getEndTime()).toMinutes();
        long totalWithNew = totalMinutesOnDay + newMeetingMinutes;
        
        int totalHoursOnDay = (int) Math.ceil(totalWithNew / 60.0);
        
        if (totalHoursOnDay > maxDaily) {
            throw new IllegalArgumentException(
                "Teacher would exceed maximum daily hours (" + totalHoursOnDay + 
                " > " + maxDaily + ") on day " + meeting.getDayOfWeek()
            );
        }
    }
}
