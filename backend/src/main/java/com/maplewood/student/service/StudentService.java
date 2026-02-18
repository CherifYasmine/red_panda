package com.maplewood.student.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maplewood.common.enums.StudentStatus;
import com.maplewood.common.exception.DuplicateResourceException;
import com.maplewood.common.exception.ResourceNotFoundException;
import com.maplewood.student.entity.Student;
import com.maplewood.student.repository.StudentRepository;

/**
 * Service for Student entity
 * Handles CRUD operations and business logic for students
 */
@Service
public class StudentService {
    
    @Autowired
    private StudentRepository studentRepository;
    
    /**
     * Get all students
     */
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
    
    /**
     * Get student by ID
     */
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Student", id));
    }
    
    /**
     * Get student by email
     */
    public Student getStudentByEmail(String email) {
        return studentRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Student", "email", email));
    }
    
    /**
     * Get student by first and last name
     */
    public Student getStudentByName(String firstName, String lastName) {
        return studentRepository.findByFirstNameAndLastName(firstName, lastName)
            .orElseThrow(() -> new ResourceNotFoundException("Student", "name", firstName + " " + lastName));
    }
    
    /**
     * Get students by first name
     */
    public List<Student> getStudentsByFirstName(String firstName) {
        return studentRepository.findByFirstNameIgnoreCase(firstName);
    }
    
    /**
     * Get students by grade level
     */
    public List<Student> getStudentsByGradeLevel(Integer gradeLevel) {
        return studentRepository.findByGradeLevel(gradeLevel);
    }
    
    /**
     * Get students by status
     */
    public List<Student> getStudentsByStatus(StudentStatus status) {
        return studentRepository.findByStatus(status);
    }
    
    /**
     * Create new student
     */
    public Student createStudent(Student student) {
        if (studentRepository.existsByEmail(student.getEmail())) {
            throw new DuplicateResourceException("Student", "email", student.getEmail());
        }
        return studentRepository.save(student);
    }
    
    /**
     * Update student
     */
    public Student updateStudent(Long id, Student studentDetails) {
        Student student = getStudentById(id);
        
        if (studentDetails.getFirstName() != null) {
            student.setFirstName(studentDetails.getFirstName());
        }
        if (studentDetails.getLastName() != null) {
            student.setLastName(studentDetails.getLastName());
        }
        if (studentDetails.getEmail() != null && !studentDetails.getEmail().equals(student.getEmail())) {
            if (studentRepository.existsByEmail(studentDetails.getEmail())) {
                throw new DuplicateResourceException("Student", "email", studentDetails.getEmail());
            }
            student.setEmail(studentDetails.getEmail());
        }
        if (studentDetails.getGradeLevel() != null) {
            student.setGradeLevel(studentDetails.getGradeLevel());
        }
        if (studentDetails.getEnrollmentYear() != null) {
            student.setEnrollmentYear(studentDetails.getEnrollmentYear());
        }
        if (studentDetails.getExpectedGraduationYear() != null) {
            student.setExpectedGraduationYear(studentDetails.getExpectedGraduationYear());
        }
        if (studentDetails.getStatus() != null) {
            student.setStatus(studentDetails.getStatus());
        }
        
        return studentRepository.save(student);
    }
    
    /**
     * Delete student
     */
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student", id);
        }
        studentRepository.deleteById(id);
    }
}
