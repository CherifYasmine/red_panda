package com.maplewood.scheduling.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.maplewood.common.enums.DayOfWeek;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "course_section_meetings",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"section_id", "day_of_week", "start_time"}
    )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseSectionMeeting {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "section_id", nullable = false)
    private CourseSection section;
    
    @Column(name = "day_of_week", nullable = false)
    @Min(value = 1, message = "Day of week must be 1-5 (Monday-Friday)")
    @Max(value = 5, message = "Day of week must be 1-5 (Monday-Friday)")
    @NotNull(message = "Day of week cannot be null")
    private Integer dayOfWeek; // 1-5 (Monday-Friday)
    
    @Column(name = "start_time", nullable = false)
    @NotNull(message = "Start time cannot be null")
    private LocalTime startTime;
    
    @Column(name = "end_time", nullable = false)
    @NotNull(message = "End time cannot be null")
    private LocalTime endTime;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Convenience method to get day as enum
     */
    public DayOfWeek getDayOfWeekEnum() {
        return DayOfWeek.fromDayValue(this.dayOfWeek);
    }
    
    /**
     * Convenience method to set day from enum
     */
    public void setDayOfWeekEnum(DayOfWeek dayEnum) {
        this.dayOfWeek = dayEnum.getDayValue();
    }
    
    /**
     * Check if this meeting overlaps with another meeting time
     */
    public boolean overlaps(CourseSectionMeeting other) {
        // Different days = no conflict
        if (!this.dayOfWeek.equals(other.dayOfWeek)) {
            return false;
        }
        
        // Same day: check time overlap
        // Overlap if: this.start < other.end AND this.end > other.start
        return this.startTime.isBefore(other.endTime) && this.endTime.isAfter(other.startTime);
    }
}
