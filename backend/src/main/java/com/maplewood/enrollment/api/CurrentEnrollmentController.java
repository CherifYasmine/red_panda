package com.maplewood.enrollment.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maplewood.common.dto.CreateEnrollmentDTO;
import com.maplewood.common.dto.EnrollmentDTO;
import com.maplewood.common.dto.UpdateEnrollmentDTO;
import com.maplewood.enrollment.service.CurrentEnrollmentService;

import jakarta.validation.Valid;

/**
 * REST API for managing current semester enrollments
 * Provides endpoints for student enrollment operations with validation
 */
@RestController
@RequestMapping("/api/v1/enrollments")
public class CurrentEnrollmentController {
    
    @Autowired
    private CurrentEnrollmentService enrollmentService;
    
    /**
     * POST /api/v1/enrollments
     * Create a new enrollment
     * 
     * Validations:
     * - Student exists and is valid
     * - Section exists and has capacity
     * - Student not already enrolled
     * - Student meets prerequisites
     * - No schedule conflicts
     * - Under 5 course limit
     */
    @PostMapping
    public ResponseEntity<EnrollmentDTO> createEnrollment(@Valid @RequestBody CreateEnrollmentDTO createDTO) {
        EnrollmentDTO enrollment = enrollmentService.createEnrollmentFromDTO(createDTO);
        return new ResponseEntity<>(enrollment, HttpStatus.CREATED);
    }
    
    /**
     * PUT /api/v1/enrollments/{id}
     * Update an existing enrollment
     */
    @PutMapping("/{id}")
    public ResponseEntity<EnrollmentDTO> updateEnrollment(
        @PathVariable Long id,
        @Valid @RequestBody UpdateEnrollmentDTO updateDTO) {
        EnrollmentDTO enrollment = enrollmentService.updateEnrollmentFromDTO(id, updateDTO);
        return ResponseEntity.ok(enrollment);
    }
    
    /**
     * GET /api/v1/enrollments/{id}
     * Get enrollment by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentDTO> getEnrollment(@PathVariable Long id) {
        EnrollmentDTO enrollment = enrollmentService.getEnrollmentById(id);
        return ResponseEntity.ok(enrollment);
    }
    
    /**
     * DELETE /api/v1/enrollments/{id}
     * Delete an enrollment (student drops course)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnrollment(@PathVariable Long id) {
        enrollmentService.deleteEnrollment(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * GET /api/v1/enrollments/student/{studentId}
     * Get all enrollments for a student
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<EnrollmentDTO>> getEnrollmentsByStudent(@PathVariable Long studentId) {
        List<EnrollmentDTO> enrollments = enrollmentService.getEnrollmentsByStudentId(studentId);
        return ResponseEntity.ok(enrollments);
    }
    
    /**
     * GET /api/v1/enrollments/section/{sectionId}
     * Get all enrollments for a section
     */
    @GetMapping("/section/{sectionId}")
    public ResponseEntity<List<EnrollmentDTO>> getEnrollmentsBySection(@PathVariable Long sectionId) {
        List<EnrollmentDTO> enrollments = enrollmentService.getEnrollmentsBySectionId(sectionId);
        return ResponseEntity.ok(enrollments);
    }
    
    /**
     * GET /api/v1/enrollments/check?studentId=X&sectionId=Y
     * Check if student can enroll in section
     */
    @GetMapping("/check")
    public ResponseEntity<Boolean> checkCanEnroll(Long studentId, Long sectionId) {
        boolean canEnroll = enrollmentService.canEnroll(studentId, sectionId);
        return ResponseEntity.ok(canEnroll);
    }
}
