package com.maplewood.student.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maplewood.common.enums.CourseHistoryStatus;
import com.maplewood.student.entity.Student;
import com.maplewood.student.entity.StudentCourseHistory;
import com.maplewood.student.repository.StudentCourseHistoryRepository;

/**
 * Service for calculating academic metrics for students
 * Calculates GPA and total credits earned from course history
 */
@Service
public class AcademicMetricsService {
    
    private static final double GRADUATION_REQUIREMENT = 30.0;  // Credits needed to graduate
    
    @Autowired
    private StudentCourseHistoryRepository courseHistoryRepository;
    
    /**
     * Calculate total credits earned (sum of all PASSED courses)
     */
    public double calculateCreditsEarned(Student student) {
        if (student == null || student.getId() == null) {
            return 0.0;
        }
        
        List<StudentCourseHistory> history = courseHistoryRepository.findByStudent(student);
        
        return history.stream()
            .filter(h -> h.getStatus() == CourseHistoryStatus.PASSED)
            .mapToDouble(h -> {
                if (h.getCourse().getCredits() != null) {
                    return h.getCourse().getCredits().doubleValue();
                }
                return 0.0;
            })
            .sum();
    }
    
    /**
     * Calculate GPA based on course performance
     * GPA = (sum of credits for PASSED courses / sum of ALL course credits) × 4.0
     * 
     * Example:
     * - Attempted: Bio(1) + Chem(1) + PE(0.5) + Math(1.5) = 4.0 credits
     * - Passed: Bio(1) + Chem(1) + PE(0.5) = 2.5 credits
     * - GPA = (2.5 / 4.0) × 4.0 = 2.5
     */
    public double calculateGPA(Student student) {
        if (student == null || student.getId() == null) {
            return 0.0;
        }
        
        List<StudentCourseHistory> history = courseHistoryRepository.findByStudent(student);
        
        if (history.isEmpty()) {
            return 0.0;
        }
        
        double totalCreditsEarned = 0.0;
        double totalCreditsAttempted = 0.0;
        
        for (StudentCourseHistory entry : history) {
            double credits = entry.getCourse().getCredits() != null ? 
                entry.getCourse().getCredits().doubleValue() : 0.0;
            
            totalCreditsAttempted += credits;
            
            if (entry.getStatus() == CourseHistoryStatus.PASSED) {
                totalCreditsEarned += credits;
            }
        }
        
        if (totalCreditsAttempted == 0) {
            return 0.0;
        }
        
        double gpa = (totalCreditsEarned / totalCreditsAttempted) * 4.0;
        return Math.round(gpa * 100.0) / 100.0;
    }
    
    /**
     * Check if student has met graduation requirements
     */
    public boolean isGraduated(Student student) {
        return calculateCreditsEarned(student) >= GRADUATION_REQUIREMENT;
    }
    
    /**
     * Calculate remaining credits needed to graduate
     */
    public double calculateRemainingCreditsToGraduate(Student student) {
        double creditsEarned = calculateCreditsEarned(student);
        double remaining = GRADUATION_REQUIREMENT - creditsEarned;
        return remaining > 0 ? Math.round(remaining * 100.0) / 100.0 : 0.0;
    }
    
    /**
     * Get all academic metrics for a student
     */
    public AcademicMetrics getMetrics(Student student) {
        double gpa = calculateGPA(student);
        double creditsEarned = calculateCreditsEarned(student);
        double remaining = calculateRemainingCreditsToGraduate(student);
        boolean graduated = isGraduated(student);
        
        return new AcademicMetrics(gpa, creditsEarned, remaining, graduated);
    }
    
    /**
     * Simple data class for academic metrics
     */
    public static class AcademicMetrics {
        private final double gpa;
        private final double creditsEarned;
        private final double remainingCreditsToGraduate;
        private final boolean isGraduated;
        
        public AcademicMetrics(double gpa, double creditsEarned, double remainingCreditsToGraduate, boolean isGraduated) {
            this.gpa = gpa;
            this.creditsEarned = creditsEarned;
            this.remainingCreditsToGraduate = remainingCreditsToGraduate;
            this.isGraduated = isGraduated;
        }
        
        public double getGpa() {
            return gpa;
        }
        
        public double getCreditsEarned() {
            return creditsEarned;
        }
        
        public double getRemainingCreditsToGraduate() {
            return remainingCreditsToGraduate;
        }
        
        public boolean isGraduated() {
            return isGraduated;
        }
    }
}
