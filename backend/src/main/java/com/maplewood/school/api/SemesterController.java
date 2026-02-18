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

import com.maplewood.common.enums.SemesterName;
import com.maplewood.school.entity.Semester;
import com.maplewood.school.service.SemesterService;

/**
 * REST controller for Semester endpoints
 * Provides CRUD operations for semesters and active semester management
 */
@RestController
@RequestMapping("/api/v1/semesters")
@CrossOrigin(origins = "*")
public class SemesterController {
    
    @Autowired
    private SemesterService semesterService;
    
    /**
     * GET all semesters
     */
    @GetMapping
    public ResponseEntity<List<Semester>> getAllSemesters() {
        return ResponseEntity.ok(semesterService.getAllSemesters());
    }
    
    /**
     * GET semester by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Semester> getSemesterById(@PathVariable Long id) {
        return ResponseEntity.ok(semesterService.getSemesterById(id));
    }
    
    /**
     * GET semester by name and year
     */
    @GetMapping("/search")
    public ResponseEntity<Semester> getSemesterByNameAndYear(
        @RequestParam SemesterName name,
        @RequestParam Integer year
    ) {
        return ResponseEntity.ok(semesterService.getSemesterByNameAndYear(name, year));
    }
    
    /**
     * GET semesters by year
     */
    @GetMapping("/year/{year}")
    public ResponseEntity<List<Semester>> getSemestersByYear(@PathVariable Integer year) {
        return ResponseEntity.ok(semesterService.getSemestersByYear(year));
    }
    
    /**
     * GET active semester
     */
    @GetMapping("/active")
    public ResponseEntity<Semester> getActiveSemester() {
        return ResponseEntity.ok(semesterService.getActiveSemester());
    }
    
    /**
     * POST create new semester
     */
    @PostMapping
    public ResponseEntity<Semester> createSemester(@RequestBody Semester semester) {
        return ResponseEntity.status(HttpStatus.CREATED).body(semesterService.createSemester(semester));
    }
    
    /**
     * PUT update semester
     */
    @PutMapping("/{id}")
    public ResponseEntity<Semester> updateSemester(@PathVariable Long id, @RequestBody Semester semester) {
        return ResponseEntity.ok(semesterService.updateSemester(id, semester));
    }
    
    /**
     * POST set semester as active
     */
    @PostMapping("/{id}/set-active")
    public ResponseEntity<Semester> setAsActive(@PathVariable Long id) {
        return ResponseEntity.ok(semesterService.setAsActive(id));
    }
    
    /**
     * DELETE semester
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSemester(@PathVariable Long id) {
        semesterService.deleteSemester(id);
        return ResponseEntity.noContent().build();
    }
}
