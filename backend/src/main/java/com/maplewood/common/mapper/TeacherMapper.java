package com.maplewood.common.mapper;

import com.maplewood.common.dto.TeacherDTO;
import com.maplewood.school.entity.Teacher;

public class TeacherMapper {
    
    public static TeacherDTO toDTO(Teacher entity) {
        if (entity == null) return null;
        return new TeacherDTO(
            entity.getId(),
            entity.getFirstName(),
            entity.getLastName(),
            entity.getSpecialization() != null ? SpecializationMapper.toDTO(entity.getSpecialization()) : null,
            entity.getEmail(),
            entity.getMaxDailyHours(),
            entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null
        );
    }
    
    public static Teacher toEntity(TeacherDTO dto) {
        if (dto == null) return null;
        Teacher entity = new Teacher();
        entity.setId(dto.getId());
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setSpecialization(dto.getSpecialization() != null ? SpecializationMapper.toEntity(dto.getSpecialization()) : null);
        entity.setEmail(dto.getEmail());
        entity.setMaxDailyHours(dto.getMaxDailyHours());
        return entity;
    }
}
