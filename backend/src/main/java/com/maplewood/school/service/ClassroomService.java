package com.maplewood.school.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maplewood.common.exception.ResourceNotFoundException;
import com.maplewood.school.entity.Classroom;
import com.maplewood.school.entity.RoomType;
import com.maplewood.school.repository.ClassroomRepository;
import com.maplewood.school.repository.RoomTypeRepository;

/**
 * Service for Classroom operations
 * Handles CRUD operations for classrooms
 */
@Service
public class ClassroomService {
    
    @Autowired
    private ClassroomRepository classroomRepository;
    
    @Autowired
    private RoomTypeRepository roomTypeRepository;
    
    /**
     * Get all classrooms
     */
    public List<Classroom> getAllClassrooms() {
        return classroomRepository.findAll();
    }
    
    /**
     * Get classroom by ID
     */
    public Classroom getClassroomById(Long id) {
        return classroomRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Classroom", id));
    }
    
    /**
     * Get classroom by name
     */
    public Classroom getClassroomByName(String name) {
        Classroom classroom = classroomRepository.findByName(name);
        if (classroom == null) {
            throw new ResourceNotFoundException("Classroom", "name", name);
        }
        return classroom;
    }
    
    /**
     * Get all classrooms of a specific room type
     */
    public List<Classroom> getClassroomsByRoomType(Long roomTypeId) {
        RoomType roomType = roomTypeRepository.findById(roomTypeId)
            .orElseThrow(() -> new ResourceNotFoundException("Room type not found with id: " + roomTypeId));
        return classroomRepository.findByRoomType(roomType);
    }
    
    /**
     * Get all classrooms on a specific floor
     */
    public List<Classroom> getClassroomsByFloor(Integer floor) {
        return classroomRepository.findByFloor(floor);
    }
    
    /**
     * Create new classroom
     */
    public Classroom createClassroom(Classroom classroom) {
        if (classroomRepository.existsByName(classroom.getName())) {
            throw new IllegalArgumentException("Classroom with name '" + classroom.getName() + "' already exists");
        }
        RoomType roomType = roomTypeRepository.findById(classroom.getRoomType().getId())
            .orElseThrow(() -> new ResourceNotFoundException("Room type not found"));
        classroom.setRoomType(roomType);
        return classroomRepository.save(classroom);
    }
    
    /**
     * Update classroom
     */
    public Classroom updateClassroom(Long id, Classroom classroomDetails) {
        Classroom classroom = getClassroomById(id);
        classroom.setName(classroomDetails.getName());
        classroom.setCapacity(classroomDetails.getCapacity());
        classroom.setEquipment(classroomDetails.getEquipment());
        classroom.setFloor(classroomDetails.getFloor());
        if (classroomDetails.getRoomType() != null && classroomDetails.getRoomType().getId() != null) {
            RoomType roomType = roomTypeRepository.findById(classroomDetails.getRoomType().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Room type not found"));
            classroom.setRoomType(roomType);
        }
        return classroomRepository.save(classroom);
    }
    
    /**
     * Delete classroom
     */
    public void deleteClassroom(Long id) {
        Classroom classroom = getClassroomById(id);
        classroomRepository.delete(classroom);
    }
}
