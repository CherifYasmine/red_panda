package com.maplewood.course.api;

import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.maplewood.common.dto.CourseSectionMeetingDTO;
import com.maplewood.common.dto.CreateCourseSectionMeetingDTO;
import com.maplewood.common.dto.UpdateCourseSectionMeetingDTO;
import com.maplewood.common.mapper.CourseSectionMeetingMapper;
import com.maplewood.common.util.DTOConverter;
import com.maplewood.course.entity.CourseSectionMeeting;
import com.maplewood.course.service.CourseSectionMeetingService;
import com.maplewood.course.service.CourseSectionService;

import jakarta.validation.Valid;

/**
 * REST Controller for CourseSectionMeeting endpoints
 * Provides CRUD operations and meeting search functionality
 * Validates all scheduling constraints (teacher conflicts, classroom conflicts, hours limits)
 */
@RestController
@RequestMapping("/api/v1/course-section-meetings")
@CrossOrigin(origins = "*")
public class CourseSectionMeetingController {
    
    @Autowired
    private CourseSectionMeetingService meetingService;
    
    @Autowired
    private CourseSectionService sectionService;
    
    /**
     * Get all meetings
     */
    @GetMapping
    public ResponseEntity<List<CourseSectionMeetingDTO>> getAllMeetings() {
        return ResponseEntity.ok(DTOConverter.convertList(meetingService.getAllMeetings(), CourseSectionMeetingMapper::toDTO));
    }
    
    /**
     * Get meeting by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourseSectionMeetingDTO> getMeetingById(@PathVariable Long id) {
        return ResponseEntity.ok(DTOConverter.convert(meetingService.getMeetingById(id), CourseSectionMeetingMapper::toDTO));
    }
    
    /**
     * Get all meetings for a specific course section
     */
    @GetMapping("/search/section/{sectionId}")
    public ResponseEntity<List<CourseSectionMeetingDTO>> getMeetingsBySection(@PathVariable Long sectionId) {
        var section = sectionService.getCourseSectionById(sectionId);
        return ResponseEntity.ok(DTOConverter.convertList(meetingService.getMeetingsBySection(section), CourseSectionMeetingMapper::toDTO));
    }
    
    /**
     * Get all meetings on a specific day of week
     * Query param: dayOfWeek (1-5 for Monday-Friday)
     */
    @GetMapping("/search/day/{dayOfWeek}")
    public ResponseEntity<List<CourseSectionMeetingDTO>> getMeetingsByDayOfWeek(@PathVariable Integer dayOfWeek) {
        return ResponseEntity.ok(DTOConverter.convertList(meetingService.getMeetingsByDayOfWeek(dayOfWeek), CourseSectionMeetingMapper::toDTO));
    }
    
    /**
     * Get meetings that conflict with a specific time range on a specific day
     * Query params: dayOfWeek, startTime (HH:MM), endTime (HH:MM)
     */
    @GetMapping("/search/conflicts")
    public ResponseEntity<List<CourseSectionMeetingDTO>> getConflictingMeetings(
        @RequestParam Integer dayOfWeek,
        @RequestParam String startTime,
        @RequestParam String endTime) {
        
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);
        
        return ResponseEntity.ok(DTOConverter.convertList(
            meetingService.getConflictingMeetings(dayOfWeek, start, end),
            CourseSectionMeetingMapper::toDTO
        ));
    }
    
    /**
     * Get meetings between two times on a specific day
     * Query params: dayOfWeek, startTime (HH:MM), endTime (HH:MM)
     */
    @GetMapping("/search/time-range")
    public ResponseEntity<List<CourseSectionMeetingDTO>> getMeetingsByTimeRange(
        @RequestParam Integer dayOfWeek,
        @RequestParam String startTime,
        @RequestParam String endTime) {
        
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);
        
        return ResponseEntity.ok(DTOConverter.convertList(
            meetingService.getMeetingsByTimeRange(dayOfWeek, start, end),
            CourseSectionMeetingMapper::toDTO
        ));
    }
    
    /**
     * Create new meeting
     * Enforces all validations:
     * - No duplicate meetings for same section/day/time
     * - Start time < End time
     * - Total meeting hours <= course.hoursPerWeek
     * - No teacher/classroom conflicts
     * - Teacher daily hours <= maxDailyHours
     */
    @PostMapping
    public ResponseEntity<CourseSectionMeetingDTO> createMeeting(@Valid @RequestBody CreateCourseSectionMeetingDTO createDTO) {
        CourseSectionMeeting created = meetingService.createMeetingFromDTO(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(CourseSectionMeetingMapper.toDTO(created));
    }
    
    /**
     * Update existing meeting
     * Enforces all validations on update
     * Only provided fields are updated (null values are skipped)
     */
    @PutMapping("/{id}")
    public ResponseEntity<CourseSectionMeetingDTO> updateMeeting(
        @PathVariable Long id,
        @Valid @RequestBody UpdateCourseSectionMeetingDTO updateDTO) {
        
        CourseSectionMeeting updated = meetingService.updateMeetingFromDTO(id, updateDTO);
        return ResponseEntity.ok(CourseSectionMeetingMapper.toDTO(updated));
    }
    
    /**
     * Delete a meeting
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeeting(@PathVariable Long id) {
        meetingService.deleteMeeting(id);
        return ResponseEntity.noContent().build();
    }
}
