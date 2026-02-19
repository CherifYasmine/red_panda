package com.maplewood.scheduling.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.maplewood.catalog.entity.Course;
import com.maplewood.school.entity.Classroom;
import com.maplewood.school.entity.Semester;
import com.maplewood.school.entity.Teacher;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "course_sections")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseSection {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "classroom_id", nullable = false)
    private Classroom classroom;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;
    
    @Max(10)
    @Column(nullable = false)
    private Integer capacity;
    
    @ColumnDefault("0")
    @Column(name = "enrollment_count", nullable = false)
    private Integer enrollmentCount;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
