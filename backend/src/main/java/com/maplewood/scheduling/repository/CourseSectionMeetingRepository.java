package com.maplewood.scheduling.repository;

import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.maplewood.scheduling.entity.CourseSection;
import com.maplewood.scheduling.entity.CourseSectionMeeting;

/**
 * Repository for CourseSectionMeeting entity
 * Provides database operations for specific meeting times of course sections
 * Critical for schedule conflict detection during enrollment
 */
@Repository
public interface CourseSectionMeetingRepository extends JpaRepository<CourseSectionMeeting, Long> {
    
    /**
     * Find all meetings for a specific course section
     */
    List<CourseSectionMeeting> findBySection(CourseSection section);
    
    /**
     * Find all meetings on a specific day of week
     */
    List<CourseSectionMeeting> findByDayOfWeek(Integer dayOfWeek);
    
    /**
     * Find all meetings on a specific day (DayOfWeek enum)
     */
    @Query("SELECT csm FROM CourseSectionMeeting csm WHERE csm.dayOfWeek = :dayOfWeek")
    List<CourseSectionMeeting> findByDay(@Param("dayOfWeek") Integer dayOfWeek);
    
    /**
     * Find meetings that overlap with a specific time range on a specific day
     * Used for schedule conflict detection
     */
    @Query("SELECT csm FROM CourseSectionMeeting csm WHERE csm.dayOfWeek = :dayOfWeek AND csm.startTime < :endTime AND csm.endTime > :startTime")
    List<CourseSectionMeeting> findConflictingMeetings(
        @Param("dayOfWeek") Integer dayOfWeek,
        @Param("startTime") LocalTime startTime,
        @Param("endTime") LocalTime endTime
    );
    
    /**
     * Find all meetings between two times on a specific day
     */
    @Query("SELECT csm FROM CourseSectionMeeting csm WHERE csm.dayOfWeek = :dayOfWeek AND csm.startTime >= :startTime AND csm.endTime <= :endTime")
    List<CourseSectionMeeting> findMeetingsByTimeRange(
        @Param("dayOfWeek") Integer dayOfWeek,
        @Param("startTime") LocalTime startTime,
        @Param("endTime") LocalTime endTime
    );
    
    /**
     * Count meetings for a section (should be 1-5 per section typically)
     */
    long countBySection(CourseSection section);
}
