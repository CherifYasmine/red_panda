package com.maplewood.student.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maplewood.common.enums.CourseHistoryStatus;
import com.maplewood.common.exception.DuplicateResourceException;
import com.maplewood.common.exception.ResourceNotFoundException;
import com.maplewood.course.entity.Course;
import com.maplewood.school.entity.Semester;
import com.maplewood.student.entity.Student;
import com.maplewood.student.entity.StudentCourseHistory;
import com.maplewood.student.repository.StudentCourseHistoryRepository;

/**
 * Service for StudentCourseHistory entity
 * Handles CRUD operations and academic record management
 */
@Service
public class StudentCourseHistoryService {
    
    @Autowired
    private StudentCourseHistoryRepository studentCourseHistoryRepository;
    
    /**
     * Get all student course histories
     */
    public List<StudentCourseHistory> getAllCourseHistories() {
        return studentCourseHistoryRepository.findAll();
    }
    
    /**
     * Get course history by ID
     */
    public StudentCourseHistory getCourseHistoryById(Long id) {
        return studentCourseHistoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("StudentCourseHistory", id));
    }
    
    /**
     * Get all courses taken by a student
     */
    public List<StudentCourseHistory> getCourseHistoryByStudent(Student student) {
        return studentCourseHistoryRepository.findByStudent(student);
    }
    
    /**
     * Get all passed courses for a student (for prerequisite validation)
     */
    public List<StudentCourseHistory> getPassedCoursesForStudent(Student student) {
        return studentCourseHistoryRepository.findByStudentAndStatus(student, CourseHistoryStatus.PASSED);
    }
    
    /**
     * Get course history for a specific student and course
     */
    public StudentCourseHistory getCourseHistoryForStudent(Student student, Course course) {
        return studentCourseHistoryRepository.findByStudentAndCourse(student, course)
            .orElseThrow(() -> new ResourceNotFoundException("StudentCourseHistory", "studentId/courseId", student.getId() + "/" + course.getId()));
    }
    
    /**
     * Check if student has passed a specific course (for prerequisite validation)
     */
    public boolean hasStudentPassedCourse(Student student, Course course) {
        return studentCourseHistoryRepository.existsByStudentAndCourseAndStatus(student, course, CourseHistoryStatus.PASSED);
    }
    
    /**
     * Get all courses taken by student in a specific semester
     */
    public List<StudentCourseHistory> getCourseHistoryBySemester(Student student, Semester semester) {
        return studentCourseHistoryRepository.findByStudentAndSemester(student, semester);
    }
    
    /**
     * Create new course history record
     */
    public StudentCourseHistory createCourseHistory(StudentCourseHistory history) {
        // Check for duplicate enrollment in same semester
        boolean exists = studentCourseHistoryRepository.existsByStudentAndCourseAndStatus(
            history.getStudent(), 
            history.getCourse(), 
            history.getStatus()
        );
        
        if (exists) {
            throw new DuplicateResourceException("StudentCourseHistory", "studentId/courseId", history.getStudent().getId() + "/" + history.getCourse().getId());
        }
        
        return studentCourseHistoryRepository.save(history);
    }
    
    /**
     * Update course history
     */
    public StudentCourseHistory updateCourseHistory(Long id, StudentCourseHistory historyDetails) {
        StudentCourseHistory history = getCourseHistoryById(id);
        
        if (historyDetails.getStatus() != null) {
            history.setStatus(historyDetails.getStatus());
        }
        
        return studentCourseHistoryRepository.save(history);
    }
    
    /**
     * Delete course history
     */
    public void deleteCourseHistory(Long id) {
        if (!studentCourseHistoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("StudentCourseHistory", id);
        }
        studentCourseHistoryRepository.deleteById(id);
    }
}
