package com.maplewood.common.dto;

/**
 * DTO for updating an enrollment
 * Allows updating the grade after semester ends
 */
public record UpdateEnrollmentDTO(
    String grade  // Optional grade - A, B, C, D, F
) {}
