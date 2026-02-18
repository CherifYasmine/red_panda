package com.maplewood.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecializationDTO {
    private Long id;
    private String name;
    private RoomTypeDTO roomType;
    private String description;
}
