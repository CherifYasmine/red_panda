package com.maplewood.school.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "teachers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Teacher {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 50, name = "first_name")
    @NotBlank(message = "First name cannot be blank")
    private String firstName;
    
    @Column(nullable = false, length = 50, name = "last_name")
    @NotBlank(message = "Last name cannot be blank")
    private String lastName;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "specialization_id", nullable = false)
    private Specialization specialization;
    
    @Column(unique = true, length = 100)
    @Email(message = "Email should be valid")
    private String email;
    
    @Column(nullable = false, name = "max_daily_hours")
    @Max(value = 4, message = "Max daily hours cannot exceed 4")
    @Min(value = 1, message = "Max daily hours cannot be less than 1")
    private Integer maxDailyHours = 4;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
