package com.maplewood.school.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maplewood.common.exception.ResourceNotFoundException;
import com.maplewood.school.entity.RoomType;
import com.maplewood.school.repository.RoomTypeRepository;

/**
 * Service for RoomType operations
 * Handles CRUD operations for room types (Lab, Classroom, Auditorium, etc.)
 */
@Service
public class RoomTypeService {
    
    @Autowired
    private RoomTypeRepository roomTypeRepository;
    
    /**
     * Get all room types
     */
    public List<RoomType> getAllRoomTypes() {
        return roomTypeRepository.findAll();
    }
    
    /**
     * Get room type by ID
     */
    public RoomType getRoomTypeById(Long id) {
        return roomTypeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Room type not found with id: " + id));
    }
    
    /**
     * Get room type by name
     */
    public RoomType getRoomTypeByName(String name) {
        RoomType roomType = roomTypeRepository.findByName(name);
        if (roomType == null) {
            throw new ResourceNotFoundException("Room type not found with name: " + name);
        }
        return roomType;
    }
    
    /**
     * Create new room type
     */
    public RoomType createRoomType(RoomType roomType) {
        if (roomTypeRepository.existsByName(roomType.getName())) {
            throw new IllegalArgumentException("Room type with name '" + roomType.getName() + "' already exists");
        }
        return roomTypeRepository.save(roomType);
    }
    
    /**
     * Update room type
     */
    public RoomType updateRoomType(Long id, RoomType roomTypeDetails) {
        RoomType roomType = getRoomTypeById(id);
        roomType.setName(roomTypeDetails.getName());
        roomType.setDescription(roomTypeDetails.getDescription());
        return roomTypeRepository.save(roomType);
    }
    
    /**
     * Delete room type
     */
    public void deleteRoomType(Long id) {
        RoomType roomType = getRoomTypeById(id);
        roomTypeRepository.delete(roomType);
    }
}
