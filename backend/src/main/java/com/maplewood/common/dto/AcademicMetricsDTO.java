package com.maplewood.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AcademicMetricsDTO {
    private Double gpa;
    private Double creditsEarned;
    private Double remainingCreditsToGraduate;
    private Boolean isGraduated;
}
