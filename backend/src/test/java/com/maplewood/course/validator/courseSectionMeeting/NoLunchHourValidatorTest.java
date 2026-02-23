package com.maplewood.course.validator.courseSectionMeeting;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.maplewood.common.enums.DayOfWeek;
import com.maplewood.course.entity.Course;
import com.maplewood.course.entity.CourseSection;
import com.maplewood.course.entity.CourseSectionMeeting;
import com.maplewood.course.repository.CourseSectionMeetingRepository;
import com.maplewood.course.validator.CourseSectionMeetingValidator;

/**
 * Unit tests for lunch hour validation in CourseSectionMeetingValidator
 * Tests that meetings cannot overlap with 12:00 PM - 1:00 PM lunch hour
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Meeting No Lunch Hour Validation Tests")
class NoLunchHourValidatorTest {

    @Mock(strictness = Mock.Strictness.LENIENT)
    private CourseSectionMeetingRepository repository;

    @InjectMocks
    private CourseSectionMeetingValidator validator;

    private CourseSectionMeeting meeting;
    private CourseSection section;
    private Course course;

    @SuppressWarnings("unused")
    @BeforeEach
    void setUp() {
        course = new Course();
        course.setId(101L);
        course.setCode("CS101");
        course.setName("Intro to Programming");
        course.setHoursPerWeek(4);

        section = new CourseSection();
        section.setId(1L);
        section.setCourse(course);

        meeting = new CourseSectionMeeting();
        meeting.setSection(section);
        meeting.setDayOfWeekEnum(DayOfWeek.MONDAY);

        setupMocks();
    }

    private void setupMocks() {
        when(repository.findBySection_IdAndDayOfWeekAndStartTime(
                section.getId(), meeting.getDayOfWeek(), meeting.getStartTime()))
            .thenReturn(List.of());
        when(repository.findBySection(section)).thenReturn(List.of());
        when(repository.findBySection_Teacher(null)).thenReturn(List.of());
        when(repository.findBySection_Classroom(null)).thenReturn(List.of());
        when(repository.findBySection_TeacherAndDayOfWeek(null, meeting.getDayOfWeek()))
            .thenReturn(List.of());
    }

    @Test
    @DisplayName("Should pass for morning meeting before lunch")
    void validateNoLunchHour_ShouldPass_ForMorningMeeting() {
        // Arrange: 9:00 AM - 10:00 AM (no lunch overlap)
        meeting.setStartTime(LocalTime.of(9, 0));
        meeting.setEndTime(LocalTime.of(10, 0));

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(meeting));
    }

    @Test
    @DisplayName("Should pass for afternoon meeting after lunch")
    void validateNoLunchHour_ShouldPass_ForAfternoonMeeting() {
        // Arrange: 1:00 PM - 2:00 PM (after lunch)
        meeting.setStartTime(LocalTime.of(13, 0));
        meeting.setEndTime(LocalTime.of(14, 0));

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(meeting));
    }

    @Test
    @DisplayName("Should throw exception for meeting completely in lunch hour")
    void validateNoLunchHour_ShouldThrowException_WhenCompletelyInLunch() {
        // Arrange: 12:15 PM - 12:45 PM (completely in lunch)
        meeting.setStartTime(LocalTime.of(12, 15));
        meeting.setEndTime(LocalTime.of(12, 45));

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            validator.validate(meeting);
        });

        assertTrue(ex.getMessage().contains("lunch hour"));
    }

    @Test
    @DisplayName("Should throw exception when meeting overlaps lunch start")
    void validateNoLunchHour_ShouldThrowException_WhenOverlapsLunchStart() {
        // Arrange: 11:45 AM - 12:15 PM (overlaps lunch start)
        meeting.setStartTime(LocalTime.of(11, 45));
        meeting.setEndTime(LocalTime.of(12, 15));

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            validator.validate(meeting);
        });

        assertTrue(ex.getMessage().contains("lunch hour"));
    }

    @Test
    @DisplayName("Should throw exception when meeting overlaps lunch end")
    void validateNoLunchHour_ShouldThrowException_WhenOverlapsLunchEnd() {
        // Arrange: 12:45 PM - 1:15 PM (overlaps lunch end)
        meeting.setStartTime(LocalTime.of(12, 45));
        meeting.setEndTime(LocalTime.of(13, 15));

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            validator.validate(meeting);
        });

        assertTrue(ex.getMessage().contains("lunch hour"));
    }

    @Test
    @DisplayName("Should pass for meeting ending at exactly noon")
    void validateNoLunchHour_ShouldPass_WhenEndsAtLunchStart() {
        // Arrange: 11:00 AM - 12:00 PM (ends exactly when lunch starts)
        meeting.setStartTime(LocalTime.of(11, 0));
        meeting.setEndTime(LocalTime.of(12, 0));

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(meeting));
    }

    @Test
    @DisplayName("Should pass for meeting starting at exactly 1 PM")
    void validateNoLunchHour_ShouldPass_WhenStartsAtLunchEnd() {
        // Arrange: 1:00 PM - 2:00 PM (starts exactly when lunch ends)
        meeting.setStartTime(LocalTime.of(13, 0));
        meeting.setEndTime(LocalTime.of(14, 0));

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(meeting));
    }
}
