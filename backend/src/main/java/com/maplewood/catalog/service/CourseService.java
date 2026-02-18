package com.maplewood.catalog.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maplewood.catalog.entity.Course;
import com.maplewood.catalog.repository.CourseRepository;
import com.maplewood.common.enums.CourseType;
import com.maplewood.common.exception.ResourceNotFoundException;
import com.maplewood.school.entity.Specialization;

/**
 * Service for Course operations
 * Handles CRUD operations for courses and complex course queries
 */
@Service
public class CourseService {
    
    @Autowired
    private CourseRepository courseRepository;
    
    /**
     * Get all courses
     */
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }
    
    /**
     * Get course by ID
     */
    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Course", id));
    }
    
    /**
     * Get course by code
     */
    public Course getCourseByCode(String code) {
        return courseRepository.findByCode(code)
            .orElseThrow(() -> new ResourceNotFoundException("Course", "code", code));
    }
    
    /**
     * Get all courses for a specialization
     */
    public List<Course> getCoursesBySpecialization(Specialization specialization) {
        return courseRepository.findBySpecialization(specialization);
    }
    
    /**
     * Get all courses of a specific type
     */
    public List<Course> getCoursesByType(CourseType courseType) {
        return courseRepository.findByCourseType(courseType);
    }
    
    /**
     * Get all courses offered in a specific semester
     */
    public List<Course> getCoursesBySemesterOrder(Integer semesterOrder) {
        return courseRepository.findBySemesterOrder(semesterOrder);
    }
    
    /**
     * Get all courses available for a specific grade level
     */
    public List<Course> getCoursesByGradeLevel(Integer gradeLevel) {
        return courseRepository.findCoursesForGradeLevel(gradeLevel);
    }
    
    /**
     * Get all courses with prerequisites
     */
    public List<Course> getCoursesWithPrerequisites() {
        return courseRepository.findCoursesWithPrerequisites();
    }
    
    /**
     * Get the full prerequisite chain for a course
     */
    public List<Course> getPrerequisiteChain(Long courseId) {
        Course course = getCourseById(courseId);
        List<Course> chain = new ArrayList<>();
        
        Course current = course.getPrerequisite();
        while (current != null) {
            chain.add(0, current);
            current = current.getPrerequisite();
        }
        
        return chain;
    }
    
    /**
     * Get all courses that depend on this course as prerequisite
     */
    public List<Course> getDependentCourses(Long courseId) {
        Course course = getCourseById(courseId);
        return courseRepository.findByPrerequisite(course);
    }
    
    /**
     * Get courses by type and grade level
     */
    public List<Course> getCoursesByTypeAndGradeLevel(CourseType courseType, Integer gradeLevel) {
        return courseRepository.findByCourseTypeAndGradeLevel(courseType, gradeLevel);
    }
    
    /**
     * Get courses by specialization and grade level
     */
    public List<Course> getCoursesBySpecializationAndGradeLevel(Specialization specialization, Integer gradeLevel) {
        return courseRepository.findBySpecializationAndGradeLevel(specialization, gradeLevel);
    }
    
    /**
     * Get courses by semester and grade level
     */
    public List<Course> getCoursesBySemesterAndGradeLevel(Integer semesterOrder, Integer gradeLevel) {
        return courseRepository.findBySemesterAndGradeLevel(semesterOrder, gradeLevel);
    }
    
    /**
     * Create new course
     */
    public Course createCourse(Course course) {
        if (courseRepository.existsByCode(course.getCode())) {
            throw new IllegalArgumentException("Course with code " + course.getCode() + " already exists");
        }
        return courseRepository.save(course);
    }
    
    /**
     * Update course
     */
    public Course updateCourse(Long id, Course courseDetails) {
        Course course = getCourseById(id);
        
        // Prevent code changes if code already exists elsewhere
        if (!course.getCode().equals(courseDetails.getCode()) && courseRepository.existsByCode(courseDetails.getCode())) {
            throw new IllegalArgumentException("Course with code " + courseDetails.getCode() + " already exists");
        }
        
        course.setCode(courseDetails.getCode());
        course.setName(courseDetails.getName());
        course.setDescription(courseDetails.getDescription());
        course.setCredits(courseDetails.getCredits());
        course.setHoursPerWeek(courseDetails.getHoursPerWeek());
        course.setSpecialization(courseDetails.getSpecialization());
        course.setPrerequisite(courseDetails.getPrerequisite());
        course.setCourseType(courseDetails.getCourseType());
        course.setGradeLevelMin(courseDetails.getGradeLevelMin());
        course.setGradeLevelMax(courseDetails.getGradeLevelMax());
        course.setSemesterOrder(courseDetails.getSemesterOrder());
        
        return courseRepository.save(course);
    }
    
    /**
     * Delete course
     */
    public void deleteCourse(Long id) {
        Course course = getCourseById(id);
        courseRepository.delete(course);
    }
}
