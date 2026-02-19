package com.maplewood.enrollment.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maplewood.common.dto.CreateEnrollmentDTO;
import com.maplewood.common.dto.EnrollmentDTO;
import com.maplewood.common.dto.UpdateEnrollmentDTO;
import com.maplewood.enrollment.entity.CurrentEnrollment;
import com.maplewood.enrollment.mapper.CurrentEnrollmentMapper;
import com.maplewood.enrollment.repository.CurrentEnrollmentRepository;
import com.maplewood.enrollment.validator.CurrentEnrollmentValidator;
import com.maplewood.scheduling.entity.CourseSection;
import com.maplewood.scheduling.repository.CourseSectionRepository;
import com.maplewood.student.entity.Student;
import com.maplewood.student.repository.StudentRepository;

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
    private StudentRepository studentRepository;
    
    @Autowired
    private CurrentEnrollmentValidator validator;
    
    /**
     * Create a new enrollment from DTO
     * Validates prerequisites, capacity, schedule conflicts, etc.
     */
    public EnrollmentDTO createEnrollmentFromDTO(CreateEnrollmentDTO createDTO) {
        // Load dependencies
        Student student = studentRepository.findById(createDTO.studentId())
            .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + createDTO.studentId()));
        
        CourseSection section = sectionRepository.findById(createDTO.sectionId())
            .orElseThrow(() -> new IllegalArgumentException("Section not found with ID: " + createDTO.sectionId()));
        
        // Create entity from DTO
        CurrentEnrollment enrollment = CurrentEnrollmentMapper.toEntityFromCreate(createDTO, student, section);
        
        // Validate all business rules
        validator.validate(enrollment);
        
        // Save and return
        CurrentEnrollment saved = enrollmentRepository.save(enrollment);
        return CurrentEnrollmentMapper.toDTO(saved);
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
    public void deleteEnrollment(Long enrollmentId) {
        CurrentEnrollment enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow(() -> new IllegalArgumentException("Enrollment not found with ID: " + enrollmentId));
        
        enrollmentRepository.delete(enrollment);
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
