package com.maplewood.common.mapper;

import com.maplewood.common.dto.StudentDTO;
import com.maplewood.common.enums.StudentStatus;
import com.maplewood.student.entity.Student;

public class StudentMapper {
    
    public static StudentDTO toDTO(Student entity) {
        if (entity == null) return null;
        return new StudentDTO(
            entity.getId(),
            entity.getFirstName(),
            entity.getLastName(),
            entity.getEmail(),
            entity.getGradeLevel(),
            entity.getEnrollmentYear(),
            entity.getExpectedGraduationYear(),
            entity.getStatus() != null ? entity.getStatus().name() : null,
            entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null
        );
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
