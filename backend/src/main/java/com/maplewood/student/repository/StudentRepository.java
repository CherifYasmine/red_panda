package com.maplewood.student.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.maplewood.common.enums.StudentStatus;
import com.maplewood.student.entity.Student;

/**
 * Repository for Student entity
 * Provides database operations for students with lookup and filtering queries
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    
    /**
     * Find student by email
     */
    Optional<Student> findByEmail(String email);
    
    /**
     * Check if student exists by email
     */
    boolean existsByEmail(String email);
    
    /**
     * Find student by first and last name
     */
    Optional<Student> findByFirstNameAndLastName(String firstName, String lastName);
    
    /**
     * Find students by first name (case-insensitive)
     */
    List<Student> findByFirstNameIgnoreCase(String firstName);
    
    /**
     * Find students by grade level
     */
    List<Student> findByGradeLevel(Integer gradeLevel);
    
    /**
     * Find students by status (ACTIVE, INACTIVE, GRADUATED)
     */
    List<Student> findByStatus(StudentStatus status);
    
    /**
     * Find active students
     */
    @Query("SELECT s FROM Student s WHERE s.status = 'ACTIVE'")
    List<Student> findActiveStudents();
    
    /**
     * Find students by enrollment year
     */
    List<Student> findByEnrollmentYear(Integer enrollmentYear);
    
    /**
     * Find students by expected graduation year
     */
    List<Student> findByExpectedGraduationYear(Integer year);
    
    /**
     * Get total count of students by status
     */
    long countByStatus(StudentStatus status);
}
