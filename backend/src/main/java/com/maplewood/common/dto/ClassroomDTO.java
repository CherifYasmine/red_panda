package com.maplewood.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomDTO {
    private Long id;
    private String name;
    private RoomTypeDTO roomType;
    private Integer capacity;
    private String equipment;
    private Integer floor;
    private String createdAt;
}
