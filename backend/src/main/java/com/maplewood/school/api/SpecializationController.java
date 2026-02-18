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

import com.maplewood.school.entity.Specialization;
import com.maplewood.school.service.SpecializationService;

/**
 * REST controller for Specialization endpoints
 * Provides CRUD operations for specializations (Math, Science, English, etc.)
 */
@RestController
@RequestMapping("/api/v1/specializations")
@CrossOrigin(origins = "*")
public class SpecializationController {
    
    @Autowired
    private SpecializationService specializationService;
    
    /**
     * GET all specializations
     */
    @GetMapping
    public ResponseEntity<List<Specialization>> getAllSpecializations() {
        return ResponseEntity.ok(specializationService.getAllSpecializations());
    }
    
    /**
     * GET specialization by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Specialization> getSpecializationById(@PathVariable Long id) {
        return ResponseEntity.ok(specializationService.getSpecializationById(id));
    }
    
    /**
     * GET specialization by name
     */
    @GetMapping("/search/name")
    public ResponseEntity<Specialization> getSpecializationByName(@RequestParam String name) {
        return ResponseEntity.ok(specializationService.getSpecializationByName(name));
    }
    
    /**
     * GET specializations by room type
     */
    @GetMapping("/room-type/{roomTypeId}")
    public ResponseEntity<List<Specialization>> getSpecializationsByRoomType(@PathVariable Long roomTypeId) {
        return ResponseEntity.ok(specializationService.getSpecializationsByRoomType(roomTypeId));
    }
    
    /**
     * POST create new specialization
     */
    @PostMapping
    public ResponseEntity<Specialization> createSpecialization(@RequestBody Specialization specialization) {
        return ResponseEntity.status(HttpStatus.CREATED).body(specializationService.createSpecialization(specialization));
    }
    
    /**
     * PUT update specialization
     */
    @PutMapping("/{id}")
    public ResponseEntity<Specialization> updateSpecialization(@PathVariable Long id, @RequestBody Specialization specialization) {
        return ResponseEntity.ok(specializationService.updateSpecialization(id, specialization));
    }
    
    /**
     * DELETE specialization
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpecialization(@PathVariable Long id) {
        specializationService.deleteSpecialization(id);
        return ResponseEntity.noContent().build();
    }
}
