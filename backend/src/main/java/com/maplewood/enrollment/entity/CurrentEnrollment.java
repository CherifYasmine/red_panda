package com.maplewood.enrollment.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.maplewood.common.enums.EnrollmentStatus;
import com.maplewood.course.entity.CourseSection;
import com.maplewood.school.entity.Semester;
import com.maplewood.student.entity.Student;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "current_enrollments", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "course_section_id", "semester_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentEnrollment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "course_section_id", nullable = false)
    private CourseSection courseSection;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;
    
    @Column(length = 2)
    private String grade;  // Final grade (A, B, C, D, F) - null until course ends
    
    @ColumnDefault("'enrolled'")
    @Column(nullable = false, length = 20)
    @NotNull(message = "Status cannot be null")
    private EnrollmentStatus status;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
