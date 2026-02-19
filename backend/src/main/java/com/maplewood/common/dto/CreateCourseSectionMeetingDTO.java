package com.maplewood.common.dto;

import java.time.LocalTime;

import com.maplewood.common.enums.DayOfWeek;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for POST requests (Create)
 * Only contains necessary fields for creation, no ID or timestamps
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCourseSectionMeetingDTO {
    
    @NotNull(message = "Section ID cannot be null")
    private Long sectionId;  // Just the ID, not full object
    
    @NotNull(message = "Day of week cannot be null")
    @Min(value = 1)
    private DayOfWeek dayOfWeek;
    
    @NotNull(message = "Start time cannot be null")
    private LocalTime startTime;
    
    @NotNull(message = "End time cannot be null")
    private LocalTime endTime;
}