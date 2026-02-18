package com.maplewood.school.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maplewood.school.entity.Specialization;
import com.maplewood.school.entity.Teacher;

/**
 * Repository for Teacher entity
 * Provides database operations for teachers
 */
@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    
    /**
     * Find teacher by email
     */
    Teacher findByEmail(String email);
    
    /**
     * Check if teacher exists by email
     */
    boolean existsByEmail(String email);
    
    /**
     * Find all teachers in a specific specialization
     */
    List<Teacher> findBySpecialization(Specialization specialization);
    
    /**
     * Find all teachers by first and last name
     */
    List<Teacher> findByFirstNameAndLastName(String firstName, String lastName);
    
    /**
     * Find teacher by full name (first + last)
     */
    Teacher findByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName);
}
