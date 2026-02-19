package com.maplewood.common.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;

import com.maplewood.common.enums.DayOfWeek;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for GET responses (Read-only)
 * Contains full nested objects and metadata
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseSectionMeetingDTO {
    private Long id;
    private CourseSectionDTO section;  // Full nested object for response
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
