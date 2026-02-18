package com.maplewood.student.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.maplewood.catalog.entity.Course;
import com.maplewood.common.enums.CourseHistoryStatus;
import com.maplewood.school.entity.Semester;

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
@Table(name = "student_course_history", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "course_id", "semester_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentCourseHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;
    
    @Column(nullable = false, length = 20)
    @NotNull(message = "Status cannot be null")
    private CourseHistoryStatus status;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
