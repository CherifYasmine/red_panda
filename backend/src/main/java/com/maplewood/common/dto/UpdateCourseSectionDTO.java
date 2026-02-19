package com.maplewood.common.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for PUT requests (Update)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCourseSectionDTO {
    
    private Long teacherId;  // Can reassign teacher
    private Long classroomId;  // Can change classroom
    
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;  // Can increase/decrease capacity
}