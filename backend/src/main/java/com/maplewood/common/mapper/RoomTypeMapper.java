package com.maplewood.common.mapper;

import com.maplewood.common.dto.RoomTypeDTO;
import com.maplewood.school.entity.RoomType;

public class RoomTypeMapper {
    
    public static RoomTypeDTO toDTO(RoomType entity) {
        if (entity == null) return null;
        return new RoomTypeDTO(
            entity.getId(),
            entity.getName(),
            entity.getDescription()
        );
    }
    
    public static RoomType toEntity(RoomTypeDTO dto) {
        if (dto == null) return null;
        RoomType entity = new RoomType();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        return entity;
    }
}
