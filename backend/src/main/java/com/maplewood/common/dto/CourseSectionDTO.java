package com.maplewood.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
