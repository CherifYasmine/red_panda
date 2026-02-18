package com.maplewood.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentCourseHistoryDTO {
    private Long id;
    private StudentDTO student;
    private CourseDTO course;
    private SemesterDTO semester;
    private String status;
    private String createdAt;
}
