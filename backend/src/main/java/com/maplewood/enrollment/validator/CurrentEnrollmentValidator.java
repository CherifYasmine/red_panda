package com.maplewood.enrollment.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maplewood.enrollment.entity.CurrentEnrollment;
import com.maplewood.enrollment.validator.enrollment.AlreadyCompletedValidator;
import com.maplewood.enrollment.validator.enrollment.CapacityValidator;
import com.maplewood.enrollment.validator.enrollment.CourseLimitValidator;
import com.maplewood.enrollment.validator.enrollment.DuplicateCourseValidator;
import com.maplewood.enrollment.validator.enrollment.GradeLevelValidator;
import com.maplewood.enrollment.validator.enrollment.PrerequisiteValidator;
import com.maplewood.enrollment.validator.enrollment.ScheduleConflictEnrollmentValidator;

/**
 * Orchestrator for CurrentEnrollment validations
 * Delegates to specialized validators for each validation rule
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
    private DuplicateCourseValidator duplicateCourseValidator;
    
    @Autowired
    private AlreadyCompletedValidator alreadyCompletedValidator;
    
    @Autowired
    private GradeLevelValidator gradeLevelValidator;
    
    @Autowired
    private CapacityValidator capacityValidator;
    
    @Autowired
    private CourseLimitValidator courseLimitValidator;
    
    @Autowired
    private PrerequisiteValidator prerequisiteValidator;
    
    @Autowired
    private ScheduleConflictEnrollmentValidator scheduleConflictEnrollmentValidator;
    
    /**
     * Main validation method - delegates to all specialized validators
     */
    public void validate(CurrentEnrollment enrollment) {
        duplicateCourseValidator.validate(enrollment);
        alreadyCompletedValidator.validate(enrollment);
        gradeLevelValidator.validate(enrollment);
        capacityValidator.validate(enrollment);
        courseLimitValidator.validate(enrollment);
        prerequisiteValidator.validate(enrollment);
        scheduleConflictEnrollmentValidator.validate(enrollment);
    }
}
