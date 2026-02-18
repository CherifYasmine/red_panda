package com.maplewood.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maplewood.school.entity.RoomType;

/**
 * Repository for RoomType entity
 * Provides database operations for room types (Lab, Classroom, Auditorium, etc.)
 */
@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {
    
    /**
     * Find room type by name
     */
    RoomType findByName(String name);
    
    /**
     * Check if room type exists by name
     */
    boolean existsByName(String name);
}
