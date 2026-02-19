package com.maplewood.common.mapper;

import com.maplewood.common.dto.CourseSectionDTO;
import com.maplewood.common.dto.CreateCourseSectionDTO;
import com.maplewood.common.dto.UpdateCourseSectionDTO;
import com.maplewood.course.entity.CourseSection;

public class CourseSectionMapper {
    
    /**
     * Convert entity to response DTO (GET)
     * Includes timestamps and full nested objects
     */
    public static CourseSectionDTO toDTO(CourseSection entity) {
        if (entity == null) return null;
        return new CourseSectionDTO(
            entity.getId(),
            entity.getCourse() != null ? CourseMapper.toDTO(entity.getCourse()) : null,
            entity.getTeacher() != null ? TeacherMapper.toDTO(entity.getTeacher()) : null,
            entity.getClassroom() != null ? ClassroomMapper.toDTO(entity.getClassroom()) : null,
            entity.getSemester() != null ? SemesterMapper.toDTO(entity.getSemester()) : null,
            entity.getCapacity(),
            entity.getEnrollmentCount(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
    
    /**
     * Convert create DTO to entity (POST)
     * Only sets fields from form, related entities will be loaded by service
     */
    public static CourseSection toEntityFromCreate(CreateCourseSectionDTO dto) {
        if (dto == null) return null;
        CourseSection entity = new CourseSection();
        entity.setCapacity(dto.getCapacity());
        entity.setEnrollmentCount(0);  // Start with 0 enrollments
        // Course, Teacher, Classroom, Semester will be loaded by service using IDs
        return entity;
    }
    
    /**
     * Convert update DTO to entity (PUT)
     * Only updates provided fields
     */
    public static CourseSection toEntityFromUpdate(UpdateCourseSectionDTO dto) {
        if (dto == null) return null;
        CourseSection entity = new CourseSection();
        if (dto.getTeacherId() != null) {
            // Teacher will be loaded by service
        }
        if (dto.getClassroomId() != null) {
            // Classroom will be loaded by service
        }
        if (dto.getCapacity() != null) {
            entity.setCapacity(dto.getCapacity());
        }
        return entity;
    }
}
