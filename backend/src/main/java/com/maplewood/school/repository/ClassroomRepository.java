package com.maplewood.school.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maplewood.school.entity.Classroom;
import com.maplewood.school.entity.RoomType;

/**
 * Repository for Classroom entity
 * Provides database operations for classrooms
 */
@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
    
    /**
     * Find classroom by name
     */
    Classroom findByName(String name);
    
    /**
     * Check if classroom exists by name
     */
    boolean existsByName(String name);
    
    /**
     * Find all classrooms of a specific room type
     */
    List<Classroom> findByRoomType(RoomType roomType);
    
    /**
     * Find all classrooms on a specific floor
     */
    List<Classroom> findByFloor(Integer floor);
    
    /**
     * Find classrooms with capacity >= specified amount
     */
    List<Classroom> findByCapacityGreaterThanEqual(Integer capacity);
}
