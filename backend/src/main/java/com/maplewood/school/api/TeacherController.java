package com.maplewood.school.api;

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

import com.maplewood.school.entity.Teacher;
import com.maplewood.school.service.TeacherService;

/**
 * REST controller for Teacher endpoints
 * Provides CRUD operations for teachers
 */
@RestController
@RequestMapping("/api/v1/teachers")
@CrossOrigin(origins = "*")
public class TeacherController {
    
    @Autowired
    private TeacherService teacherService;
    
    /**
     * GET all teachers
     */
    @GetMapping
    public ResponseEntity<List<Teacher>> getAllTeachers() {
        return ResponseEntity.ok(teacherService.getAllTeachers());
    }
    
    /**
     * GET teacher by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Teacher> getTeacherById(@PathVariable Long id) {
        return ResponseEntity.ok(teacherService.getTeacherById(id));
    }
    
    /**
     * GET teacher by email
     */
    @GetMapping("/search/email")
    public ResponseEntity<Teacher> getTeacherByEmail(@RequestParam String email) {
        return ResponseEntity.ok(teacherService.getTeacherByEmail(email));
    }
    
    /**
     * GET teachers by specialization
     */
    @GetMapping("/specialization/{specializationId}")
    public ResponseEntity<List<Teacher>> getTeachersBySpecialization(@PathVariable Long specializationId) {
        return ResponseEntity.ok(teacherService.getTeachersBySpecialization(specializationId));
    }
    
    /**
     * POST create new teacher
     */
    @PostMapping
    public ResponseEntity<Teacher> createTeacher(@RequestBody Teacher teacher) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teacherService.createTeacher(teacher));
    }
    
    /**
     * PUT update teacher
     */
    @PutMapping("/{id}")
    public ResponseEntity<Teacher> updateTeacher(@PathVariable Long id, @RequestBody Teacher teacher) {
        return ResponseEntity.ok(teacherService.updateTeacher(id, teacher));
    }
    
    /**
     * DELETE teacher
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.noContent().build();
    }
}
