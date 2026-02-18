package com.maplewood.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SemesterDTO {
    private Long id;
    private String name;
    private Integer year;
    private Integer orderInYear;
    private String startDate;
    private String endDate;
    private Boolean isActive;
    private String createdAt;
}
