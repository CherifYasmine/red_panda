package com.maplewood.common.mapper;

import com.maplewood.common.dto.ClassroomDTO;
import com.maplewood.school.entity.Classroom;

public class ClassroomMapper {
    
    public static ClassroomDTO toDTO(Classroom entity) {
        if (entity == null) return null;
        return new ClassroomDTO(
            entity.getId(),
            entity.getName(),
            entity.getRoomType() != null ? RoomTypeMapper.toDTO(entity.getRoomType()) : null,
            entity.getCapacity(),
            entity.getEquipment(),
            entity.getFloor(),
            entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null
        );
    }
    
    public static Classroom toEntity(ClassroomDTO dto) {
        if (dto == null) return null;
        Classroom entity = new Classroom();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setRoomType(dto.getRoomType() != null ? RoomTypeMapper.toEntity(dto.getRoomType()) : null);
        entity.setCapacity(dto.getCapacity());
        entity.setEquipment(dto.getEquipment());
        entity.setFloor(dto.getFloor());
        return entity;
    }
}
