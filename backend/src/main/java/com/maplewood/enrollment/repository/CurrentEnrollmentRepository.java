package com.maplewood.enrollment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.maplewood.common.enums.EnrollmentStatus;
import com.maplewood.enrollment.entity.CurrentEnrollment;
import com.maplewood.scheduling.entity.CourseSection;
import com.maplewood.school.entity.Semester;
import com.maplewood.student.entity.Student;

/**
 * Repository for CurrentEnrollment entity
 * Provides database operations for student's current semester enrollments
 * Critical for enrollment validation (course limit, schedule conflicts, etc.)
 */
@Repository
public interface CurrentEnrollmentRepository extends JpaRepository<CurrentEnrollment, Long> {
    
    /**
     * Find all enrollments for a student in a specific semester
     */
    List<CurrentEnrollment> findByStudentAndSemester(Student student, Semester semester);
    
    /**
     * Find specific enrollment record
     */
    Optional<CurrentEnrollment> findByStudentAndCourseSection(Student student, CourseSection courseSection);
    
    /**
     * Check if student is enrolled in a specific section
     */
    boolean existsByStudentAndCourseSection(Student student, CourseSection courseSection);
    
    /**
     * Check if student is enrolled in a specific section in a semester
     */
    boolean existsByStudentAndCourseSectionAndSemester(Student student, CourseSection courseSection, Semester semester);
    
    /**
     * Count active enrollments for a student in a semester (for 5-course limit validation)
     */
    @Query("SELECT COUNT(ce) FROM CurrentEnrollment ce WHERE ce.student = :student AND ce.semester = :semester AND ce.status = :status")
    long countByStudentAndSemesterAndStatus(@Param("student") Student student, @Param("semester") Semester semester, @Param("status") EnrollmentStatus status);
    
    /**
     * Get all active enrollments for a student in a semester
     */
    @Query("SELECT ce FROM CurrentEnrollment ce WHERE ce.student = :student AND ce.semester = :semester AND ce.status = :status")
    List<CurrentEnrollment> findByStudentAndSemesterAndStatus(@Param("student") Student student, @Param("semester") Semester semester, @Param("status") EnrollmentStatus status);
    
    /**
     * Find enrollments by status
     */
    List<CurrentEnrollment> findByStatus(EnrollmentStatus status);
    
    /**
     * Find all enrollments in a semester
     */
    List<CurrentEnrollment> findBySemester(Semester semester);
    
    /**
     * Count total enrollments in a section (for section capacity tracking)
     */
    long countByCourseSection(CourseSection courseSection);
    
    /**
     * Find all enrollments for a specific course section
     */
    List<CurrentEnrollment> findByCourseSection(CourseSection courseSection);
    
    /**
     * Check if a student has any active enrollments in a semester
     */
    @Query("SELECT COUNT(ce) > 0 FROM CurrentEnrollment ce WHERE ce.student = :student AND ce.semester = :semester AND ce.status = 'ENROLLED'")
    boolean hasActiveEnrollments(@Param("student") Student student, @Param("semester") Semester semester);
    
    /**
     * Get total course count for a student in a semester
     */
    @Query("SELECT COUNT(DISTINCT ce.courseSection.course) FROM CurrentEnrollment ce WHERE ce.student = :student AND ce.semester = :semester AND ce.status = 'ENROLLED'")
    long countDistinctCoursesForStudent(@Param("student") Student student, @Param("semester") Semester semester);
}
