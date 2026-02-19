package com.maplewood.scheduling.validator;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maplewood.common.enums.CourseType;
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
 * 3. No Lunch Hour - Meetings cannot overlap with 12:00 PM - 1:00 PM
 * 4. Course Hours Type - Core courses 4-6 hours/week, Elective 2-4 hours/week
 * 5. Hours Validation - Total meeting hours matches course.hoursPerWeek
 * 6. Schedule Conflicts - No teacher or classroom conflicts
 * 7. Teacher Daily Hours - Teacher daily hours <= maxDailyHours
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
        validateNoLunchHour(meeting);
        validateCourseHoursType(meeting);
        validateHours(meeting);
        validateScheduleConflicts(meeting);
        validateTeacherDailyHours(meeting);
    }
    
    /**
     * VALIDATION 1: Uniqueness Check
     * Ensures same meeting time doesn't already exist for this section
     * On update, excludes the current meeting from the check
     */
    private void validateUniqueness(CourseSectionMeeting meeting) {
        if (meeting.getSection() == null || meeting.getSection().getId() == null) {
            throw new IllegalArgumentException("Section must be provided");
        }
        
        // Get all existing meetings with same day and start time
        List<CourseSectionMeeting> conflictingMeetings = repository.findBySection_IdAndDayOfWeekAndStartTime(
            meeting.getSection().getId(),
            meeting.getDayOfWeek(),
            meeting.getStartTime()
        );
        
        // For updates, exclude the current meeting being updated
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
     * VALIDATION 3: No Lunch Hour Meetings
     * - Classes cannot be scheduled during lunch hour (12:00 PM - 1:00 PM)
     */
    private void validateNoLunchHour(CourseSectionMeeting meeting) {
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
    
    /**
     * VALIDATION 4: Course Hours Type Validation
     * - Core courses must be 4-6 hours per week
     * - Elective courses must be 2-4 hours per week
     */
    private void validateCourseHoursType(CourseSectionMeeting meeting) {
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
    
    /**
     * VALIDATION 5: Hours Validation (CRITICAL)
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
    
    /**
     * VALIDATION 5: Schedule Conflicts
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
            // Exclude current meeting if updating
            if (meeting.getId() != null) {
                teacherMeetings = teacherMeetings.stream()
                    .filter(m -> !m.getId().equals(meeting.getId()))
                    .toList();
            }
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
            // Exclude current meeting if updating
            if (meeting.getId() != null) {
                classroomMeetings = classroomMeetings.stream()
                    .filter(m -> !m.getId().equals(meeting.getId()))
                    .toList();
            }
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
     * VALIDATION 6: Teacher Max Daily Hours
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
            throw new IllegalArgumentException(
                "Teacher would exceed maximum daily hours (" + totalHoursOnDay + 
                " > " + maxDaily + ") on " + meeting.getDayOfWeekEnum()
            );
        }
    }
}
