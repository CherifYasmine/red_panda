package com.maplewood.scheduling.service;

import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maplewood.common.dto.CreateCourseSectionMeetingDTO;
import com.maplewood.common.dto.UpdateCourseSectionMeetingDTO;
import com.maplewood.common.exception.ResourceNotFoundException;
import com.maplewood.scheduling.entity.CourseSection;
import com.maplewood.scheduling.entity.CourseSectionMeeting;
import com.maplewood.scheduling.repository.CourseSectionMeetingRepository;
import com.maplewood.scheduling.repository.CourseSectionRepository;
import com.maplewood.scheduling.validator.CourseSectionMeetingValidator;

/**
 * Service for CourseSectionMeeting entity
 */
@Service
public class CourseSectionMeetingService {
    
    @Autowired
    private CourseSectionMeetingRepository meetingRepository;
    
    @Autowired
    private CourseSectionRepository sectionRepository;
    
    @Autowired
    private CourseSectionMeetingValidator validator;
    
    /**
     * Get all meetings
     */
    public List<CourseSectionMeeting> getAllMeetings() {
        return meetingRepository.findAll();
    }
    
    /**
     * Get meeting by ID
     */
    public CourseSectionMeeting getMeetingById(Long id) {
        return meetingRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("CourseSectionMeeting", id));
    }
    
    /**
     * Get all meetings for a specific course section
     */
    public List<CourseSectionMeeting> getMeetingsBySection(CourseSection section) {
        return meetingRepository.findBySection(section);
    }
    
    /**
     * Get all meetings on a specific day of week
     */
    public List<CourseSectionMeeting> getMeetingsByDayOfWeek(Integer dayOfWeek) {
        return meetingRepository.findByDayOfWeek(dayOfWeek);
    }
    
    /**
     * Get meetings that conflict with a specific time range on a specific day
     */
    public List<CourseSectionMeeting> getConflictingMeetings(Integer dayOfWeek, LocalTime startTime, LocalTime endTime) {
        return meetingRepository.findConflictingMeetings(dayOfWeek, startTime, endTime);
    }
    
    /**
     * Get meetings between two times on a specific day
     */
    public List<CourseSectionMeeting> getMeetingsByTimeRange(Integer dayOfWeek, LocalTime startTime, LocalTime endTime) {
        return meetingRepository.findMeetingsByTimeRange(dayOfWeek, startTime, endTime);
    }
    
    /**
     * Count meetings for a section
     */
    public long countMeetingsBySection(CourseSection section) {
        return meetingRepository.countBySection(section);
    }
    
    /**
     * Create new meeting from DTO
     * Loads section entity, creates meeting, validates, and saves
     * All business rule validations run here before persistence
     */
    public CourseSectionMeeting createMeetingFromDTO(CreateCourseSectionMeetingDTO createDTO) {
        // Load section
        CourseSection section = sectionRepository.findById(createDTO.getSectionId())
            .orElseThrow(() -> new ResourceNotFoundException("CourseSection", createDTO.getSectionId()));
        
        // Create entity from DTO values
        CourseSectionMeeting meeting = new CourseSectionMeeting();
        meeting.setSection(section);
        meeting.setDayOfWeekEnum(createDTO.getDayOfWeek());
        meeting.setStartTime(createDTO.getStartTime());
        meeting.setEndTime(createDTO.getEndTime());
        
        // Run all validations
        validator.validate(meeting);
        
        // Save to database
        return meetingRepository.save(meeting);
    }
    
    /**
     * Update existing meeting from DTO
     * Loads existing meeting, updates fields, validates, and saves
     * Only provided fields are updated (null values are skipped)
     */
    public CourseSectionMeeting updateMeetingFromDTO(Long id, UpdateCourseSectionMeetingDTO updateDTO) {
        // Load existing meeting
        CourseSectionMeeting existing = getMeetingById(id);
        
        // Update only provided fields
        if (updateDTO.getDayOfWeek() != null) {
            existing.setDayOfWeekEnum(updateDTO.getDayOfWeek());
        }
        if (updateDTO.getStartTime() != null) {
            existing.setStartTime(updateDTO.getStartTime());
        }
        if (updateDTO.getEndTime() != null) {
            existing.setEndTime(updateDTO.getEndTime());
        }
        
        // Run all validations
        validator.validate(existing);
        
        // Save to database
        return meetingRepository.save(existing);
    }
    
    /**
     * Delete a meeting
     */
    public void deleteMeeting(Long id) {
        CourseSectionMeeting meeting = getMeetingById(id);
        meetingRepository.delete(meeting);
    }
}
