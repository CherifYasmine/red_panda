package com.maplewood.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Integer gradeLevel;
    private Integer enrollmentYear;
    private Integer expectedGraduationYear;
    private String status;
    private String createdAt;
}
