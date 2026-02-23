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

/**
 * Unit tests for uniqueness validation
 * Tests that no duplicate meetings exist for same section/day/time
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Meeting Uniqueness Validation Tests")
class UniquenessValidatorTest {

    @Mock(strictness = Mock.Strictness.LENIENT)
    private CourseSectionMeetingRepository repository;

    @InjectMocks
    private UniquenessValidator validator;

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
    }

    @Test
    @DisplayName("Should pass when no duplicate meeting exists")
    void validateUniqueness_ShouldPass_WhenNoDuplicate() {
        // Arrange: No existing meetings for this section/day/time
        when(repository.findBySection_IdAndDayOfWeekAndStartTime(
                section.getId(), meeting.getDayOfWeek(), meeting.getStartTime()))
            .thenReturn(List.of());

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(meeting));
    }

    @Test
    @DisplayName("Should throw exception when duplicate meeting already exists")
    void validateUniqueness_ShouldThrowException_WhenDuplicateExists() {
        // Arrange: Duplicate meeting exists
        CourseSectionMeeting duplicate = new CourseSectionMeeting();
        duplicate.setId(99L);
        duplicate.setSection(section);
        duplicate.setDayOfWeekEnum(DayOfWeek.MONDAY);
        duplicate.setStartTime(LocalTime.of(9, 0));

        when(repository.findBySection_IdAndDayOfWeekAndStartTime(
                section.getId(), meeting.getDayOfWeek(), meeting.getStartTime()))
            .thenReturn(List.of(duplicate));

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            validator.validate(meeting);
        });

        assertTrue(ex.getMessage().contains("Meeting already exists"));
        assertTrue(ex.getMessage().contains("MONDAY"));
    }

    @Test
    @DisplayName("Should pass when updating and excluding self from duplicate check")
    void validateUniqueness_ShouldPass_WhenUpdatingSelfMeeting() {
        // Arrange: Meeting is being updated (has an ID)
        meeting.setId(1L);
        CourseSectionMeeting existingCopy = new CourseSectionMeeting();
        existingCopy.setId(1L);  // Same ID - this is our meeting being updated
        existingCopy.setSection(section);
        existingCopy.setDayOfWeekEnum(DayOfWeek.MONDAY);
        existingCopy.setStartTime(LocalTime.of(9, 0));

        when(repository.findBySection_IdAndDayOfWeekAndStartTime(
                section.getId(), meeting.getDayOfWeek(), meeting.getStartTime()))
            .thenReturn(List.of(existingCopy));

        // Act & Assert - should not throw (self is filtered out)
        assertDoesNotThrow(() -> validator.validate(meeting));
    }
}
