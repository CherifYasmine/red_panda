package com.maplewood.common.mapper;

import com.maplewood.common.dto.SpecializationDTO;
import com.maplewood.school.entity.Specialization;

public class SpecializationMapper {
    
    public static SpecializationDTO toDTO(Specialization entity) {
        if (entity == null) return null;
        return new SpecializationDTO(
            entity.getId(),
            entity.getName(),
            entity.getRoomType() != null ? RoomTypeMapper.toDTO(entity.getRoomType()) : null,
            entity.getDescription()
        );
    }
    
    public static Specialization toEntity(SpecializationDTO dto) {
        if (dto == null) return null;
        Specialization entity = new Specialization();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setRoomType(dto.getRoomType() != null ? RoomTypeMapper.toEntity(dto.getRoomType()) : null);
        entity.setDescription(dto.getDescription());
        return entity;
    }
}
