package com.maplewood.school.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maplewood.school.entity.RoomType;
import com.maplewood.school.entity.Specialization;

/**
 * Repository for Specialization entity
 * Provides database operations for specializations (Math, Science, English, etc.)
 */
@Repository
public interface SpecializationRepository extends JpaRepository<Specialization, Long> {
    
    /**
     * Find specialization by name
     */
    Specialization findByName(String name);
    
    /**
     * Check if specialization exists by name
     */
    boolean existsByName(String name);
    
    /**
     * Find all specializations for a specific room type
     */
    List<Specialization> findByRoomType(RoomType roomType);
}
