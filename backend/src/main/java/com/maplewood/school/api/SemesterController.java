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

import com.maplewood.common.dto.SemesterDTO;
import com.maplewood.common.enums.SemesterName;
import com.maplewood.common.mapper.SemesterMapper;
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
    public ResponseEntity<List<SemesterDTO>> getAllSemesters() {
        return ResponseEntity.ok(semesterService.getAllSemesters()
            .stream()
            .map(SemesterMapper::toDTO)
            .collect(Collectors.toList()));
    }
    
    /**
     * GET semester by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<SemesterDTO> getSemesterById(@PathVariable Long id) {
        return ResponseEntity.ok(SemesterMapper.toDTO(semesterService.getSemesterById(id)));
    }
    
    /**
     * GET semester by name and year
     */
    @GetMapping("/search")
    public ResponseEntity<SemesterDTO> getSemesterByNameAndYear(
        @RequestParam SemesterName name,
        @RequestParam Integer year
    ) {
        return ResponseEntity.ok(SemesterMapper.toDTO(semesterService.getSemesterByNameAndYear(name, year)));
    }
    
    /**
     * GET semesters by year
     */
    @GetMapping("/year/{year}")
    public ResponseEntity<List<SemesterDTO>> getSemestersByYear(@PathVariable Integer year) {
        return ResponseEntity.ok(semesterService.getSemestersByYear(year)
            .stream()
            .map(SemesterMapper::toDTO)
            .collect(Collectors.toList()));
    }
    
    /**
     * GET active semester
     */
    @GetMapping("/active")
    public ResponseEntity<SemesterDTO> getActiveSemester() {
        return ResponseEntity.ok(SemesterMapper.toDTO(semesterService.getActiveSemester()));
    }
    
    /**
     * POST create new semester
     */
    @PostMapping
    public ResponseEntity<SemesterDTO> createSemester(@RequestBody SemesterDTO semesterDTO) {
        Semester entity = SemesterMapper.toEntity(semesterDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(SemesterMapper.toDTO(semesterService.createSemester(entity)));
    }
    
    /**
     * PUT update semester
     */
    @PutMapping("/{id}")
    public ResponseEntity<SemesterDTO> updateSemester(@PathVariable Long id, @RequestBody SemesterDTO semesterDTO) {
        Semester entity = SemesterMapper.toEntity(semesterDTO);
        return ResponseEntity.ok(SemesterMapper.toDTO(semesterService.updateSemester(id, entity)));
    }
    
    /**
     * POST set semester as active
     */
    @PostMapping("/{id}/set-active")
    public ResponseEntity<SemesterDTO> setAsActive(@PathVariable Long id) {
        return ResponseEntity.ok(SemesterMapper.toDTO(semesterService.setAsActive(id)));
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
