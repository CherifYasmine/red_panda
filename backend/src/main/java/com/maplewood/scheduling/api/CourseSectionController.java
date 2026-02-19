package com.maplewood.scheduling.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maplewood.catalog.entity.Course;
import com.maplewood.catalog.service.CourseService;
import com.maplewood.common.dto.CourseSectionDTO;
import com.maplewood.common.dto.CreateCourseSectionDTO;
import com.maplewood.common.dto.UpdateCourseSectionDTO;
import com.maplewood.common.mapper.CourseSectionMapper;
import com.maplewood.common.util.DTOConverter;
import com.maplewood.scheduling.entity.CourseSection;
import com.maplewood.scheduling.service.CourseSectionService;
import com.maplewood.school.entity.Classroom;
import com.maplewood.school.entity.Semester;
import com.maplewood.school.entity.Teacher;
import com.maplewood.school.service.ClassroomService;
import com.maplewood.school.service.SemesterService;
import com.maplewood.school.service.TeacherService;

import jakarta.validation.Valid;

/**
 * REST Controller for CourseSection endpoints
 * Provides CRUD operations and section search functionality
 */
@RestController
@RequestMapping("/api/v1/course-sections")
@CrossOrigin(origins = "*")
public class CourseSectionController {
    
    @Autowired
    private CourseSectionService courseSectionService;
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private TeacherService teacherService;
    
    @Autowired
    private ClassroomService classroomService;
    
    @Autowired
    private SemesterService semesterService;
    
    /**
     * Get all course sections
     */
    @GetMapping
    public ResponseEntity<List<CourseSectionDTO>> getAllSections() {
        return ResponseEntity.ok(DTOConverter.convertList(courseSectionService.getAllSections(), CourseSectionMapper::toDTO));
    }
    
    /**
     * Get course section by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourseSectionDTO> getCourseSectionById(@PathVariable Long id) {
        return ResponseEntity.ok(DTOConverter.convert(courseSectionService.getCourseSectionById(id), CourseSectionMapper::toDTO));
    }
    
    /**
     * Get all sections for a specific course
     */
    @GetMapping("/search/course/{courseId}")
    public ResponseEntity<List<CourseSectionDTO>> getSectionsByCourse(@PathVariable Long courseId) {
        Course course = courseService.getCourseById(courseId);
        return ResponseEntity.ok(DTOConverter.convertList(courseSectionService.getSectionsByCourse(course), CourseSectionMapper::toDTO));
    }
    
    /**
     * Get all sections taught by a specific teacher
     */
    @GetMapping("/search/teacher/{teacherId}")
    public ResponseEntity<List<CourseSectionDTO>> getSectionsByTeacher(@PathVariable Long teacherId) {
        Teacher teacher = teacherService.getTeacherById(teacherId);
        return ResponseEntity.ok(DTOConverter.convertList(courseSectionService.getSectionsByTeacher(teacher), CourseSectionMapper::toDTO));
    }
    
    /**
     * Get all sections in a specific classroom
     */
    @GetMapping("/search/classroom/{classroomId}")
    public ResponseEntity<List<CourseSectionDTO>> getSectionsByClassroom(@PathVariable Long classroomId) {
        Classroom classroom = classroomService.getClassroomById(classroomId);
        return ResponseEntity.ok(DTOConverter.convertList(courseSectionService.getSectionsByClassroom(classroom), CourseSectionMapper::toDTO));
    }
    
    /**
     * Get all sections for a specific semester
     */
    @GetMapping("/search/semester/{semesterId}")
    public ResponseEntity<List<CourseSectionDTO>> getSectionsBySemester(@PathVariable Long semesterId) {
        Semester semester = semesterService.getSemesterById(semesterId);
        return ResponseEntity.ok(DTOConverter.convertList(courseSectionService.getSectionsBySemester(semester), CourseSectionMapper::toDTO));
    }
    
    /**
     * Get all sections of a course in a specific semester
     */
    @GetMapping("/search/course/{courseId}/semester/{semesterId}")
    public ResponseEntity<List<CourseSectionDTO>> getSectionsByCourseAndSemester(@PathVariable Long courseId, @PathVariable Long semesterId) {
        Course course = courseService.getCourseById(courseId);
        Semester semester = semesterService.getSemesterById(semesterId);
        return ResponseEntity.ok(DTOConverter.convertList(courseSectionService.getSectionsByCourseAndSemester(course, semester), CourseSectionMapper::toDTO));
    }
    
    /**
     * Get all sections taught by a teacher in a specific semester
     */
    @GetMapping("/search/teacher/{teacherId}/semester/{semesterId}")
    public ResponseEntity<List<CourseSectionDTO>> getSectionsByTeacherAndSemester(@PathVariable Long teacherId, @PathVariable Long semesterId) {
        Teacher teacher = teacherService.getTeacherById(teacherId);
        Semester semester = semesterService.getSemesterById(semesterId);
        return ResponseEntity.ok(DTOConverter.convertList(courseSectionService.getSectionsByTeacherAndSemester(teacher, semester), CourseSectionMapper::toDTO));
    }
    
    /**
     * Get all sections in a classroom for a specific semester
     */
    @GetMapping("/search/classroom/{classroomId}/semester/{semesterId}")
    public ResponseEntity<List<CourseSectionDTO>> getSectionsByClassroomAndSemester(@PathVariable Long classroomId, @PathVariable Long semesterId) {
        Classroom classroom = classroomService.getClassroomById(classroomId);
        Semester semester = semesterService.getSemesterById(semesterId);
        return ResponseEntity.ok(DTOConverter.convertList(courseSectionService.getSectionsByClassroomAndSemester(classroom, semester), CourseSectionMapper::toDTO));
    }
    
    /**
     * Get available sections (with enrollment below capacity)
     */
    @GetMapping("/search/available")
    public ResponseEntity<List<CourseSectionDTO>> getAvailableSections() {
        return ResponseEntity.ok(DTOConverter.convertList(courseSectionService.getAvailableSections(), CourseSectionMapper::toDTO));
    }
    
    /**
     * Create new course section
     */
    @PostMapping
    public ResponseEntity<CourseSectionDTO> createCourseSection(@Valid @RequestBody CreateCourseSectionDTO createDTO) {
        CourseSection created = courseSectionService.createCourseSectionFromDTO(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(CourseSectionMapper.toDTO(created));
    }
    
    /**
     * Update course section
     */
    @PutMapping("/{id}")
    public ResponseEntity<CourseSectionDTO> updateCourseSection(@PathVariable Long id, @Valid @RequestBody UpdateCourseSectionDTO updateDTO) {
        CourseSection updated = courseSectionService.updateCourseSectionFromDTO(id, updateDTO);
        return ResponseEntity.ok(CourseSectionMapper.toDTO(updated));
    }
    
    /**
     * Delete course section
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourseSection(@PathVariable Long id) {
        courseSectionService.deleteCourseSection(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Increment enrollment count (when student enrolls)
     */
    @PutMapping("/{id}/enroll")
    public ResponseEntity<CourseSectionDTO> incrementEnrollmentCount(@PathVariable Long id) {
        CourseSection updated = courseSectionService.incrementEnrollmentCount(id);
        return ResponseEntity.ok(CourseSectionMapper.toDTO(updated));
    }
    
    /**
     * Decrement enrollment count (when student withdraws)
     */
    @PutMapping("/{id}/withdraw")
    public ResponseEntity<CourseSectionDTO> decrementEnrollmentCount(@PathVariable Long id) {
        CourseSection updated = courseSectionService.decrementEnrollmentCount(id);
        return ResponseEntity.ok(CourseSectionMapper.toDTO(updated));
    }
}
