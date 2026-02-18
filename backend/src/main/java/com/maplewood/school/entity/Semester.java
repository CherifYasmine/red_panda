package com.maplewood.school.entity;

import org.hibernate.annotations.ColumnDefault;

import com.maplewood.common.enums.SemesterName;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "semesters", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "year"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Semester {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 20)
    @NotNull(message = "Semester name cannot be null")
    private SemesterName name;
    
    @Column(nullable = false)
    private Integer year;
    
    @Column(name = "order_in_year", nullable = false)
    @Min(value = 1, message = "Order in year must be 1 (Fall) or 2 (Spring)")
    @Max(value = 2, message = "Order in year must be 1 (Fall) or 2 (Spring)")
    private Integer orderInYear;
    
    @Column(name = "start_date")
    private String startDate;
    
    @Column(name = "end_date")
    private String endDate;
    
    @ColumnDefault("false")
    @Column(name = "is_active")
    private Boolean isActive;
    
    @Column(name = "created_at", updatable = false, insertable = false)
    private String createdAt;
}
