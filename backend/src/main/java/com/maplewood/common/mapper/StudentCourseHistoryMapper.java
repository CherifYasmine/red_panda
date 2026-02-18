package com.maplewood.common.mapper;

import com.maplewood.common.dto.StudentCourseHistoryDTO;
import com.maplewood.common.enums.CourseHistoryStatus;
import com.maplewood.student.entity.StudentCourseHistory;

public class StudentCourseHistoryMapper {
    
    public static StudentCourseHistoryDTO toDTO(StudentCourseHistory entity) {
        if (entity == null) return null;
        return new StudentCourseHistoryDTO(
            entity.getId(),
            entity.getStudent() != null ? StudentMapper.toDTO(entity.getStudent()) : null,
            entity.getCourse() != null ? CourseMapper.toDTO(entity.getCourse()) : null,
            entity.getSemester() != null ? SemesterMapper.toDTO(entity.getSemester()) : null,
            entity.getStatus() != null ? entity.getStatus().name() : null,
            entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null
        );
    }
    
    public static StudentCourseHistory toEntity(StudentCourseHistoryDTO dto) {
        if (dto == null) return null;
        StudentCourseHistory entity = new StudentCourseHistory();
        entity.setId(dto.getId());
        entity.setStudent(dto.getStudent() != null ? StudentMapper.toEntity(dto.getStudent()) : null);
        entity.setCourse(dto.getCourse() != null ? CourseMapper.toEntity(dto.getCourse()) : null);
        entity.setSemester(dto.getSemester() != null ? SemesterMapper.toEntity(dto.getSemester()) : null);
        entity.setStatus(dto.getStatus() != null ? CourseHistoryStatus.valueOf(dto.getStatus()) : null);
        return entity;
    }
}
