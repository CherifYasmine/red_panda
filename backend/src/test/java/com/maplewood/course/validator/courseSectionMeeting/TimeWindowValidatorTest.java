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
 * Unit tests for time window validation in CourseSectionMeetingValidator
 * Tests that start time < end time
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Meeting Time Window Validation Tests")
class TimeWindowValidatorTest {

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
        meeting.setStartTime(LocalTime.of(9, 0));
        meeting.setEndTime(LocalTime.of(10, 0));

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
