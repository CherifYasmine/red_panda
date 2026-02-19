package com.maplewood.common.dto;

import java.time.LocalTime;

import com.maplewood.common.enums.DayOfWeek;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for PUT requests (Update)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCourseSectionMeetingDTO {
    
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
}