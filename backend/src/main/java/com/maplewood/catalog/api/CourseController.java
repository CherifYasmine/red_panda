package com.maplewood.catalog.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.maplewood.catalog.entity.Course;
import com.maplewood.catalog.service.CourseService;
import com.maplewood.common.dto.CourseDTO;
import com.maplewood.common.enums.CourseType;
import com.maplewood.common.mapper.CourseMapper;

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
     * Get all courses
     */
    @GetMapping
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses()
            .stream()
            .map(CourseMapper::toDTO)
            .collect(Collectors.toList()));
    }
    
    /**
     * Get course by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(CourseMapper.toDTO(courseService.getCourseById(id)));
    }
    
    /**
     * Get course by code
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<CourseDTO> getCourseByCode(@PathVariable String code) {
        return ResponseEntity.ok(CourseMapper.toDTO(courseService.getCourseByCode(code)));
    }
    
    /**
     * Get all courses by type (CORE or ELECTIVE)
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<CourseDTO>> getCoursesByType(@PathVariable CourseType type) {
        return ResponseEntity.ok(courseService.getCoursesByType(type)
            .stream()
            .map(CourseMapper::toDTO)
            .collect(Collectors.toList()));
    }
    
    /**
     * Get all courses by semester order
     * @param semesterOrder 1 for Fall, 2 for Spring
     */
    @GetMapping("/semester/{semesterOrder}")
    public ResponseEntity<List<CourseDTO>> getCoursesBySemesterOrder(@PathVariable Integer semesterOrder) {
        return ResponseEntity.ok(courseService.getCoursesBySemesterOrder(semesterOrder)
            .stream()
            .map(CourseMapper::toDTO)
            .collect(Collectors.toList()));
    }
    
    /**
     * Get all courses available for a specific grade level
     */
    @GetMapping("/grade-level/{gradeLevel}")
    public ResponseEntity<List<CourseDTO>> getCoursesByGradeLevel(@PathVariable Integer gradeLevel) {
        return ResponseEntity.ok(courseService.getCoursesByGradeLevel(gradeLevel)
            .stream()
            .map(CourseMapper::toDTO)
            .collect(Collectors.toList()));
    }
    
    /**
     * Get all courses with prerequisites
     */
    @GetMapping("/with-prerequisites")
    public ResponseEntity<List<CourseDTO>> getCoursesWithPrerequisites() {
        return ResponseEntity.ok(courseService.getCoursesWithPrerequisites()
            .stream()
            .map(CourseMapper::toDTO)
            .collect(Collectors.toList()));
    }
    
    /**
     * Get the prerequisite chain for a course
     */
    @GetMapping("/{id}/prerequisite-chain")
    public ResponseEntity<List<CourseDTO>> getPrerequisiteChain(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getPrerequisiteChain(id)
            .stream()
            .map(CourseMapper::toDTO)
            .collect(Collectors.toList()));
    }
    
    /**
     * Get all courses that depend on this course as prerequisite
     */
    @GetMapping("/{id}/dependents")
    public ResponseEntity<List<CourseDTO>> getDependentCourses(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getDependentCourses(id)
            .stream()
            .map(CourseMapper::toDTO)
            .collect(Collectors.toList()));
    }
    
    /**
     * Get courses by type and grade level
     */
    @GetMapping("/search")
    public ResponseEntity<List<CourseDTO>> searchCourses(
            @RequestParam(required = false) CourseType type,
            @RequestParam(required = false) Integer gradeLevel,
            @RequestParam(required = false) Integer semesterOrder) {
        
        if (type != null && gradeLevel != null) {
            return ResponseEntity.ok(courseService.getCoursesByTypeAndGradeLevel(type, gradeLevel)
                .stream()
                .map(CourseMapper::toDTO)
                .collect(Collectors.toList()));
        }
        
        if (gradeLevel != null) {
            return ResponseEntity.ok(courseService.getCoursesByGradeLevel(gradeLevel)
                .stream()
                .map(CourseMapper::toDTO)
                .collect(Collectors.toList()));
        }
        
        if (type != null) {
            return ResponseEntity.ok(courseService.getCoursesByType(type)
                .stream()
                .map(CourseMapper::toDTO)
                .collect(Collectors.toList()));
        }
        
        if (semesterOrder != null) {
            return ResponseEntity.ok(courseService.getCoursesBySemesterOrder(semesterOrder)
                .stream()
                .map(CourseMapper::toDTO)
                .collect(Collectors.toList()));
        }
        
        return ResponseEntity.ok(courseService.getAllCourses()
            .stream()
            .map(CourseMapper::toDTO)
            .collect(Collectors.toList()));
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
     * Delete course
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}
