package com.maplewood.course.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.maplewood.common.dto.CourseDTO;
import com.maplewood.common.enums.CourseType;
import com.maplewood.common.mapper.CourseMapper;
import com.maplewood.common.util.DTOConverter;
import com.maplewood.course.entity.Course;
import com.maplewood.course.service.CourseService;

import jakarta.validation.Valid;

/**
 * REST Controller for Course endpoints
 */
@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {
    
    @Autowired
    private CourseService courseService;
    
    /**
     * Get all courses with pagination
     */
    @GetMapping
    public ResponseEntity<Page<CourseDTO>> getAllCourses(Pageable pageable) {
        return ResponseEntity.ok(courseService.getAllCourses(pageable).map(CourseMapper::toDTO));
    }
    
    /**
     * Get course by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(DTOConverter.convert(courseService.getCourseById(id), CourseMapper::toDTO));
    }
    
    /**
     * Get course by code
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<CourseDTO> getCourseByCode(@PathVariable String code) {
        return ResponseEntity.ok(DTOConverter.convert(courseService.getCourseByCode(code), CourseMapper::toDTO));
    }
    
    /**
     * Get all courses by type (CORE or ELECTIVE)
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<CourseDTO>> getCoursesByType(@PathVariable CourseType type) {
        return ResponseEntity.ok(DTOConverter.convertList(courseService.getCoursesByType(type), CourseMapper::toDTO));
    }
    
    /**
     * Get all courses by semester order
     * @param semesterOrder 1 for Fall, 2 for Spring
     */
    @GetMapping("/semester/{semesterOrder}")
    public ResponseEntity<List<CourseDTO>> getCoursesBySemesterOrder(@PathVariable Integer semesterOrder) {
        return ResponseEntity.ok(DTOConverter.convertList(courseService.getCoursesBySemesterOrder(semesterOrder), CourseMapper::toDTO));
    }
    
    /**
     * Get all courses by semester name (FALL or SPRING)
     * Provides string-based filtering for better frontend UX
     */
    @GetMapping("/semester-name/{semesterName}")
    public ResponseEntity<List<CourseDTO>> getCoursesBySemesterName(@PathVariable String semesterName) {
        Integer semesterOrder;
        if ("FALL".equalsIgnoreCase(semesterName)) {
            semesterOrder = 1;
        } else if ("SPRING".equalsIgnoreCase(semesterName)) {
            semesterOrder = 2;
        } else {
            throw new IllegalArgumentException("Invalid semester name. Must be FALL or SPRING");
        }
        return ResponseEntity.ok(DTOConverter.convertList(courseService.getCoursesBySemesterOrder(semesterOrder), CourseMapper::toDTO));
    }
    
    /**
     * Get all courses available for a specific grade level
     */
    @GetMapping("/grade-level/{gradeLevel}")
    public ResponseEntity<List<CourseDTO>> getCoursesByGradeLevel(@PathVariable Integer gradeLevel) {
        return ResponseEntity.ok(DTOConverter.convertList(courseService.getCoursesByGradeLevel(gradeLevel), CourseMapper::toDTO));
    }
    
    /**
     * Get all courses with prerequisites
     */
    @GetMapping("/with-prerequisites")
    public ResponseEntity<List<CourseDTO>> getCoursesWithPrerequisites() {
        return ResponseEntity.ok(DTOConverter.convertList(courseService.getCoursesWithPrerequisites(), CourseMapper::toDTO));
    }
    
    /**
     * Get the prerequisite chain for a course
     */
    @GetMapping("/{id}/prerequisite-chain")
    public ResponseEntity<List<CourseDTO>> getPrerequisiteChain(@PathVariable Long id) {
        return ResponseEntity.ok(DTOConverter.convertList(courseService.getPrerequisiteChain(id), CourseMapper::toDTO));
    }
    
    /**
     * Get all courses that depend on this course as prerequisite
     */
    @GetMapping("/{id}/dependents")
    public ResponseEntity<List<CourseDTO>> getDependentCourses(@PathVariable Long id) {
        return ResponseEntity.ok(DTOConverter.convertList(courseService.getDependentCourses(id), CourseMapper::toDTO));
    }
    
    /**
     * Get courses by type and grade level
     * Supports filtering by:
     * - specialization (ID)
     * - type (CORE, ELECTIVE)
     * - gradeLevel (9-12)
     * - semesterOrder (1=Fall, 2=Spring)
     * - activeOnly (true/false) - only courses with available sections in active semester
     * 
     * Example: /api/v1/courses/search?specialization=3&gradeLevel=10&semesterOrder=1&activeOnly=true
     */
    @GetMapping("/search")
    public ResponseEntity<List<CourseDTO>> searchCourses(
            @RequestParam(required = false) Long specialization,
            @RequestParam(required = false) CourseType type,
            @RequestParam(required = false) Integer gradeLevel,
            @RequestParam(required = false) Integer semesterOrder,
            @RequestParam(required = false, defaultValue = "false") Boolean activeOnly) {
        
        // If filtering for active semester only, get courses with available sections in active semester
        if (Boolean.TRUE.equals(activeOnly)) {
            return ResponseEntity.ok(DTOConverter.convertList(
                courseService.getCoursesWithAvailableSections(),
                CourseMapper::toDTO
            ));
        }
        
        // Apply multiple filters if provided
        List<Course> result = courseService.getAllCourses();
        
        if (specialization != null) {
            result = result.stream()
                .filter(c -> c.getSpecialization().getId().equals(specialization))
                .toList();
        }
        if (type != null) {
            result = result.stream()
                .filter(c -> c.getCourseType().equals(type))
                .toList();
        }
        if (gradeLevel != null) {
            result = result.stream()
                .filter(c -> gradeLevel >= c.getGradeLevelMin() && gradeLevel <= c.getGradeLevelMax())
                .toList();
        }
        if (semesterOrder != null) {
            result = result.stream()
                .filter(c -> c.getSemesterOrder().equals(semesterOrder))
                .toList();
        }
        
        return ResponseEntity.ok(DTOConverter.convertList(result, CourseMapper::toDTO));
    }
    
    /**
     * Create new course
     */
    @PostMapping
    public ResponseEntity<CourseDTO> createCourse(@Valid @RequestBody CourseDTO courseDTO) {
        Course entity = CourseMapper.toEntity(courseDTO);
        Course created = courseService.createCourse(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(CourseMapper.toDTO(created));
    }
    
    /**
     * Update course
     */
    @PutMapping("/{id}")
    public ResponseEntity<CourseDTO> updateCourse(@PathVariable Long id, @Valid @RequestBody CourseDTO courseDTO) {
        Course entity = CourseMapper.toEntity(courseDTO);
        Course updated = courseService.updateCourse(id, entity);
        return ResponseEntity.ok(CourseMapper.toDTO(updated));
    }
    
    /**
     * Get courses by specialization
     */
    @GetMapping("/specialization/{specializationId}")
    public ResponseEntity<List<CourseDTO>> getCoursesBySpecialization(@PathVariable Long specializationId) {
        return ResponseEntity.ok(DTOConverter.convertList(
            courseService.getCoursesBySpecialization(specializationId),
            CourseMapper::toDTO
        ));
    }
    
    /**
     * Get available sections for a course in the active semester
     */
    @GetMapping("/{courseId}/available-sections")
    public ResponseEntity<?> getAvailableSectionsForCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getAvailableSectionsForCourse(courseId));
    }
    
    /**
     * Delete course
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}
