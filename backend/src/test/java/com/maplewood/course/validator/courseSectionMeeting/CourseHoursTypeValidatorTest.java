package com.maplewood.course.validator.courseSectionMeeting;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.maplewood.common.enums.CourseType;
import com.maplewood.common.enums.DayOfWeek;
import com.maplewood.course.entity.Course;
import com.maplewood.course.entity.CourseSection;
import com.maplewood.course.entity.CourseSectionMeeting;

/**
 * Unit tests for course hours type validation
 * Tests that Core courses are 4-6 hours/week and Elective courses are 2-4 hours/week
 */
@DisplayName("Meeting Course Hours Type Validation Tests")
class CourseHoursTypeValidatorTest {

    private CourseHoursTypeValidator validator;

    private CourseSectionMeeting meeting;
    private CourseSection section;
    private Course course;

    @SuppressWarnings("unused")
    @BeforeEach
    void setUp() {
        validator = new CourseHoursTypeValidator();
        
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
        meeting.setEndTime(LocalTime.of(10, 0));
    }

    @Test
    @DisplayName("Should pass for core course with 4 hours per week")
    void validateCourseHoursType_ShouldPass_ForCoreWith4Hours() {
        // Arrange: Core course with 4 hours/week
        course.setCourseType(CourseType.CORE);
        course.setHoursPerWeek(4);

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(meeting));
    }

    @Test
    @DisplayName("Should pass for core course with 6 hours per week")
    void validateCourseHoursType_ShouldPass_ForCoreWith6Hours() {
        // Arrange: Core course with 6 hours/week
        course.setCourseType(CourseType.CORE);
        course.setHoursPerWeek(6);

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(meeting));
    }

    @Test
    @DisplayName("Should throw exception for core course with less than 4 hours per week")
    void validateCourseHoursType_ShouldThrowException_ForCoreWith3Hours() {
        // Arrange: Core course with invalid 3 hours/week
        course.setCourseType(CourseType.CORE);
        course.setHoursPerWeek(3);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            validator.validate(meeting);
        });

        assertTrue(ex.getMessage().contains("Core courses must be 4-6 hours"));
        assertTrue(ex.getMessage().contains("3 hours"));
    }

    @Test
    @DisplayName("Should throw exception for core course with more than 6 hours per week")
    void validateCourseHoursType_ShouldThrowException_ForCoreWith7Hours() {
        // Arrange: Core course with invalid 7 hours/week
        course.setCourseType(CourseType.CORE);
        course.setHoursPerWeek(7);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            validator.validate(meeting);
        });

        assertTrue(ex.getMessage().contains("Core courses must be 4-6 hours"));
        assertTrue(ex.getMessage().contains("7 hours"));
    }

    @Test
    @DisplayName("Should pass for elective course with 2 hours per week")
    void validateCourseHoursType_ShouldPass_ForElectiveWith2Hours() {
        // Arrange: Elective course with 2 hours/week
        course.setCourseType(CourseType.ELECTIVE);
        course.setHoursPerWeek(2);

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(meeting));
    }

    @Test
    @DisplayName("Should pass for elective course with 4 hours per week")
    void validateCourseHoursType_ShouldPass_ForElectiveWith4Hours() {
        // Arrange: Elective course with 4 hours/week
        course.setCourseType(CourseType.ELECTIVE);
        course.setHoursPerWeek(4);

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(meeting));
    }

    @Test
    @DisplayName("Should throw exception for elective course with less than 2 hours per week")
    void validateCourseHoursType_ShouldThrowException_ForElectiveWith1Hour() {
        // Arrange: Elective course with invalid 1 hour/week
        course.setCourseType(CourseType.ELECTIVE);
        course.setHoursPerWeek(1);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            validator.validate(meeting);
        });

        assertTrue(ex.getMessage().contains("Elective courses must be 2-4 hours"));
        assertTrue(ex.getMessage().contains("1 hours"));
    }

    @Test
    @DisplayName("Should throw exception for elective course with more than 4 hours per week")
    void validateCourseHoursType_ShouldThrowException_ForElectiveWith5Hours() {
        // Arrange: Elective course with invalid 5 hours/week
        course.setCourseType(CourseType.ELECTIVE);
        course.setHoursPerWeek(5);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            validator.validate(meeting);
        });

        assertTrue(ex.getMessage().contains("Elective courses must be 2-4 hours"));
        assertTrue(ex.getMessage().contains("5 hours"));
    }
}
