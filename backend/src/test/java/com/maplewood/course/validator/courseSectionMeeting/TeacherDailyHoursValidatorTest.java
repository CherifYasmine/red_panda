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
import com.maplewood.common.exception.ScheduleConflictException;
import com.maplewood.course.entity.Course;
import com.maplewood.course.entity.CourseSection;
import com.maplewood.course.entity.CourseSectionMeeting;
import com.maplewood.course.repository.CourseSectionMeetingRepository;
import com.maplewood.course.validator.CourseSectionMeetingValidator;
import com.maplewood.school.entity.Teacher;

/**
 * Unit tests for teacher daily hours validation in CourseSectionMeetingValidator
 * Tests that teacher daily hour limits are enforced
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Meeting Teacher Daily Hours Validation Tests")
class TeacherDailyHoursValidatorTest {

    @Mock(strictness = Mock.Strictness.LENIENT)
    private CourseSectionMeetingRepository repository;

    @InjectMocks
    private CourseSectionMeetingValidator validator;

    private CourseSectionMeeting meeting;
    private CourseSection section;
    private Course course;
    private Teacher teacher;

    @SuppressWarnings("unused")
    @BeforeEach
    void setUp() {
        course = new Course();
        course.setId(101L);
        course.setCode("CS101");
        course.setCourseType(CourseType.CORE);
        course.setHoursPerWeek(4);

        teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("Dr.");
        teacher.setLastName("Smith");
        teacher.setMaxDailyHours(4);  // Teacher can teach 4 hours per day max

        section = new CourseSection();
        section.setId(1L);
        section.setCourse(course);
        section.setTeacher(teacher);

        meeting = new CourseSectionMeeting();
        meeting.setSection(section);
        meeting.setDayOfWeekEnum(DayOfWeek.MONDAY);
        meeting.setStartTime(LocalTime.of(9, 0));
        meeting.setEndTime(LocalTime.of(10, 0));  // 1 hour

        setupMocks();
    }

    private void setupMocks() {
        when(repository.findBySection_IdAndDayOfWeekAndStartTime(
                section.getId(), meeting.getDayOfWeek(), meeting.getStartTime()))
            .thenReturn(List.of());
        when(repository.findBySection_Teacher(teacher)).thenReturn(List.of());
        when(repository.findBySection_Classroom(null)).thenReturn(List.of());
        when(repository.findBySection_TeacherAndDayOfWeek(teacher, meeting.getDayOfWeek()))
            .thenReturn(List.of());
    }

    @Test
    @DisplayName("Should pass when teacher daily hours are within limit")
    void validateTeacherDailyHours_ShouldPass_WhenWithinLimit() {
        // Arrange: Teacher already has 2 hours on MONDAY, max is 4
        CourseSectionMeeting existingMeeting = new CourseSectionMeeting();
        existingMeeting.setId(1L);
        existingMeeting.setDayOfWeekEnum(DayOfWeek.MONDAY);
        existingMeeting.setStartTime(LocalTime.of(13, 0));
        existingMeeting.setEndTime(LocalTime.of(15, 0));  // 2 hours

        when(repository.findBySection_TeacherAndDayOfWeek(teacher, DayOfWeek.MONDAY.getDayValue()))
            .thenReturn(List.of(existingMeeting));

        // New meeting: 1 hour (total = 2 + 1 = 3, within 4-hour limit)
        meeting.setStartTime(LocalTime.of(9, 0));
        meeting.setEndTime(LocalTime.of(10, 0));

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(meeting));
    }

    @Test
    @DisplayName("Should throw exception when teacher would exceed daily hours")
    void validateTeacherDailyHours_ShouldThrowException_WhenExceedsLimit() {
        // Arrange: Teacher already has 3 hours on MONDAY, trying to add 2 more (exceeds 4)
        CourseSectionMeeting existingMeeting = new CourseSectionMeeting();
        existingMeeting.setId(2L);
        existingMeeting.setDayOfWeekEnum(DayOfWeek.MONDAY);
        existingMeeting.setStartTime(LocalTime.of(13, 0));
        existingMeeting.setEndTime(LocalTime.of(16, 0));  // 3 hours

        when(repository.findBySection_TeacherAndDayOfWeek(teacher, DayOfWeek.MONDAY.getDayValue()))
            .thenReturn(List.of(existingMeeting));

        // New meeting: 2 hours (total = 3 + 2 = 5, exceeds 4-hour limit)
        meeting.setStartTime(LocalTime.of(9, 0));
        meeting.setEndTime(LocalTime.of(11, 0));

        // Act & Assert
        ScheduleConflictException ex = assertThrows(ScheduleConflictException.class, () -> {
            validator.validate(meeting);
        });

        assertTrue(ex.getMessage().contains("daily hours") || ex.getMessage().contains("4 hours"));
    }

    @Test
    @DisplayName("Should pass when teacher has no daily hour limit set")
    void validateTeacherDailyHours_ShouldPass_WhenNoLimitSet() {
        // Arrange: Teacher has no max daily hours constraint
        teacher.setMaxDailyHours(null);  // No limit

        CourseSectionMeeting existingMeeting = new CourseSectionMeeting();
        existingMeeting.setId(3L);
        existingMeeting.setDayOfWeekEnum(DayOfWeek.MONDAY);
        existingMeeting.setStartTime(LocalTime.of(8, 0));
        existingMeeting.setEndTime(LocalTime.of(16, 0));  // 8 hours

        when(repository.findBySection_TeacherAndDayOfWeek(teacher, DayOfWeek.MONDAY.getDayValue()))
            .thenReturn(List.of(existingMeeting));

        // New meeting: 2 hours (total = 8 + 2 = 10, but no limit, should pass)
        meeting.setStartTime(LocalTime.of(17, 0));
        meeting.setEndTime(LocalTime.of(19, 0));

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(meeting));
    }

    @Test
    @DisplayName("Should pass when exactly at daily hour limit")
    void validateTeacherDailyHours_ShouldPass_WhenExactlyAtLimit() {
        // Arrange: Teacher has 3 hours, adding 1 more = exactly 4 (at limit)
        CourseSectionMeeting existingMeeting = new CourseSectionMeeting();
        existingMeeting.setId(4L);
        existingMeeting.setDayOfWeekEnum(DayOfWeek.MONDAY);
        existingMeeting.setStartTime(LocalTime.of(13, 0));
        existingMeeting.setEndTime(LocalTime.of(16, 0));  // 3 hours

        when(repository.findBySection_TeacherAndDayOfWeek(teacher, DayOfWeek.MONDAY.getDayValue()))
            .thenReturn(List.of(existingMeeting));

        // New meeting: 1 hour (total = 3 + 1 = 4, exactly at limit, should pass)
        meeting.setStartTime(LocalTime.of(9, 0));
        meeting.setEndTime(LocalTime.of(10, 0));

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(meeting));
    }

    @Test
    @DisplayName("Should pass when teacher has no conflicting meetings on that day")
    void validateTeacherDailyHours_ShouldPass_WhenNoOtherMeetingsOnDay() {
        // Arrange: Teacher has no meetings on MONDAY
        when(repository.findBySection_TeacherAndDayOfWeek(teacher, DayOfWeek.MONDAY.getDayValue()))
            .thenReturn(List.of());

        // New meeting: 1 hour (no existing, well within limit)
        meeting.setStartTime(LocalTime.of(9, 0));
        meeting.setEndTime(LocalTime.of(10, 0));

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(meeting));
    }
}
