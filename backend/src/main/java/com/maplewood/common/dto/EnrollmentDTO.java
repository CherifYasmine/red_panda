package com.maplewood.common.dto;

import java.time.LocalDateTime;

/**
 * DTO for enrollment responses
 * Contains full enrollment details with related course/student info
 */
public record EnrollmentDTO(
    Long id,
    StudentDTO student,
    CourseSectionDTO section,
    String grade,
    String status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
