package com.maplewood.catalog.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.maplewood.common.enums.CourseType;
import com.maplewood.school.entity.Specialization;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 10)
    @NotBlank(message = "Course code cannot be blank")
    private String code;
    
    @Column(nullable = false, length = 100)
    @NotBlank(message = "Course name cannot be blank")
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false, precision = 3, scale = 1)
    @DecimalMin(value = "0.1", inclusive = true)
    private BigDecimal credits;
    
    @Column(nullable = false, name = "hours_per_week")
    @Min(2)
    @Max(6)
    private Integer hoursPerWeek;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "specialization_id", nullable = false)
    private Specialization specialization;
    
    @ManyToOne
    @JoinColumn(name = "prerequisite_id")
    private Course prerequisite; // Self-reference for prerequisites
    
    @Column(nullable = false, length = 20)
    @NotNull(message = "Course type cannot be null")
    private CourseType courseType;
    
    @Column(name = "grade_level_min")
    @Min(9)
    @Max(12)
    private Integer gradeLevelMin;
    
    @Column(name = "grade_level_max")
    @Min(9)
    @Max(12)
    private Integer gradeLevelMax;
    
    @Column(name = "semester_order", nullable = false)
    @Min(value = 1, message = "Semester order must be 1 (Fall) or 2 (Spring)")
    @Max(value = 2, message = "Semester order must be 1 (Fall) or 2 (Spring)")
    private Integer semesterOrder;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @AssertTrue(message = "Grade level max must be greater than or equal to grade level min")
    @SuppressWarnings("unused")
    private boolean validateGradeLevelRange() {
        if (gradeLevelMin == null || gradeLevelMax == null) {
            return true;
        }
        return gradeLevelMax >= gradeLevelMin;
    }
}
