package com.maplewood.school.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maplewood.common.enums.SemesterName;
import com.maplewood.school.entity.Semester;

/**
 * Repository for Semester entity
 * Provides database operations for semesters
 */
@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {
    
    /**
     * Find semester by name and year
     */
    Optional<Semester> findByNameAndYear(SemesterName name, Integer year);
    
    /**
     * Find all semesters for a specific year
     */
    List<Semester> findByYear(Integer year);
    
    /**
     * Find active semester
     */
    Optional<Semester> findByIsActive(Boolean isActive);
    
    /**
     * Find semester by name and order in year
     */
    Optional<Semester> findByNameAndOrderInYear(SemesterName name, Integer orderInYear);
    
    /**
     * Find all semesters ordered by year and order in year
     */
    List<Semester> findAllByOrderByYearDescOrderInYearDesc();
}
