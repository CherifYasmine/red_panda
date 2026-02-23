package com.maplewood.course.validator.courseSectionMeeting;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.maplewood.course.entity.CourseSectionMeeting;

/**
 * Unit tests for no lunch hour validation
 * Tests that meetings do not overlap with 12:00 PM - 1:00 PM
 */
@DisplayName("Meeting No Lunch Hour Validation Tests")
class NoLunchHourValidatorTest {

    private NoLunchHourValidator validator;
    private CourseSectionMeeting meeting;

    @SuppressWarnings("unused")
    @BeforeEach
    void setUp() {
        validator = new NoLunchHourValidator();
        meeting = new CourseSectionMeeting();
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
