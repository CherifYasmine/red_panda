package com.maplewood.common.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for POST requests (Create)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCourseSectionDTO {
    
    @NotNull(message = "Course ID cannot be null")
    private Long courseId;
    
    @NotNull(message = "Teacher ID cannot be null")
    private Long teacherId;
    
    @NotNull(message = "Classroom ID cannot be null")
    private Long classroomId;
    
    @NotNull(message = "Capacity cannot be null")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;
}