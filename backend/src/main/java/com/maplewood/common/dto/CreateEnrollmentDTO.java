package com.maplewood.common.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO for creating a new enrollment
 * Minimal data - just what's needed for enrollment
 */
public record CreateEnrollmentDTO(
    @NotNull(message = "Student ID cannot be null") Long studentId,
    @NotNull(message = "Section ID cannot be null") Long sectionId
) {}
