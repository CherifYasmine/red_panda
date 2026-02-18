package com.maplewood.common.mapper;

import com.maplewood.common.dto.SemesterDTO;
import com.maplewood.school.entity.Semester;

public class SemesterMapper {
    
    public static SemesterDTO toDTO(Semester entity) {
        if (entity == null) return null;
        return new SemesterDTO(
            entity.getId(),
            entity.getName() != null ? entity.getName().name() : null,
            entity.getYear(),
            entity.getOrderInYear(),
            entity.getStartDate(),
            entity.getEndDate(),
            entity.getIsActive() != null && entity.getIsActive(),
            entity.getCreatedAt()
        );
    }
    
    public static Semester toEntity(SemesterDTO dto) {
        if (dto == null) return null;
        Semester entity = new Semester();
        entity.setId(dto.getId());
        entity.setName(dto.getName() != null ? com.maplewood.common.enums.SemesterName.valueOf(dto.getName()) : null);
        entity.setYear(dto.getYear());
        entity.setOrderInYear(dto.getOrderInYear());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setIsActive(dto.getIsActive() != null && dto.getIsActive());
        return entity;
    }
}
