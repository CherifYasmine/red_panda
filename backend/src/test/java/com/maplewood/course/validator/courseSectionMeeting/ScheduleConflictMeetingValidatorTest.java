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
import com.maplewood.school.entity.Classroom;
import com.maplewood.school.entity.Teacher;

/**
 * Unit tests for schedule conflict validation
 * Tests that teacher and classroom conflicts are detected
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Meeting Schedule Conflict Validation Tests")
class ScheduleConflictMeetingValidatorTest {

    @Mock(strictness = Mock.Strictness.LENIENT)
    private CourseSectionMeetingRepository repository;

    @InjectMocks
    private ScheduleConflictMeetingValidator validator;

    private CourseSectionMeeting meeting;
    private CourseSection section;
    private Course course;
    private Teacher teacher;
    private Classroom classroom;

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

        classroom = new Classroom();
        classroom.setId(1L);
        classroom.setName("A101");

        section = new CourseSection();
        section.setId(1L);
        section.setCourse(course);
        section.setTeacher(teacher);
        section.setClassroom(classroom);

        meeting = new CourseSectionMeeting();
        meeting.setSection(section);
        meeting.setDayOfWeekEnum(DayOfWeek.MONDAY);
        meeting.setStartTime(LocalTime.of(9, 0));
        meeting.setEndTime(LocalTime.of(10, 0));
    }

    @Test
    @DisplayName("Should pass when teacher has no schedule conflicts")
    void validateScheduleConflict_ShouldPass_WhenTeacherNoConflict() {
        // Arrange: No conflicting meetings at same time
        when(repository.findBySection_TeacherAndDayOfWeek(teacher, DayOfWeek.MONDAY.getDayValue()))
            .thenReturn(List.of());

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(meeting));
    }

    @Test
    @DisplayName("Should throw exception when teacher has conflicting meeting at same time")
    void validateScheduleConflict_ShouldThrowException_WhenTeacherHasConflict() {
        // Arrange: Teacher has meeting from 9:30-10:30 (overlaps with 9:00-10:00)
        CourseSectionMeeting conflictingMeeting = new CourseSectionMeeting();
        conflictingMeeting.setId(2L);
        conflictingMeeting.setDayOfWeekEnum(DayOfWeek.MONDAY);
        conflictingMeeting.setStartTime(LocalTime.of(9, 30));
        conflictingMeeting.setEndTime(LocalTime.of(10, 30));

        when(repository.findBySection_Teacher(teacher))
            .thenReturn(List.of(conflictingMeeting));

        // Act & Assert
        ScheduleConflictException ex = assertThrows(ScheduleConflictException.class, () -> {
            validator.validate(meeting);
        });

        assertTrue(ex.getMessage().contains("Teacher") || ex.getMessage().contains("conflict"));
    }

    @Test
    @DisplayName("Should pass when classroom has no schedule conflicts")
    void validateScheduleConflict_ShouldPass_WhenClassroomNoConflict() {
        // Arrange: Classroom is free at this time
        when(repository.findBySection_Classroom(classroom)).thenReturn(List.of());

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(meeting));
    }

    @Test
    @DisplayName("Should throw exception when classroom is booked at same time")
    void validateScheduleConflict_ShouldThrowException_WhenClassroomBooked() {
        // Arrange: Classroom booked for 9:15-10:15 (overlaps with 9:00-10:00)
        CourseSectionMeeting conflictingMeeting = new CourseSectionMeeting();
        conflictingMeeting.setId(3L);
        conflictingMeeting.setDayOfWeekEnum(DayOfWeek.MONDAY);
        conflictingMeeting.setStartTime(LocalTime.of(9, 15));
        conflictingMeeting.setEndTime(LocalTime.of(10, 15));

        when(repository.findBySection_Classroom(classroom))
            .thenReturn(List.of(conflictingMeeting));

        // Act & Assert
        ScheduleConflictException ex = assertThrows(ScheduleConflictException.class, () -> {
            validator.validate(meeting);
        });

        assertTrue(ex.getMessage().contains("Classroom") || ex.getMessage().contains("conflict"));
    }

    @Test
    @DisplayName("Should pass when both teacher and classroom are free")
    void validateScheduleConflict_ShouldPass_WhenBothFree() {
        // Arrange: No conflicts for either teacher or classroom
        when(repository.findBySection_TeacherAndDayOfWeek(teacher, DayOfWeek.MONDAY.getDayValue()))
            .thenReturn(List.of());
        when(repository.findBySection_Classroom(classroom)).thenReturn(List.of());

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(meeting));
    }

    @Test
    @DisplayName("Should pass when meeting times do not overlap")
    void validateScheduleConflict_ShouldPass_WhenNoTimeOverlap() {
        // Arrange: Existing meeting is 13:00-14:00, new is 9:00-10:00 (no overlap)
        CourseSectionMeeting laterMeeting = new CourseSectionMeeting();
        laterMeeting.setId(4L);
        laterMeeting.setDayOfWeekEnum(DayOfWeek.MONDAY);
        laterMeeting.setStartTime(LocalTime.of(13, 0));
        laterMeeting.setEndTime(LocalTime.of(14, 0));

        when(repository.findBySection_TeacherAndDayOfWeek(teacher, DayOfWeek.MONDAY.getDayValue()))
            .thenReturn(List.of(laterMeeting));
        when(repository.findBySection_Classroom(classroom))
            .thenReturn(List.of(laterMeeting));

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(meeting));
    }
}
