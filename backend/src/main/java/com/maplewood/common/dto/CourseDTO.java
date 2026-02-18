package com.maplewood.common.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    private Long id;
    private String code;
    private String name;
    private String description;
    private BigDecimal credits;
    private Integer hoursPerWeek;
    private SpecializationDTO specialization;
    private CourseDTO prerequisite;
    private String courseType;
    private Integer gradeLevelMin;
    private Integer gradeLevelMax;
    private Integer semesterOrder;
    private String createdAt;
}
