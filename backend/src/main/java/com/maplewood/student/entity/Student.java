package com.maplewood.student.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import com.maplewood.common.enums.StudentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 50, name = "first_name")
    @NotBlank(message = "First name cannot be blank")
    private String firstName;
    
    @Column(nullable = false, length = 50, name = "last_name")
    @NotBlank(message = "Last name cannot be blank")
    private String lastName;
    
    @Column(unique = true, length = 100)
    @Email
    private String email;
    
    @Column(nullable = false, name = "grade_level")
    @Min(9)
    @Max(12)
    private Integer gradeLevel;
    
    @Column(nullable = false, name = "enrollment_year")
    private Integer enrollmentYear;
    
    @Column(name = "expected_graduation_year")
    private Integer expectedGraduationYear;
    
    @ColumnDefault("'active'")
    @Column(nullable = false, length = 20)
    @NotNull(message = "Status cannot be null")
    private StudentStatus status;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Get full name
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
