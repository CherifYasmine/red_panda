package com.maplewood.common.mapper;

import com.maplewood.common.dto.StudentDTO;
import com.maplewood.common.enums.StudentStatus;
import com.maplewood.student.entity.Student;

public class StudentMapper {
    
    public static StudentDTO toDTO(Student entity) {
        if (entity == null) return null;
        StudentDTO dto = new StudentDTO();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setEmail(entity.getEmail());
        dto.setGradeLevel(entity.getGradeLevel());
        dto.setEnrollmentYear(entity.getEnrollmentYear());
        dto.setExpectedGraduationYear(entity.getExpectedGraduationYear());
        dto.setStatus(entity.getStatus() != null ? entity.getStatus().name() : null);
        dto.setCreatedAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null);
        // academicMetrics is set by the controller's enrichWithMetrics method
        return dto;
    }
    
    public static Student toEntity(StudentDTO dto) {
        if (dto == null) return null;
        Student entity = new Student();
        entity.setId(dto.getId());
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());
        entity.setGradeLevel(dto.getGradeLevel());
        entity.setEnrollmentYear(dto.getEnrollmentYear());
        entity.setExpectedGraduationYear(dto.getExpectedGraduationYear());
        entity.setStatus(dto.getStatus() != null ? StudentStatus.valueOf(dto.getStatus()) : null);
        return entity;
    }
}
