package com.maplewood.common.mapper;

import com.maplewood.common.dto.CourseDTO;
import com.maplewood.common.enums.CourseType;
import com.maplewood.course.entity.Course;

public class CourseMapper {
    
    public static CourseDTO toDTO(Course entity) {
        if (entity == null) return null;
        return new CourseDTO(
            entity.getId(),
            entity.getCode(),
            entity.getName(),
            entity.getDescription(),
            entity.getCredits(),
            entity.getHoursPerWeek(),
            entity.getSpecialization() != null ? SpecializationMapper.toDTO(entity.getSpecialization()) : null,
            entity.getPrerequisite() != null ? toDTO(entity.getPrerequisite()) : null,
            entity.getCourseType() != null ? entity.getCourseType().name() : null,
            entity.getGradeLevelMin(),
            entity.getGradeLevelMax(),
            entity.getSemesterOrder(),
            entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null
        );
    }
    
    public static Course toEntity(CourseDTO dto) {
        if (dto == null) return null;
        Course entity = new Course();
        entity.setId(dto.getId());
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setCredits(dto.getCredits());
        entity.setHoursPerWeek(dto.getHoursPerWeek());
        entity.setSpecialization(dto.getSpecialization() != null ? SpecializationMapper.toEntity(dto.getSpecialization()) : null);
        entity.setPrerequisite(dto.getPrerequisite() != null ? toEntity(dto.getPrerequisite()) : null);
        entity.setCourseType(dto.getCourseType() != null ? CourseType.valueOf(dto.getCourseType()) : null);
        entity.setGradeLevelMin(dto.getGradeLevelMin());
        entity.setGradeLevelMax(dto.getGradeLevelMax());
        entity.setSemesterOrder(dto.getSemesterOrder());
        return entity;
    }
}
