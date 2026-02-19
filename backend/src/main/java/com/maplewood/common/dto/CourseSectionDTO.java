package com.maplewood.common.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for GET responses (Read-only)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseSectionDTO {
    private Long id;
    private CourseDTO course;
    private TeacherDTO teacher;
    private ClassroomDTO classroom;
    private SemesterDTO semester;
    private Integer capacity;
    private Integer enrollmentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
