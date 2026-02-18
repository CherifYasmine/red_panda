package com.maplewood.school.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maplewood.common.exception.ResourceNotFoundException;
import com.maplewood.school.entity.RoomType;
import com.maplewood.school.entity.Specialization;
import com.maplewood.school.repository.RoomTypeRepository;
import com.maplewood.school.repository.SpecializationRepository;

/**
 * Service for Specialization operations
 * Handles CRUD operations for specializations (Math, Science, English, etc.)
 */
@Service
public class SpecializationService {
    
    @Autowired
    private SpecializationRepository specializationRepository;
    
    @Autowired
    private RoomTypeRepository roomTypeRepository;
    
    /**
     * Get all specializations
     */
    public List<Specialization> getAllSpecializations() {
        return specializationRepository.findAll();
    }
    
    /**
     * Get specialization by ID
     */
    public Specialization getSpecializationById(Long id) {
        return specializationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialization not found with id: " + id));
    }
    
    /**
     * Get specialization by name
     */
    public Specialization getSpecializationByName(String name) {
        Specialization specialization = specializationRepository.findByName(name);
        if (specialization == null) {
            throw new ResourceNotFoundException("Specialization not found with name: " + name);
        }
        return specialization;
    }
    
    /**
     * Get all specializations for a room type
     */
    public List<Specialization> getSpecializationsByRoomType(Long roomTypeId) {
        RoomType roomType = roomTypeRepository.findById(roomTypeId)
            .orElseThrow(() -> new ResourceNotFoundException("Room type not found with id: " + roomTypeId));
        return specializationRepository.findByRoomType(roomType);
    }
    
    /**
     * Create new specialization
     */
    public Specialization createSpecialization(Specialization specialization) {
        if (specializationRepository.existsByName(specialization.getName())) {
            throw new IllegalArgumentException("Specialization with name '" + specialization.getName() + "' already exists");
        }
        if (specialization.getRoomType() != null && specialization.getRoomType().getId() != null) {
            RoomType roomType = roomTypeRepository.findById(specialization.getRoomType().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Room type not found"));
            specialization.setRoomType(roomType);
        }
        return specializationRepository.save(specialization);
    }
    
    /**
     * Update specialization
     */
    public Specialization updateSpecialization(Long id, Specialization specializationDetails) {
        Specialization specialization = getSpecializationById(id);
        specialization.setName(specializationDetails.getName());
        specialization.setDescription(specializationDetails.getDescription());
        if (specializationDetails.getRoomType() != null && specializationDetails.getRoomType().getId() != null) {
            RoomType roomType = roomTypeRepository.findById(specializationDetails.getRoomType().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Room type not found"));
            specialization.setRoomType(roomType);
        }
        return specializationRepository.save(specialization);
    }
    
    /**
     * Delete specialization
     */
    public void deleteSpecialization(Long id) {
        Specialization specialization = getSpecializationById(id);
        specializationRepository.delete(specialization);
    }
}
