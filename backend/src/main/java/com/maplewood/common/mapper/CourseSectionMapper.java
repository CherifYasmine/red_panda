package com.maplewood.common.mapper;

import com.maplewood.common.dto.CourseSectionDTO;
import com.maplewood.scheduling.entity.CourseSection;

public class CourseSectionMapper {
    
    public static CourseSectionDTO toDTO(CourseSection entity) {
        if (entity == null) return null;
        return new CourseSectionDTO(
            entity.getId(),
            entity.getCourse() != null ? CourseMapper.toDTO(entity.getCourse()) : null,
            entity.getTeacher() != null ? TeacherMapper.toDTO(entity.getTeacher()) : null,
            entity.getClassroom() != null ? ClassroomMapper.toDTO(entity.getClassroom()) : null,
            entity.getSemester() != null ? SemesterMapper.toDTO(entity.getSemester()) : null,
            entity.getCapacity(),
            entity.getEnrollmentCount()
        );
    }
    
    public static CourseSection toEntity(CourseSectionDTO dto) {
        if (dto == null) return null;
        CourseSection entity = new CourseSection();
        entity.setId(dto.getId());
        entity.setCourse(dto.getCourse() != null ? CourseMapper.toEntity(dto.getCourse()) : null);
        entity.setTeacher(dto.getTeacher() != null ? TeacherMapper.toEntity(dto.getTeacher()) : null);
        entity.setClassroom(dto.getClassroom() != null ? ClassroomMapper.toEntity(dto.getClassroom()) : null);
        entity.setSemester(dto.getSemester() != null ? SemesterMapper.toEntity(dto.getSemester()) : null);
        entity.setCapacity(dto.getCapacity());
        entity.setEnrollmentCount(dto.getEnrollmentCount());
        return entity;
    }
}
