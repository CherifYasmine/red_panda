package com.maplewood.school.api;

import java.util.List;
import java.util.stream.Collectors;

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

import com.maplewood.common.dto.TeacherDTO;
import com.maplewood.common.mapper.TeacherMapper;
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
    public ResponseEntity<List<TeacherDTO>> getAllTeachers() {
        return ResponseEntity.ok(teacherService.getAllTeachers()
            .stream()
            .map(TeacherMapper::toDTO)
            .collect(Collectors.toList()));
    }
    
    /**
     * GET teacher by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<TeacherDTO> getTeacherById(@PathVariable Long id) {
        return ResponseEntity.ok(TeacherMapper.toDTO(teacherService.getTeacherById(id)));
    }
    
    /**
     * GET teacher by email
     */
    @GetMapping("/search/email")
    public ResponseEntity<TeacherDTO> getTeacherByEmail(@RequestParam String email) {
        return ResponseEntity.ok(TeacherMapper.toDTO(teacherService.getTeacherByEmail(email)));
    }
    
    /**
     * GET teachers by specialization
     */
    @GetMapping("/specialization/{specializationId}")
    public ResponseEntity<List<TeacherDTO>> getTeachersBySpecialization(@PathVariable Long specializationId) {
        return ResponseEntity.ok(teacherService.getTeachersBySpecialization(specializationId)
            .stream()
            .map(TeacherMapper::toDTO)
            .collect(Collectors.toList()));
    }
    
    /**
     * POST create new teacher
     */
    @PostMapping
    public ResponseEntity<TeacherDTO> createTeacher(@RequestBody TeacherDTO teacherDTO) {
        Teacher entity = TeacherMapper.toEntity(teacherDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(TeacherMapper.toDTO(teacherService.createTeacher(entity)));
    }
    
    /**
     * PUT update teacher
     */
    @PutMapping("/{id}")
    public ResponseEntity<TeacherDTO> updateTeacher(@PathVariable Long id, @RequestBody TeacherDTO teacherDTO) {
        Teacher entity = TeacherMapper.toEntity(teacherDTO);
        return ResponseEntity.ok(TeacherMapper.toDTO(teacherService.updateTeacher(id, entity)));
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
