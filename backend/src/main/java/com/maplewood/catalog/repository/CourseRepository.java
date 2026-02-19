package com.maplewood.catalog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.maplewood.catalog.entity.Course;
import com.maplewood.common.enums.CourseType;
import com.maplewood.school.entity.Specialization;

/**
 * Repository for Course entity
 * Provides database operations for courses with complex filtering queries
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
    /**
     * Find course by unique code (e.g., "MATH-101", "ENG-201")
     */
    Optional<Course> findByCode(String code);
    
    /**
     * Check if course exists by code
     */
    boolean existsByCode(String code);
    
    /**
     * Find all courses for a specific specialization
     */
    List<Course> findBySpecialization(Specialization specialization);
    
    /**
     * Find all courses of a specific type (CORE or ELECTIVE)
     */
    List<Course> findByCourseType(CourseType courseType);
    
    /**
     * Find all courses offered in a specific semester (semester_order: 1=Fall, 2=Spring)
     */
    List<Course> findBySemesterOrder(Integer semesterOrder);
    
    /**
     * Find all courses that have prerequisites
     */
    @Query("SELECT c FROM Course c WHERE c.prerequisite IS NOT NULL")
    List<Course> findCoursesWithPrerequisites();
    
    /**
     * Find all courses that have a specific course as prerequisite
     */
    List<Course> findByPrerequisite(Course prerequisite);
    
    /**
     * Find courses available for a specific grade level
     * A course is available if the student's grade is within the course's grade level range
     */
    @Query("SELECT c FROM Course c WHERE c.gradeLevelMin <= :studentGrade AND c.gradeLevelMax >= :studentGrade")
    List<Course> findCoursesForGradeLevel(@Param("studentGrade") Integer studentGrade);
    
    /**
     * Find core courses available for a specific grade level
     */
    @Query("SELECT c FROM Course c WHERE c.courseType = :courseType AND c.gradeLevelMin <= :studentGrade AND c.gradeLevelMax >= :studentGrade")
    List<Course> findByCourseTypeAndGradeLevel(@Param("courseType") CourseType courseType, @Param("studentGrade") Integer studentGrade);
    
    /**
     * Find courses by specialization and grade level
     */
    @Query("SELECT c FROM Course c WHERE c.specialization = :specialization AND c.gradeLevelMin <= :studentGrade AND c.gradeLevelMax >= :studentGrade")
    List<Course> findBySpecializationAndGradeLevel(@Param("specialization") Specialization specialization, @Param("studentGrade") Integer studentGrade);
    
    /**
     * Find courses offered in a specific semester and grade level
     */
    @Query("SELECT c FROM Course c WHERE c.semesterOrder = :semesterOrder AND c.gradeLevelMin <= :studentGrade AND c.gradeLevelMax >= :studentGrade")
    List<Course> findBySemesterAndGradeLevel(@Param("semesterOrder") Integer semesterOrder, @Param("studentGrade") Integer studentGrade);
    
    /**
     * Find all courses for a specific specialization by ID
     */
    List<Course> findBySpecialization_Id(Long specializationId);
}
