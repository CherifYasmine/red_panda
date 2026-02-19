package com.maplewood.enrollment.validator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maplewood.catalog.entity.Course;
import com.maplewood.common.enums.CourseHistoryStatus;
import com.maplewood.enrollment.entity.CurrentEnrollment;
import com.maplewood.enrollment.repository.CurrentEnrollmentRepository;
import com.maplewood.scheduling.entity.CourseSection;
import com.maplewood.scheduling.repository.CourseSectionMeetingRepository;
import com.maplewood.student.entity.Student;
import com.maplewood.student.repository.StudentCourseHistoryRepository;

/**
 * Validator for CurrentEnrollment
 * Enforces all business rules for student course enrollment
 * 
 * Validations (in order):
 * 1. Duplicate Course - Student not already enrolled in this course (any section) this semester
 * 2. Already Completed - Student cannot retake a course they've already passed
 * 3. Grade Level - Student's grade level within course's min/max range
 * 4. Capacity Check - Section hasn't reached capacity
 * 5. Course Limit - Student not exceeding 5 courses per semester
 * 6. Prerequisites - Student has passed all required prerequisites
 * 7. Schedule Conflicts - No overlap between enrolled meetings
 */
@Component
public class CurrentEnrollmentValidator {
    
    @Autowired
    private CurrentEnrollmentRepository enrollmentRepository;
    
    @Autowired
    private StudentCourseHistoryRepository courseHistoryRepository;
    
    @Autowired
    private CourseSectionMeetingRepository meetingRepository;
    
    /**
     * Main validation method - runs all validations
     */
    public void validate(CurrentEnrollment enrollment) {
        validateNoDuplicateCourse(enrollment);
        validateNotAlreadyCompleted(enrollment);
        validateGradeLevel(enrollment);
        validateCapacity(enrollment);
        validateCourseLimit(enrollment);
        validatePrerequisites(enrollment);
        validateScheduleConflicts(enrollment);
    }
    
    /**
     * VALIDATION 1: Duplicate Course Prevention
     * Ensures student not already enrolled in this course (any section) in this semester
     */
    private void validateNoDuplicateCourse(CurrentEnrollment enrollment) {
        if (enrollment.getStudent() == null || enrollment.getStudent().getId() == null) {
            throw new IllegalArgumentException("Student must be provided");
        }
        if (enrollment.getCourseSection() == null || enrollment.getCourseSection().getId() == null) {
            throw new IllegalArgumentException("Section must be provided");
        }
        if (enrollment.getCourseSection().getCourse() == null) {
            throw new IllegalArgumentException("Section must have course defined");
        }
        if (enrollment.getCourseSection().getSemester() == null) {
            throw new IllegalArgumentException("Section must have semester defined");
        }
        
        long courseCount = enrollmentRepository.countByStudent_IdAndCourse_IdAndSemester_Id(
            enrollment.getStudent().getId(),
            enrollment.getCourseSection().getCourse().getId(),
            enrollment.getCourseSection().getSemester().getId()
        );
        
        if (courseCount > 0) {
            throw new IllegalArgumentException(
                "Student is already enrolled in " + enrollment.getCourseSection().getCourse().getName() + 
                " (id: " + enrollment.getCourseSection().getCourse().getId() + ")" + " (CourseSection id: " + enrollment.getCourseSection().getId() + ") in this semester. Cannot take the same course twice per semester."
            );
        }
    }
    
    /**
     * VALIDATION 2: Already Completed
     * Ensures student cannot retake a course they've already passed in history
     */
    private void validateNotAlreadyCompleted(CurrentEnrollment enrollment) {
        if (enrollment.getStudent() == null || enrollment.getCourseSection() == null) {
            throw new IllegalArgumentException("Student and section must be provided");
        }
        
        if (enrollment.getCourseSection().getCourse() == null) {
            throw new IllegalArgumentException("Section must have course defined");
        }
        
        // Check if student has already passed this course
        boolean alreadyPassed = courseHistoryRepository.existsByStudentAndCourseAndStatus(
            enrollment.getStudent(),
            enrollment.getCourseSection().getCourse(),
            CourseHistoryStatus.PASSED
        );
        
        if (alreadyPassed) {
            throw new IllegalArgumentException(
                "Student has already completed " + enrollment.getCourseSection().getCourse().getName() + 
                ". Cannot retake a course that has been passed."
            );
        }
    }
    
    /**
     * VALIDATION 3: Grade Level
     * Ensures student's grade level is within course's min/max range
     */
    private void validateGradeLevel(CurrentEnrollment enrollment) {
        if (enrollment.getStudent() == null || enrollment.getCourseSection() == null) {
            throw new IllegalArgumentException("Student and section must be provided");
        }
        
        if (enrollment.getCourseSection().getCourse() == null) {
            throw new IllegalArgumentException("Section must have course defined");
        }
        
        Integer studentGradeLevel = enrollment.getStudent().getGradeLevel();
        Integer courseGradeMin = enrollment.getCourseSection().getCourse().getGradeLevelMin();
        Integer courseGradeMax = enrollment.getCourseSection().getCourse().getGradeLevelMax();
        
        if (studentGradeLevel == null) {
            throw new IllegalArgumentException("Student must have grade level defined");
        }
        if (courseGradeMin == null || courseGradeMax == null) {
            throw new IllegalArgumentException("Course must have grade level range defined");
        }
        
        if (studentGradeLevel < courseGradeMin || studentGradeLevel > courseGradeMax) {
            throw new IllegalArgumentException(
                "Student grade level " + studentGradeLevel + 
                " is not allowed for this course (requires grade " + courseGradeMin + "-" + courseGradeMax + ")"
            );
        }
    }
    
    /**
     * VALIDATION 4: Capacity Check
     * Ensures section hasn't reached maximum capacity
     */
    private void validateCapacity(CurrentEnrollment enrollment) {
        if (enrollment.getCourseSection() == null) {
            throw new IllegalArgumentException("Section must be provided");
        }
        
        CourseSection section = enrollment.getCourseSection();
        Integer capacity = section.getCapacity();
        if (capacity == null) {
            throw new IllegalArgumentException("Section must have capacity defined");
        }
        
        long currentEnrollments = enrollmentRepository.countByCourseSection(section);
        
        if (currentEnrollments >= capacity) {
            throw new IllegalArgumentException(
                "Section has reached maximum capacity (" + capacity + " students)"
            );
        }
    }
    
    /**
     * VALIDATION 5: Course Limit
     * Student cannot exceed 5 courses per semester
     */
    private void validateCourseLimit(CurrentEnrollment enrollment) {
        if (enrollment.getStudent() == null || enrollment.getCourseSection() == null) {
            throw new IllegalArgumentException("Student and section must be provided");
        }
        
        if (enrollment.getCourseSection().getSemester() == null) {
            throw new IllegalArgumentException("Section must have semester defined");
        }
        
        long currentCourses = enrollmentRepository.countByStudent_IdAndCourseSection_Semester_Id(
            enrollment.getStudent().getId(),
            enrollment.getCourseSection().getSemester().getId()
        );
        
        if (currentCourses >= 5) {
            throw new IllegalArgumentException(
                "Student cannot enroll in more than 5 courses per semester (already enrolled in 5)"
            );
        }
    }
    
    /**
     * VALIDATION 6: Prerequisites
     * Student must have passed all prerequisite courses
     * Prerequisite must be from same or earlier semester (semester_order logic)
     */
    private void validatePrerequisites(CurrentEnrollment enrollment) {
        if (enrollment.getStudent() == null || enrollment.getCourseSection() == null) {
            throw new IllegalArgumentException("Student and section must be provided");
        }
        
        Course course = enrollment.getCourseSection().getCourse();
        if (course == null) {
            throw new IllegalArgumentException("Section must have course defined");
        }
        
        // If no prerequisite, validation passes
        if (course.getPrerequisite() == null) {
            return;
        }
        
        // Check if student has passed the prerequisite
        boolean hasPrerequisite = courseHistoryRepository.existsByStudentAndCourseAndStatus(
            enrollment.getStudent(),
            course.getPrerequisite(),
            CourseHistoryStatus.PASSED
        );
        
        if (!hasPrerequisite) {
            throw new IllegalArgumentException(
                "Student has not completed prerequisite: " + course.getPrerequisite().getName() + "(code: " + course.getPrerequisite().getCode() + ")"
            );
        }
        
        // BONUS: Validate semester ordering
        // Prerequisite course's semester_order should be <= current course's semester_order
        // This prevents illogical scheduling like Fall course requiring Spring prerequisite
        if (course.getSemesterOrder() != null && course.getPrerequisite().getSemesterOrder() != null) {
            if (course.getPrerequisite().getSemesterOrder() > course.getSemesterOrder()) {
                throw new IllegalArgumentException(
                    "Prerequisite " + course.getPrerequisite().getName() + " is scheduled for later semester. " +
                    "Prerequisites must be from same or earlier semester."
                );
            }
        }
    }
    
    /**
     * VALIDATION 7: Schedule Conflicts
     * Student's new meeting times cannot overlap with already enrolled courses
     */
    private void validateScheduleConflicts(CurrentEnrollment enrollment) {
        if (enrollment.getStudent() == null || enrollment.getCourseSection() == null) {
            throw new IllegalArgumentException("Student and section must be provided");
        }
        
        Student student = enrollment.getStudent();
        CourseSection newSection = enrollment.getCourseSection();
        
        // Get all meetings for the new section
        List<com.maplewood.scheduling.entity.CourseSectionMeeting> newMeetings = 
            meetingRepository.findBySection(newSection);
        
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
            List<com.maplewood.scheduling.entity.CourseSectionMeeting> existingMeetings = 
                meetingRepository.findBySection(existing.getCourseSection());
            
            // Check for any overlaps
            for (com.maplewood.scheduling.entity.CourseSectionMeeting newMeeting : newMeetings) {
                for (com.maplewood.scheduling.entity.CourseSectionMeeting existingMeeting : existingMeetings) {
                    if (newMeeting.overlaps(existingMeeting)) {
                        throw new IllegalArgumentException(
                            "Schedule conflict: " + existing.getCourseSection().getCourse().getName() + 
                            " meets at the same time"
                        );
                    }
                }
            }
        }
    }
}
