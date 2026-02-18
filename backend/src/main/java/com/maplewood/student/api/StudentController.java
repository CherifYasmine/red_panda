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

import com.maplewood.common.dto.StudentDTO;
import com.maplewood.common.enums.StudentStatus;
import com.maplewood.common.mapper.StudentMapper;
import com.maplewood.common.util.DTOConverter;
import com.maplewood.student.entity.Student;
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
    
    /**
     * Get all students
     */
    @GetMapping
    public ResponseEntity<List<StudentDTO>> getAllStudents() {
        return ResponseEntity.ok(DTOConverter.convertList(studentService.getAllStudents(), StudentMapper::toDTO));
    }
    
    /**
     * Get student by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(DTOConverter.convert(studentService.getStudentById(id), StudentMapper::toDTO));
    }
    
    /**
     * Get student by email
     */
    @GetMapping("/search/email")
    public ResponseEntity<StudentDTO> getStudentByEmail(@RequestParam String email) {
        return ResponseEntity.ok(DTOConverter.convert(studentService.getStudentByEmail(email), StudentMapper::toDTO));
    }
    
    /**
     * Get student by first and last name
     */
    @GetMapping("/search/name")
    public ResponseEntity<StudentDTO> getStudentByName(
        @RequestParam String firstName,
        @RequestParam String lastName
    ) {
        return ResponseEntity.ok(DTOConverter.convert(studentService.getStudentByName(firstName, lastName), StudentMapper::toDTO));
    }
    
    /**
     * Get students by first name
     */
    @GetMapping("/search/first-name")
    public ResponseEntity<List<StudentDTO>> getStudentsByFirstName(@RequestParam String firstName) {
        return ResponseEntity.ok(DTOConverter.convertList(studentService.getStudentsByFirstName(firstName), StudentMapper::toDTO));
    }
    
    /**
     * Get students by grade level
     */
    @GetMapping("/grade-level/{gradeLevel}")
    public ResponseEntity<List<StudentDTO>> getStudentsByGradeLevel(@PathVariable Integer gradeLevel) {
        return ResponseEntity.ok(DTOConverter.convertList(studentService.getStudentsByGradeLevel(gradeLevel), StudentMapper::toDTO));
    }
    
    /**
     * Get students by status (ACTIVE, INACTIVE, GRADUATED)
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<StudentDTO>> getStudentsByStatus(@PathVariable StudentStatus status) {
        return ResponseEntity.ok(DTOConverter.convertList(studentService.getStudentsByStatus(status), StudentMapper::toDTO));
    }
    
    /**
     * Create new student
     */
    @PostMapping
    public ResponseEntity<StudentDTO> createStudent(@Valid @RequestBody StudentDTO studentDTO) {
        Student entity = StudentMapper.toEntity(studentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(StudentMapper.toDTO(studentService.createStudent(entity)));
    }
    
    /**
     * Update student
     */
    @PutMapping("/{id}")
    public ResponseEntity<StudentDTO> updateStudent(@PathVariable Long id, @Valid @RequestBody StudentDTO studentDTO) {
        Student entity = StudentMapper.toEntity(studentDTO);
        return ResponseEntity.ok(StudentMapper.toDTO(studentService.updateStudent(id, entity)));
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
