package com.maplewood.common.mapper;

import com.maplewood.common.dto.CourseSectionMeetingDTO;
import com.maplewood.common.dto.CreateCourseSectionMeetingDTO;
import com.maplewood.common.dto.UpdateCourseSectionMeetingDTO;
import com.maplewood.scheduling.entity.CourseSectionMeeting;

public class CourseSectionMeetingMapper {
    
    /**
     * Convert entity to response DTO (GET)
     * Includes timestamps and full nested objects
     */
    public static CourseSectionMeetingDTO toDTO(CourseSectionMeeting entity) {
        if (entity == null) return null;
        return new CourseSectionMeetingDTO(
            entity.getId(),
            entity.getSection() != null ? CourseSectionMapper.toDTO(entity.getSection()) : null,
            entity.getDayOfWeekEnum(),  // Convert Integer to DayOfWeek enum
            entity.getStartTime(),
            entity.getEndTime(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
    
    /**
     * Convert create DTO to entity (POST)
     * Only sets fields from form, section will be loaded by service
     */
    public static CourseSectionMeeting toEntityFromCreate(CreateCourseSectionMeetingDTO dto) {
        if (dto == null) return null;
        CourseSectionMeeting entity = new CourseSectionMeeting();
        entity.setDayOfWeekEnum(dto.getDayOfWeek());  // Convert DayOfWeek enum to Integer
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        // Section will be loaded by service using sectionId
        return entity;
    }
    
    /**
     * Convert update DTO to entity (PUT)
     * Only updates provided fields
     */
    public static CourseSectionMeeting toEntityFromUpdate(UpdateCourseSectionMeetingDTO dto) {
        if (dto == null) return null;
        CourseSectionMeeting entity = new CourseSectionMeeting();
        if (dto.getDayOfWeek() != null) {
            entity.setDayOfWeekEnum(dto.getDayOfWeek());
        }
        if (dto.getStartTime() != null) {
            entity.setStartTime(dto.getStartTime());
        }
        if (dto.getEndTime() != null) {
            entity.setEndTime(dto.getEndTime());
        }
        return entity;
    }
}
