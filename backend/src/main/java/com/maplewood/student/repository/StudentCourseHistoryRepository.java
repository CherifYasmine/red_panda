package com.maplewood.student.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.maplewood.common.enums.CourseHistoryStatus;
import com.maplewood.course.entity.Course;
import com.maplewood.school.entity.Semester;
import com.maplewood.student.entity.Student;
import com.maplewood.student.entity.StudentCourseHistory;

/**
 * Repository for StudentCourseHistory entity
 * Provides database operations for student course history tracking
 * Critical for prerequisite validation, credit calculations, and academic record management
 */
@Repository
public interface StudentCourseHistoryRepository extends JpaRepository<StudentCourseHistory, Long> {
    
    /**
     * Find all courses taken by a student with pagination
     */
    Page<StudentCourseHistory> findByStudent(Student student, Pageable pageable);
    
    /**
     * Find all courses taken by a student
     */
    List<StudentCourseHistory> findByStudent(Student student);
    
    /**
     * Find all courses a student has PASSED (for prerequisite validation)
     */
    List<StudentCourseHistory> findByStudentAndStatus(Student student, CourseHistoryStatus status);
    
    /**
     * Find specific course history record for a student
     */
    Optional<StudentCourseHistory> findByStudentAndCourse(Student student, Course course);
    
    /**
     * Check if student has passed a specific course (prerequisite validation)
     */
    @Query("SELECT COUNT(sch) > 0 FROM StudentCourseHistory sch WHERE sch.student = :student AND sch.course = :course AND sch.status = :status")
    boolean existsByStudentAndCourseAndStatus(@Param("student") Student student, @Param("course") Course course, @Param("status") CourseHistoryStatus status);
    
    /**
     * Find all courses taken in a specific semester
     */
    List<StudentCourseHistory> findByStudentAndSemester(Student student, Semester semester);
    
    /**
     * Find courses by semester and status
     */
    List<StudentCourseHistory> findByStudentAndSemesterAndStatus(Student student, Semester semester, CourseHistoryStatus status);
    
    /**
     * Get all students who took a specific course
     */
    List<StudentCourseHistory> findByCourse(Course course);
    
    /**
     * Get all students who have PASSED a specific course
     */
    List<StudentCourseHistory> findByCourseAndStatus(Course course, CourseHistoryStatus status);
    
    /**
     * Count total courses passed by student (for credit calculation)
     */
    long countByStudentAndStatus(Student student, CourseHistoryStatus status);
    
    /**
     * Count courses taken in a specific semester
     */
    long countByStudentAndSemester(Student student, Semester semester);
    
    /**
     * Find courses a student has FAILED (for retry eligibility)
     */
    @Query("SELECT sch FROM StudentCourseHistory sch WHERE sch.student = :student AND sch.status = :status")
    List<StudentCourseHistory> findFailedCoursesByStudent(@Param("student") Student student, @Param("status") CourseHistoryStatus status);
}
