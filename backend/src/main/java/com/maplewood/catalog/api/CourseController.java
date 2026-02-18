package com.maplewood.catalog.api;

import java.util.List;

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
import com.maplewood.common.util.DTOConverter;

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
        return ResponseEntity.ok(DTOConverter.convertList(courseService.getAllCourses(), CourseMapper::toDTO));
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
     */
    @GetMapping("/search")
    public ResponseEntity<List<CourseDTO>> searchCourses(
            @RequestParam(required = false) CourseType type,
            @RequestParam(required = false) Integer gradeLevel,
            @RequestParam(required = false) Integer semesterOrder) {
        
        if (type != null && gradeLevel != null) {
            return ResponseEntity.ok(DTOConverter.convertList(courseService.getCoursesByTypeAndGradeLevel(type, gradeLevel), CourseMapper::toDTO));
        }
        
        if (gradeLevel != null) {
            return ResponseEntity.ok(DTOConverter.convertList(courseService.getCoursesByGradeLevel(gradeLevel), CourseMapper::toDTO));
        }
        
        if (type != null) {
            return ResponseEntity.ok(DTOConverter.convertList(courseService.getCoursesByType(type), CourseMapper::toDTO));
        }
        
        if (semesterOrder != null) {
            return ResponseEntity.ok(DTOConverter.convertList(courseService.getCoursesBySemesterOrder(semesterOrder), CourseMapper::toDTO));
        }
        
        return ResponseEntity.ok(DTOConverter.convertList(courseService.getAllCourses(), CourseMapper::toDTO));
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
