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

import com.maplewood.common.enums.CourseType;
import com.maplewood.common.enums.DayOfWeek;
import com.maplewood.course.entity.Course;
import com.maplewood.course.entity.CourseSection;
import com.maplewood.course.entity.CourseSectionMeeting;
import com.maplewood.course.repository.CourseSectionMeetingRepository;

/**
 * Unit tests for hours validation
 * Tests that total meeting hours matches course requirement
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Meeting Hours Validation Tests")
class HoursValidationTest {

    @Mock(strictness = Mock.Strictness.LENIENT)
    private CourseSectionMeetingRepository repository;

    @InjectMocks
    private HoursValidationValidator validator;

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
        course.setCourseType(CourseType.CORE);
        course.setHoursPerWeek(4);

        section = new CourseSection();
        section.setId(1L);
        section.setCourse(course);

        meeting = new CourseSectionMeeting();
        meeting.setSection(section);
        meeting.setDayOfWeekEnum(DayOfWeek.MONDAY);
        meeting.setStartTime(LocalTime.of(9, 0));
        meeting.setEndTime(LocalTime.of(10, 0));  // 1 hour
    }

    @Test
    @DisplayName("Should pass when total hours equals requirement")
    void validateHours_ShouldPass_WhenTotalHoursEquals() {
        // Arrange: Course needs 4 hours, add 4 meetings of 1 hour each
        CourseSectionMeeting m1 = new CourseSectionMeeting();
        m1.setId(1L);
        m1.setStartTime(LocalTime.of(9, 0));
        m1.setEndTime(LocalTime.of(10, 0));

        CourseSectionMeeting m2 = new CourseSectionMeeting();
        m2.setId(2L);
        m2.setStartTime(LocalTime.of(10, 0));
        m2.setEndTime(LocalTime.of(11, 0));

        CourseSectionMeeting m3 = new CourseSectionMeeting();
        m3.setId(3L);
        m3.setStartTime(LocalTime.of(11, 0));
        m3.setEndTime(LocalTime.of(12, 0));

        when(repository.findBySection(section)).thenReturn(List.of(m1, m2, m3));

        // New meeting: 1 hour (total = 3 + 1 = 4)
        meeting.setStartTime(LocalTime.of(14, 0));
        meeting.setEndTime(LocalTime.of(15, 0));

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(meeting));
    }

    @Test
    @DisplayName("Should throw exception when total hours exceeds requirement")
    void validateHours_ShouldThrowException_WhenExceedsMaxHours() {
        // Arrange: Course needs 4 hours, but already has 3 hours scheduled
        CourseSectionMeeting m1 = new CourseSectionMeeting();
        m1.setId(1L);
        m1.setStartTime(LocalTime.of(9, 0));
        m1.setEndTime(LocalTime.of(10, 0));

        CourseSectionMeeting m2 = new CourseSectionMeeting();
        m2.setId(2L);
        m2.setStartTime(LocalTime.of(10, 0));
        m2.setEndTime(LocalTime.of(12, 0));  // 2 hours

        when(repository.findBySection(section)).thenReturn(List.of(m1, m2));  // 3 hours total

        // New meeting: 2 hours (total = 3 + 2 = 5, exceeds 4)
        meeting.setStartTime(LocalTime.of(14, 0));
        meeting.setEndTime(LocalTime.of(16, 0));

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            validator.validate(meeting);
        });

        assertTrue(ex.getMessage().contains("exceed"));
        assertTrue(ex.getMessage().contains("4 hours"));
    }

    @Test
    @DisplayName("Should pass when updating existing meeting and excluding it from calculation")
    void validateHours_ShouldPass_WhenUpdatingAndExcludingSelf() {
        // Arrange: Meeting is being updated (has ID)
        meeting.setId(1L);

        CourseSectionMeeting existingMeeting = new CourseSectionMeeting();
        existingMeeting.setId(1L);  // Same ID - will be excluded
        existingMeeting.setStartTime(LocalTime.of(9, 0));
        existingMeeting.setEndTime(LocalTime.of(10, 0));  // 1 hour

        CourseSectionMeeting other = new CourseSectionMeeting();
        other.setId(2L);
        other.setStartTime(LocalTime.of(10, 0));
        other.setEndTime(LocalTime.of(13, 0));  // 3 hours

        when(repository.findBySection(section)).thenReturn(List.of(existingMeeting, other));

        // Update the meeting to 1 hour (after exclusion: 0 + 3 + 1 = 4, equals requirement)
        meeting.setStartTime(LocalTime.of(14, 0));
        meeting.setEndTime(LocalTime.of(15, 0));

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(meeting));
    }

    @Test
    @DisplayName("Should pass when no existing meetings")
    void validateHours_ShouldPass_WhenNoExistingMeetings() {
        // Arrange: This is the first meeting
        when(repository.findBySection(section)).thenReturn(List.of());

        // New meeting: 1 hour (need 4 more)
        meeting.setStartTime(LocalTime.of(9, 0));
        meeting.setEndTime(LocalTime.of(10, 0));

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(meeting));
    }
}
