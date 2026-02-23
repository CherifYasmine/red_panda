package com.maplewood.enrollment.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maplewood.common.dto.CreateEnrollmentDTO;
import com.maplewood.common.dto.EnrollmentDTO;
import com.maplewood.common.dto.UpdateEnrollmentDTO;
import com.maplewood.common.exception.ScheduleConflictException;
import com.maplewood.course.entity.CourseSection;
import com.maplewood.course.repository.CourseSectionRepository;
import com.maplewood.course.service.CourseSectionService;
import com.maplewood.enrollment.entity.CurrentEnrollment;
import com.maplewood.enrollment.mapper.CurrentEnrollmentMapper;
import com.maplewood.enrollment.repository.CurrentEnrollmentRepository;
import com.maplewood.enrollment.validator.CurrentEnrollmentValidator;
import com.maplewood.student.entity.Student;
import com.maplewood.student.repository.StudentRepository;

import jakarta.persistence.OptimisticLockException;

/**
 * Service for managing current semester enrollments
 * Handles enrollment creation, updates, and retrieval with validation
 */
@Service
public class CurrentEnrollmentService {
    
    @Autowired
    private CurrentEnrollmentRepository enrollmentRepository;
    
    @Autowired
    private CourseSectionRepository sectionRepository;
    
    @Autowired
    private CourseSectionService courseSectionService;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private CurrentEnrollmentValidator validator;
    
    /**
     * Create a new enrollment from DTO
     * Validates prerequisites, capacity, schedule conflicts, etc.
     * Handles concurrent enrollments with optimistic locking + retry
     * 
     * If 2+ students enroll simultaneously at capacity 1:
     * - Both pass validation (both see capacity = 1, enrollment_count = 0)
     * - First student saves → enrollment_count = 1, version = 2
     * - Second student retries → validation fails (capacity now exceeded)
     */
    @Transactional
    public EnrollmentDTO createEnrollmentFromDTO(CreateEnrollmentDTO createDTO) {
        return createEnrollmentWithRetry(createDTO, 0);
    }
    
    /**
     * Recursive retry logic for enrollment creation
     * Max 3 retries to handle transient OptimisticLockException
     */
    private EnrollmentDTO createEnrollmentWithRetry(CreateEnrollmentDTO createDTO, int attempt) {
        final int MAX_RETRIES = 3;
        
        try {
            // Load dependencies (fresh from DB each retry)
            Student student = studentRepository.findById(createDTO.studentId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + createDTO.studentId()));
            
            CourseSection section = sectionRepository.findById(createDTO.sectionId())
                .orElseThrow(() -> new IllegalArgumentException("Section not found with ID: " + createDTO.sectionId()));
            
            // Create entity from DTO
            CurrentEnrollment enrollment = CurrentEnrollmentMapper.toEntityFromCreate(createDTO, student, section);
            
            // Validate all business rules (may fail if another student enrolled simultaneously)
            validator.validate(enrollment);
            
            // Save enrollment first
            CurrentEnrollment saved = enrollmentRepository.save(enrollment);
            
            // Increment section enrollment count (triggers version update for optimistic locking)
            section.setEnrollmentCount(section.getEnrollmentCount() + 1);
            sectionRepository.save(section);  // Version incremented here
            
            return CurrentEnrollmentMapper.toDTO(saved);
            
        } catch (OptimisticLockException e) {
            // Version conflict: another transaction modified CourseSection
            if (attempt < MAX_RETRIES) {
                // Retry with fresh data
                return createEnrollmentWithRetry(createDTO, attempt + 1);
            } else {
                // Max retries exceeded
                throw new ScheduleConflictException(
                    "Section became unavailable. Too many concurrent enrollments. Please try again."
                );
            }
        }
    }
    
    /**
     * Update an existing enrollment
     * Can update grade after semester ends
     */
    public EnrollmentDTO updateEnrollmentFromDTO(Long enrollmentId, UpdateEnrollmentDTO updateDTO) {
        CurrentEnrollment enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow(() -> new IllegalArgumentException("Enrollment not found with ID: " + enrollmentId));
        
        CurrentEnrollmentMapper.updateFromDTO(updateDTO, enrollment);
        
        CurrentEnrollment saved = enrollmentRepository.save(enrollment);
        return CurrentEnrollmentMapper.toDTO(saved);
    }
    
    /**
     * Get enrollment by ID
     */
    public EnrollmentDTO getEnrollmentById(Long enrollmentId) {
        CurrentEnrollment enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow(() -> new IllegalArgumentException("Enrollment not found with ID: " + enrollmentId));
        return CurrentEnrollmentMapper.toDTO(enrollment);
    }
    
    /**
     * Get all enrollments for a student
     */
    public List<EnrollmentDTO> getEnrollmentsByStudentId(Long studentId) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId));
        
        List<CurrentEnrollment> enrollments = enrollmentRepository.findByStudent(student);
        return enrollments.stream()
            .map(CurrentEnrollmentMapper::toDTO)
            .toList();
    }
    
    /**
     * Get all enrollments for a section
     */
    public List<EnrollmentDTO> getEnrollmentsBySectionId(Long sectionId) {
        CourseSection section = sectionRepository.findById(sectionId)
            .orElseThrow(() -> new IllegalArgumentException("Section not found with ID: " + sectionId));
        
        List<CurrentEnrollment> enrollments = enrollmentRepository.findByCourseSection(section);
        return enrollments.stream()
            .map(CurrentEnrollmentMapper::toDTO)
            .toList();
    }
    
    /**
     * Delete an enrollment (student dropping a course)
     */
    @Transactional
    public void deleteEnrollment(Long enrollmentId) {
        CurrentEnrollment enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow(() -> new IllegalArgumentException("Enrollment not found with ID: " + enrollmentId));
        
        enrollmentRepository.delete(enrollment);

        // Decrement section enrollment count
        courseSectionService.decrementEnrollmentCount(enrollment.getCourseSection().getId());
    }
    
    /**
     * Check if student can enroll in a section
     * Returns true if all validations would pass
     */
    public boolean canEnroll(Long studentId, Long sectionId) {
        try {
            Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
            
            CourseSection section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new IllegalArgumentException("Section not found"));
            
            CurrentEnrollment enrollment = new CurrentEnrollment();
            enrollment.setStudent(student);
            enrollment.setCourseSection(section);
            
            validator.validate(enrollment);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
