package com.maplewood.course.validator.courseSectionMeeting;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.maplewood.common.enums.DayOfWeek;
import com.maplewood.course.entity.CourseSectionMeeting;

/**
 * Unit tests for time window validation
 * Tests that start time < end time
 */
@DisplayName("Meeting Time Window Validation Tests")
class TimeWindowValidatorTest {

    private TimeWindowValidator validator;
    private CourseSectionMeeting meeting;

    @SuppressWarnings("unused")
    @BeforeEach
    void setUp() {
        validator = new TimeWindowValidator();
        meeting = new CourseSectionMeeting();
        meeting.setDayOfWeekEnum(DayOfWeek.MONDAY);
        meeting.setStartTime(LocalTime.of(9, 0));
        meeting.setEndTime(LocalTime.of(10, 0));
    }

    @Test
    @DisplayName("Should pass when start time before end time")
    void validateTimeWindow_ShouldPass_WhenStartBeforeEnd() {
        // Arrange: 9:00 AM - 10:00 AM (valid)
        meeting.setStartTime(LocalTime.of(9, 0));
        meeting.setEndTime(LocalTime.of(10, 0));

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(meeting));
    }

    @Test
    @DisplayName("Should throw exception when end time equals start time")
    void validateTimeWindow_ShouldThrowException_WhenEndEqualsStart() {
        // Arrange: Both at 9:00 AM (invalid)
        meeting.setStartTime(LocalTime.of(9, 0));
        meeting.setEndTime(LocalTime.of(9, 0));

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            validator.validate(meeting);
        });

        assertTrue(ex.getMessage().contains("Start time must be before end time"));
    }

    @Test
    @DisplayName("Should throw exception when end time before start time")
    void validateTimeWindow_ShouldThrowException_WhenEndBeforeStart() {
        // Arrange: 10:00 AM - 9:00 AM (invalid)
        meeting.setStartTime(LocalTime.of(10, 0));
        meeting.setEndTime(LocalTime.of(9, 0));

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            validator.validate(meeting);
        });

        assertTrue(ex.getMessage().contains("Start time must be before end time"));
    }

    @Test
    @DisplayName("Should pass with minute precision")
    void validateTimeWindow_ShouldPass_WithMinutePrecision() {
        // Arrange: 9:30 AM - 10:15 AM
        meeting.setStartTime(LocalTime.of(9, 30));
        meeting.setEndTime(LocalTime.of(10, 15));

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(meeting));
    }
}
