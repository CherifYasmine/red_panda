package com.maplewood.student.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.maplewood.common.dto.AcademicMetricsDTO;
import com.maplewood.common.dto.StudentDTO;
import com.maplewood.common.enums.StudentStatus;
import com.maplewood.common.mapper.StudentMapper;
import com.maplewood.student.entity.Student;
import com.maplewood.student.service.AcademicMetricsService;
import com.maplewood.student.service.StudentService;

import jakarta.validation.Valid;

/**
 * REST Controller for Student endpoints
 * Provides CRUD operations and student search functionality
 */
@RestController
@RequestMapping("/api/v1/students")
@CrossOrigin(origins = "*")
public class StudentController {
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private AcademicMetricsService academicMetricsService;
    
    /**
     * Enrich StudentDTO with academic metrics
     */
    private StudentDTO enrichWithMetrics(Student entity) {
        StudentDTO dto = StudentMapper.toDTO(entity);
        AcademicMetricsService.AcademicMetrics metrics = academicMetricsService.getMetrics(entity);
        dto.setAcademicMetrics(new AcademicMetricsDTO(
            metrics.getGpa(), 
            metrics.getCreditsEarned(),
            metrics.getRemainingCreditsToGraduate(),
            metrics.isGraduated()
        ));
        return dto;
    }
    
    /**
     * Get all students
     */
    @GetMapping
    public ResponseEntity<List<StudentDTO>> getAllStudents() {
        return ResponseEntity.ok(
            studentService.getAllStudents().stream()
                .map(this::enrichWithMetrics)
                .toList()
        );
    }
    
    /**
     * Get student by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable Long id) {
        Student student = studentService.getStudentById(id);
        return ResponseEntity.ok(enrichWithMetrics(student));
    }
    
    /**
     * Get student by email
     */
    @GetMapping("/search/email")
    public ResponseEntity<StudentDTO> getStudentByEmail(@RequestParam String email) {
        Student student = studentService.getStudentByEmail(email);
        return ResponseEntity.ok(enrichWithMetrics(student));
    }
    
    /**
     * Get student by first and last name
     */
    @GetMapping("/search/name")
    public ResponseEntity<StudentDTO> getStudentByName(
        @RequestParam String firstName,
        @RequestParam String lastName
    ) {
        Student student = studentService.getStudentByName(firstName, lastName);
        return ResponseEntity.ok(enrichWithMetrics(student));
    }
    
    /**
     * Get students by first name
     */
    @GetMapping("/search/first-name")
    public ResponseEntity<List<StudentDTO>> getStudentsByFirstName(@RequestParam String firstName) {
        return ResponseEntity.ok(
            studentService.getStudentsByFirstName(firstName).stream()
                .map(this::enrichWithMetrics)
                .toList()
        );
    }
    
    /**
     * Get students by grade level
     */
    @GetMapping("/grade-level/{gradeLevel}")
    public ResponseEntity<List<StudentDTO>> getStudentsByGradeLevel(@PathVariable Integer gradeLevel) {
        return ResponseEntity.ok(
            studentService.getStudentsByGradeLevel(gradeLevel).stream()
                .map(this::enrichWithMetrics)
                .toList()
        );
    }
    
    /**
     * Get students by status (ACTIVE, INACTIVE, GRADUATED)
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<StudentDTO>> getStudentsByStatus(@PathVariable StudentStatus status) {
        return ResponseEntity.ok(
            studentService.getStudentsByStatus(status).stream()
                .map(this::enrichWithMetrics)
                .toList()
        );
    }
    
    /**
     * Create new student
     */
    @PostMapping
    public ResponseEntity<StudentDTO> createStudent(@Valid @RequestBody StudentDTO studentDTO) {
        Student entity = StudentMapper.toEntity(studentDTO);
        Student created = studentService.createStudent(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(enrichWithMetrics(created));
    }
    
    /**
     * Update student
     */
    @PutMapping("/{id}")
    public ResponseEntity<StudentDTO> updateStudent(@PathVariable Long id, @Valid @RequestBody StudentDTO studentDTO) {
        Student entity = StudentMapper.toEntity(studentDTO);
        Student updated = studentService.updateStudent(id, entity);
        return ResponseEntity.ok(enrichWithMetrics(updated));
    }
    
    /**
     * Delete student
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}
