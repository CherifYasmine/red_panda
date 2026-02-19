package com.maplewood.scheduling.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maplewood.catalog.entity.Course;
import com.maplewood.catalog.service.CourseService;
import com.maplewood.common.dto.CreateCourseSectionDTO;
import com.maplewood.common.dto.UpdateCourseSectionDTO;
import com.maplewood.common.exception.ResourceNotFoundException;
import com.maplewood.scheduling.entity.CourseSection;
import com.maplewood.scheduling.repository.CourseSectionRepository;
import com.maplewood.school.entity.Classroom;
import com.maplewood.school.entity.Semester;
import com.maplewood.school.entity.Teacher;
import com.maplewood.school.service.ClassroomService;
import com.maplewood.school.service.SemesterService;
import com.maplewood.school.service.TeacherService;

/**
 * Service for CourseSection entity
 * Handles CRUD operations and business logic for course sections
 * A CourseSection is a specific instance of a Course taught by a Teacher in a Semester
 */
@Service
public class CourseSectionService {
    
    @Autowired
    private CourseSectionRepository courseSectionRepository;
    
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
    public List<CourseSection> getAllSections() {
        return courseSectionRepository.findAll();
    }
    
    /**
     * Get course section by ID
     */
    public CourseSection getCourseSectionById(Long id) {
        return courseSectionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("CourseSection", id));
    }
    
    /**
     * Get all sections for a specific course
     */
    public List<CourseSection> getSectionsByCourse(Course course) {
        return courseSectionRepository.findByCourse(course);
    }
    
    /**
     * Get all sections taught by a specific teacher
     */
    public List<CourseSection> getSectionsByTeacher(Teacher teacher) {
        return courseSectionRepository.findByTeacher(teacher);
    }
    
    /**
     * Get all sections in a specific classroom
     */
    public List<CourseSection> getSectionsByClassroom(Classroom classroom) {
        return courseSectionRepository.findByClassroom(classroom);
    }
    
    /**
     * Get all sections for a specific semester
     */
    public List<CourseSection> getSectionsBySemester(Semester semester) {
        return courseSectionRepository.findBySemester(semester);
    }
    
    /**
     * Get all sections of a course in a specific semester
     */
    public List<CourseSection> getSectionsByCourseAndSemester(Course course, Semester semester) {
        return courseSectionRepository.findByCourseAndSemester(course, semester);
    }
    
    /**
     * Get all sections taught by a teacher in a specific semester
     */
    public List<CourseSection> getSectionsByTeacherAndSemester(Teacher teacher, Semester semester) {
        return courseSectionRepository.findByTeacherAndSemester(teacher, semester);
    }
    
    /**
     * Get all sections in a classroom for a specific semester
     */
    public List<CourseSection> getSectionsByClassroomAndSemester(Classroom classroom, Semester semester) {
        return courseSectionRepository.findByClassroomAndSemester(classroom, semester);
    }
    
    /**
     * Get available sections (with enrollment below capacity)
     */
    public List<CourseSection> getAvailableSections() {
        return courseSectionRepository.findAvailableSections();
    }
    
    /**
     * Create new course section
     * Validates that teacher's specialization matches course's specialization
     */
    public CourseSection createCourseSectionFromDTO(CreateCourseSectionDTO createDTO) {
        CourseSection courseSection = new CourseSection();
        
        // Load and set all entities
        Course course = courseService.getCourseById(createDTO.getCourseId());
        Teacher teacher = teacherService.getTeacherById(createDTO.getTeacherId());
        Classroom classroom = classroomService.getClassroomById(createDTO.getClassroomId());
        Semester activeSemester = semesterService.getActiveSemester();  // Auto-load active semester
        
        // VALIDATION: Check specialization match
        if (course.getSpecialization() == null || teacher.getSpecialization() == null) {
            throw new IllegalArgumentException("Course and teacher must have specializations defined");
        }
        
        if (!course.getSpecialization().getId().equals(teacher.getSpecialization().getId())) {
            throw new IllegalArgumentException(
                "Teacher " + teacher.getFirstName() + " " + teacher.getLastName() + 
                " specializes in " + teacher.getSpecialization().getName() + 
                " but this course is in " + course.getSpecialization().getName()
            );
        }
        
        courseSection.setCourse(course);
        courseSection.setTeacher(teacher);
        courseSection.setClassroom(classroom);
        courseSection.setSemester(activeSemester);
        courseSection.setCapacity(createDTO.getCapacity());
        courseSection.setEnrollmentCount(0);
        
        return courseSectionRepository.save(courseSection);
    }
    
    /**
     * Update course section
     */
    public CourseSection updateCourseSectionFromDTO(Long id, UpdateCourseSectionDTO updateDTO) {
        CourseSection existing = getCourseSectionById(id);
        
        if (updateDTO.getTeacherId() != null) {
            Teacher teacher = teacherService.getTeacherById(updateDTO.getTeacherId());
            existing.setTeacher(teacher);
        }
        if (updateDTO.getClassroomId() != null) {
            Classroom classroom = classroomService.getClassroomById(updateDTO.getClassroomId());
            existing.setClassroom(classroom);
        }
        if (updateDTO.getCapacity() != null) {
            existing.setCapacity(updateDTO.getCapacity());
        }
        
        return courseSectionRepository.save(existing);
    }
    
    /**
     * Delete course section
     */
    public void deleteCourseSection(Long id) {
        if (!courseSectionRepository.existsById(id)) {
            throw new ResourceNotFoundException("CourseSection", id);
        }
        courseSectionRepository.deleteById(id);
    }
    
    /**
     * Increment enrollment count (when student enrolls)
     */
    public CourseSection incrementEnrollmentCount(Long sectionId) {
        CourseSection section = getCourseSectionById(sectionId);
        section.setEnrollmentCount(section.getEnrollmentCount() + 1);
        return courseSectionRepository.save(section);
    }
    
    /**
     * Decrement enrollment count (when student withdraws)
     */
    public CourseSection decrementEnrollmentCount(Long sectionId) {
        CourseSection section = getCourseSectionById(sectionId);
        if (section.getEnrollmentCount() > 0) {
            section.setEnrollmentCount(section.getEnrollmentCount() - 1);
            return courseSectionRepository.save(section);
        }
        return section;
    }
}
