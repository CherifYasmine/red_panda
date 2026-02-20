package com.maplewood.course.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.maplewood.common.enums.CourseType;
import com.maplewood.common.exception.ResourceNotFoundException;
import com.maplewood.course.entity.Course;
import com.maplewood.course.entity.CourseSection;
import com.maplewood.course.repository.CourseRepository;
import com.maplewood.course.repository.CourseSectionRepository;
import com.maplewood.course.specification.CourseSpecification;
import com.maplewood.school.entity.Semester;
import com.maplewood.school.entity.Specialization;
import com.maplewood.school.repository.SemesterRepository;

/**
 * Service for Course operations
 * Handles CRUD operations for courses and complex course queries
 */
@Service
public class CourseService {
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private CourseSectionRepository courseSectionRepository;
    
    @Autowired
    private SemesterRepository semesterRepository;
    
    /**
     * Get all courses with pagination
     */
    public Page<Course> getAllCourses(Pageable pageable) {
        return courseRepository.findAll(pageable);
    }
    
    /**
     * Get all courses
     */
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }
    
    /**
     * Search courses with filters using Specifications
     */
    public Page<Course> searchCourses(
            Long specialization,
            CourseType type,
            Integer gradeLevel,
            Integer semesterOrder,
            Pageable pageable) {
        
        Specification<Course> spec = CourseSpecification.withFilters(
            specialization,
            type,
            gradeLevel,
            semesterOrder
        );
        
        return courseRepository.findAll(spec, pageable);
    }
    
    /**
     * Search courses with filters and optional activeOnly parameter
     * If activeOnly=true, combines specification filters with availability check
     */
    public Page<Course> searchCourses(
            Long specialization,
            CourseType type,
            Integer gradeLevel,
            Integer semesterOrder,
            Boolean activeOnly,
            Pageable pageable) {
        
        // Get all courses matching specification filters
        Specification<Course> spec = CourseSpecification.withFilters(
            specialization,
            type,
            gradeLevel,
            semesterOrder
        );
        
        Page<Course> results = courseRepository.findAll(spec, pageable);
        
        // If activeOnly, filter results to only those with available sections
        if (Boolean.TRUE.equals(activeOnly)) {
            List<Course> filtered = results.getContent().stream()
                .filter(course -> !getAvailableSectionsForCourse(course.getId()).isEmpty())
                .toList();
            
            // Rebuild the page with filtered content
            // Note: This is in-memory pagination, not ideal for large datasets
            // but necessary because availability requires runtime checking
            return new PageImpl<>(filtered, pageable, filtered.size());
        }
        
        return results;
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
    
    /**
     * Get courses by specialization
     */
    public List<Course> getCoursesBySpecialization(Long specializationId) {
        return courseRepository.findBySpecialization_Id(specializationId);
    }
    
    /**
     * Get courses with available sections in active semester
     * Returns only courses that have at least one section with available capacity
     */
    public List<Course> getCoursesWithAvailableSections() {
        // This will be implemented to filter courses that have sections in active semester with < capacity
        return courseRepository.findAll().stream()
            .filter(course -> !getAvailableSectionsForCourse(course.getId()).isEmpty())
            .toList();
    }
    
    /**
     * Get courses with available sections in active semester with pagination
     * Returns only courses that have at least one section with available capacity
     */
    public Page<Course> getCoursesWithAvailableSections(Pageable pageable) {
        List<Course> allAvailable = getCoursesWithAvailableSections();
        
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), allAvailable.size());
        List<Course> pageContent = allAvailable.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, allAvailable.size());
    }
    
    /**
     * Get available sections for a specific course in the active semester
     * Returns sections with enrollment < capacity
     */
    public List<CourseSection> getAvailableSectionsForCourse(Long courseId) {
        Course course = getCourseById(courseId);
        
        // Get active semester
        Semester activeSemester = semesterRepository.findByIsActive(true)
            .orElse(null);
        
        if (activeSemester == null) {
            return List.of();
        }
        
        // Get all sections of this course in the active semester
        List<CourseSection> sections = courseSectionRepository
            .findByCourseAndSemester(course, activeSemester);
        
        // Filter to only those with available capacity (enrollmentCount < capacity)
        return sections.stream()
            .filter(section -> section.getEnrollmentCount() < section.getCapacity())
            .toList();
    }
}
