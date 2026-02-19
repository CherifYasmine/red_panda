package com.maplewood.course.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.maplewood.course.entity.Course;
import com.maplewood.course.entity.CourseSection;
import com.maplewood.school.entity.Classroom;
import com.maplewood.school.entity.Semester;
import com.maplewood.school.entity.Teacher;

/**
 * Repository for CourseSection entity
 * Provides database operations for course sections with specific times and teachers
 * Sections are specific instances of a course (e.g., Physics I Section A vs Section B)
 */
@Repository
public interface CourseSectionRepository extends JpaRepository<CourseSection, Long> {
    
    /**
     * Find all sections for a specific course
     */
    List<CourseSection> findByCourse(Course course);
    
    /**
     * Find all sections taught by a specific teacher
     */
    List<CourseSection> findByTeacher(Teacher teacher);
    
    /**
     * Find all sections in a specific classroom
     */
    List<CourseSection> findByClassroom(Classroom classroom);
    
    /**
     * Find all sections for a specific semester
     */
    List<CourseSection> findBySemester(Semester semester);
    
    /**
     * Find all sections of a course in a specific semester
     */
    List<CourseSection> findByCourseAndSemester(Course course, Semester semester);
    
    /**
     * Find all sections taught by a teacher in a specific semester
     */
    List<CourseSection> findByTeacherAndSemester(Teacher teacher, Semester semester);
    
    /**
     * Find all sections in a classroom for a specific semester (for room availability)
     */
    List<CourseSection> findByClassroomAndSemester(Classroom classroom, Semester semester);
    
    /**
     * Check if a section exists for a course in a semester
     */
    boolean existsByCourseAndSemester(Course course, Semester semester);
    
    /**
     * Count sections for a course (how many parallel sections exist)
     */
    long countByCourse(Course course);
    
    /**
     * Get sections with enrollment below capacity (for availability)
     */
    @Query("SELECT cs FROM CourseSection cs WHERE cs.enrollmentCount < cs.course.credits * 10")
    List<CourseSection> findAvailableSections();
}
