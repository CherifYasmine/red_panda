package com.maplewood.student.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maplewood.common.dto.StudentCourseHistoryDTO;
import com.maplewood.common.mapper.StudentCourseHistoryMapper;
import com.maplewood.student.entity.Student;
import com.maplewood.student.entity.StudentCourseHistory;
import com.maplewood.student.service.StudentCourseHistoryService;
import com.maplewood.student.service.StudentService;

import jakarta.validation.Valid;

/**
 * REST Controller for StudentCourseHistory endpoints
 * Provides CRUD operations for student academic records
 */
@RestController
@RequestMapping("/api/v1/students/{studentId}/course-history")
public class StudentCourseHistoryController {
    
    @Autowired
    private StudentCourseHistoryService courseHistoryService;
    
    @Autowired
    private StudentService studentService;
    
    /**
     * Get all course histories (global)
     */
    @GetMapping("/_all")
    public ResponseEntity<List<StudentCourseHistoryDTO>> getAllCourseHistories() {
        return ResponseEntity.ok(courseHistoryService.getAllCourseHistories()
            .stream()
            .map(StudentCourseHistoryMapper::toDTO)
            .collect(Collectors.toList()));
    }
    
    /**
     * Get course history by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<StudentCourseHistoryDTO> getCourseHistoryById(
        @PathVariable Long studentId,
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(StudentCourseHistoryMapper.toDTO(courseHistoryService.getCourseHistoryById(id)));
    }
    
    /**
     * Get all courses taken by a student
     */
    @GetMapping
    public ResponseEntity<List<StudentCourseHistoryDTO>> getCourseHistoryByStudent(@PathVariable Long studentId) {
        Student student = studentService.getStudentById(studentId);
        return ResponseEntity.ok(courseHistoryService.getCourseHistoryByStudent(student)
            .stream()
            .map(StudentCourseHistoryMapper::toDTO)
            .collect(Collectors.toList()));
    }
    
    /**
     * Get all passed courses for a student (for prerequisite validation)
     */
    @GetMapping("/passed")
    public ResponseEntity<List<StudentCourseHistoryDTO>> getPassedCoursesForStudent(@PathVariable Long studentId) {
        Student student = studentService.getStudentById(studentId);
        return ResponseEntity.ok(courseHistoryService.getPassedCoursesForStudent(student)
            .stream()
            .map(StudentCourseHistoryMapper::toDTO)
            .collect(Collectors.toList()));
    }
    
    /**
     * Create new course history record
     */
    @PostMapping
    public ResponseEntity<StudentCourseHistoryDTO> createCourseHistory(
        @PathVariable Long studentId,
        @Valid @RequestBody StudentCourseHistoryDTO historyDTO
    ) {
        Student student = studentService.getStudentById(studentId);
        StudentCourseHistory entity = StudentCourseHistoryMapper.toEntity(historyDTO);
        entity.setStudent(student);
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(StudentCourseHistoryMapper.toDTO(courseHistoryService.createCourseHistory(entity)));
    }
    
    // /**
    //  * Update course history
    //  */
    // @PutMapping("/{id}")
    // public ResponseEntity<StudentCourseHistoryDTO> updateCourseHistory(
    //     @PathVariable Long studentId,
    //     @PathVariable Long id,
    //     @Valid @RequestBody StudentCourseHistoryDTO historyDTO
    // ) {
    //     StudentCourseHistory entity = StudentCourseHistoryMapper.toEntity(historyDTO);
    //     return ResponseEntity.ok(StudentCourseHistoryMapper.toDTO(courseHistoryService.updateCourseHistory(id, entity)));
    // }
    
    // /**
    //  * Delete course history
    //  */
    // @DeleteMapping("/{id}")
    // public ResponseEntity<Void> deleteCourseHistory(
    //     @PathVariable Long studentId,
    //     @PathVariable Long id
    // ) {
    //     courseHistoryService.deleteCourseHistory(id);
    //     return ResponseEntity.noContent().build();
    // }
}
