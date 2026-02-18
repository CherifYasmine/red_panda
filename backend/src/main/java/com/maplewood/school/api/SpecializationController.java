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

import com.maplewood.common.dto.SpecializationDTO;
import com.maplewood.common.mapper.SpecializationMapper;
import com.maplewood.school.entity.Specialization;
import com.maplewood.school.service.SpecializationService;

import jakarta.validation.Valid;

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
    public ResponseEntity<List<SpecializationDTO>> getAllSpecializations() {
        return ResponseEntity.ok(specializationService.getAllSpecializations()
            .stream()
            .map(SpecializationMapper::toDTO)
            .collect(Collectors.toList()));
    }
    
    /**
     * GET specialization by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<SpecializationDTO> getSpecializationById(@PathVariable Long id) {
        return ResponseEntity.ok(SpecializationMapper.toDTO(specializationService.getSpecializationById(id)));
    }
    
    /**
     * GET specialization by name
     */
    @GetMapping("/search/name")
    public ResponseEntity<SpecializationDTO> getSpecializationByName(@RequestParam String name) {
        return ResponseEntity.ok(SpecializationMapper.toDTO(specializationService.getSpecializationByName(name)));
    }
    
    /**
     * GET specializations by room type
     */
    @GetMapping("/room-type/{roomTypeId}")
    public ResponseEntity<List<SpecializationDTO>> getSpecializationsByRoomType(@PathVariable Long roomTypeId) {
        return ResponseEntity.ok(specializationService.getSpecializationsByRoomType(roomTypeId)
            .stream()
            .map(SpecializationMapper::toDTO)
            .collect(Collectors.toList()));
    }
    
    /**
     * POST create new specialization
     */
    @PostMapping
    public ResponseEntity<SpecializationDTO> createSpecialization(@Valid @RequestBody SpecializationDTO specializationDTO) {
        Specialization entity = SpecializationMapper.toEntity(specializationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(SpecializationMapper.toDTO(specializationService.createSpecialization(entity)));
    }
    
    /**
     * PUT update specialization
     */
    @PutMapping("/{id}")
    public ResponseEntity<SpecializationDTO> updateSpecialization(@PathVariable Long id, @Valid @RequestBody SpecializationDTO specializationDTO) {
        Specialization entity = SpecializationMapper.toEntity(specializationDTO);
        return ResponseEntity.ok(SpecializationMapper.toDTO(specializationService.updateSpecialization(id, entity)));
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
