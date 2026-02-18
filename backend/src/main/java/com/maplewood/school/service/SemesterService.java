package com.maplewood.school.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.maplewood.common.enums.SemesterName;
import com.maplewood.common.exception.ResourceNotFoundException;
import com.maplewood.school.entity.Semester;
import com.maplewood.school.repository.SemesterRepository;

/**
 * Service for Semester operations
 * Handles CRUD operations for semesters and active semester management
 */
@Service
public class SemesterService {
    
    @Autowired
    private SemesterRepository semesterRepository;
    
    /**
     * Get all semesters
     */
    public List<Semester> getAllSemesters() {
        return semesterRepository.findAllByOrderByYearDescOrderInYearDesc();
    }
    
    /**
     * Get semester by ID
     */
    public Semester getSemesterById(Long id) {
        return semesterRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Semester not found with id: " + id));
    }
    
    /**
     * Get semester by name and year
     */
    public Semester getSemesterByNameAndYear(SemesterName name, Integer year) {
        Optional<Semester> semester = semesterRepository.findByNameAndYear(name, year);
        if (!semester.isPresent()) {
            throw new ResourceNotFoundException("Semester not found: " + name + " " + year);
        }
        return semester.get();
    }
    
    /**
     * Get all semesters for a specific year
     */
    public List<Semester> getSemestersByYear(Integer year) {
        return semesterRepository.findByYear(year);
    }
    
    /**
     * Get active semester
     */
    public Semester getActiveSemester() {
        Optional<Semester> semester = semesterRepository.findByIsActive(true);
        if (!semester.isPresent()) {
            throw new ResourceNotFoundException("No active semester found");
        }
        return semester.get();
    }
    
    /**
     * Create new semester
     */
    public Semester createSemester(Semester semester) {
        Optional<Semester> existing = semesterRepository.findByNameAndYear(semester.getName(), semester.getYear());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Semester " + semester.getName() + " " + semester.getYear() + " already exists");
        }
        return semesterRepository.save(semester);
    }
    
    /**
     * Update semester
     */
    public Semester updateSemester(Long id, Semester semesterDetails) {
        Semester semester = getSemesterById(id);
        semester.setName(semesterDetails.getName());
        semester.setYear(semesterDetails.getYear());
        semester.setOrderInYear(semesterDetails.getOrderInYear());
        semester.setStartDate(semesterDetails.getStartDate());
        semester.setEndDate(semesterDetails.getEndDate());
        semester.setIsActive(semesterDetails.getIsActive());
        return semesterRepository.save(semester);
    }
    
    /**
     * Set semester as active (deactivates others)
     */
    public Semester setAsActive(Long id) {
        Semester semester = getSemesterById(id);
        
        // Deactivate all other semesters
        List<Semester> allSemesters = getAllSemesters();
        for (Semester s : allSemesters) {
            if (!s.getId().equals(id) && s.getIsActive() != null && s.getIsActive()) {
                s.setIsActive(false);
                semesterRepository.save(s);
            }
        }
        
        // Activate this semester
        semester.setIsActive(true);
        return semesterRepository.save(semester);
    }
    
    /**
     * Delete semester
     */
    public void deleteSemester(Long id) {
        Semester semester = getSemesterById(id);
        semesterRepository.delete(semester);
    }
}
