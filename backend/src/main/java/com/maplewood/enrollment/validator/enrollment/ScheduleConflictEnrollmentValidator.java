package com.maplewood.enrollment.validator.enrollment;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maplewood.common.exception.ScheduleConflictException;
import com.maplewood.course.entity.CourseSectionMeeting;
import com.maplewood.course.repository.CourseSectionMeetingRepository;
import com.maplewood.enrollment.entity.CurrentEnrollment;
import com.maplewood.enrollment.repository.CurrentEnrollmentRepository;
import com.maplewood.student.entity.Student;

/**
 * Validator for student schedule conflicts
 * Ensures new meeting times don't overlap with already enrolled courses
 */
@Component
public class ScheduleConflictEnrollmentValidator {
    
    @Autowired
    private CurrentEnrollmentRepository enrollmentRepository;
    
    @Autowired
    private CourseSectionMeetingRepository meetingRepository;
    
    public void validate(CurrentEnrollment enrollment) {
        if (enrollment.getStudent() == null || enrollment.getCourseSection() == null) {
            throw new IllegalArgumentException("Student and section must be provided");
        }
        
        Student student = enrollment.getStudent();
        var newSection = enrollment.getCourseSection();
        
        // Get all meetings for the new section
        List<CourseSectionMeeting> newMeetings = meetingRepository.findBySection(newSection);
        
        if (newMeetings.isEmpty()) {
            return;  // No meetings scheduled yet
        }
        
        // Get all student's current enrollments in the same semester
        List<CurrentEnrollment> currentEnrollments = enrollmentRepository.findByStudent(student);
        
        // Filter to same semester
        Long semesterId = newSection.getSemester().getId();
        currentEnrollments = currentEnrollments.stream()
            .filter(e -> e.getCourseSection().getSemester().getId().equals(semesterId))
            .toList();
        
        // Check each existing enrollment for conflicts
        for (CurrentEnrollment existing : currentEnrollments) {
            List<CourseSectionMeeting> existingMeetings = meetingRepository.findBySection(existing.getCourseSection());
            
            // Check for any overlaps
            for (CourseSectionMeeting newMeeting : newMeetings) {
                for (CourseSectionMeeting existingMeeting : existingMeetings) {
                    if (newMeeting.overlaps(existingMeeting)) {
                        throw new ScheduleConflictException(
                            "Schedule conflict: Course " + existing.getCourseSection().getCourse().getName() + 
                            " meets at the same time"
                        );
                    }
                }
            }
        }
    }
}
