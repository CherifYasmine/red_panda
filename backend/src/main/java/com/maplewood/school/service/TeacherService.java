package com.maplewood.school.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maplewood.common.exception.ResourceNotFoundException;
import com.maplewood.school.entity.Specialization;
import com.maplewood.school.entity.Teacher;
import com.maplewood.school.repository.SpecializationRepository;
import com.maplewood.school.repository.TeacherRepository;

/**
 * Service for Teacher operations
 * Handles CRUD operations for teachers
 */
@Service
public class TeacherService {
    
    @Autowired
    private TeacherRepository teacherRepository;
    
    @Autowired
    private SpecializationRepository specializationRepository;
    
    /**
     * Get all teachers
     */
    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }
    
    /**
     * Get teacher by ID
     */
    public Teacher getTeacherById(Long id) {
        return teacherRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Teacher", id));
    }
    
    /**
     * Get teacher by email
     */
    public Teacher getTeacherByEmail(String email) {
        Teacher teacher = teacherRepository.findByEmail(email);
        if (teacher == null) {
            throw new ResourceNotFoundException("Teacher", "email", email);
        }
        return teacher;
    }
    
    /**
     * Get all teachers in a specialization
     */
    public List<Teacher> getTeachersBySpecialization(Long specializationId) {
        Specialization specialization = specializationRepository.findById(specializationId)
            .orElseThrow(() -> new ResourceNotFoundException("Specialization not found with id: " + specializationId));
        return teacherRepository.findBySpecialization(specialization);
    }
    
    /**
     * Create new teacher
     */
    public Teacher createTeacher(Teacher teacher) {
        if (teacher.getEmail() != null && teacherRepository.existsByEmail(teacher.getEmail())) {
            throw new IllegalArgumentException("Teacher with email '" + teacher.getEmail() + "' already exists");
        }
        Specialization specialization = specializationRepository.findById(teacher.getSpecialization().getId())
            .orElseThrow(() -> new ResourceNotFoundException("Specialization not found"));
        teacher.setSpecialization(specialization);
        return teacherRepository.save(teacher);
    }
    
    /**
     * Update teacher
     */
    public Teacher updateTeacher(Long id, Teacher teacherDetails) {
        Teacher teacher = getTeacherById(id);
        teacher.setFirstName(teacherDetails.getFirstName());
        teacher.setLastName(teacherDetails.getLastName());
        teacher.setEmail(teacherDetails.getEmail());
        teacher.setMaxDailyHours(teacherDetails.getMaxDailyHours());
        if (teacherDetails.getSpecialization() != null && teacherDetails.getSpecialization().getId() != null) {
            Specialization specialization = specializationRepository.findById(teacherDetails.getSpecialization().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Specialization not found"));
            teacher.setSpecialization(specialization);
        }
        return teacherRepository.save(teacher);
    }
    
    /**
     * Delete teacher
     */
    public void deleteTeacher(Long id) {
        Teacher teacher = getTeacherById(id);
        teacherRepository.delete(teacher);
    }
}
