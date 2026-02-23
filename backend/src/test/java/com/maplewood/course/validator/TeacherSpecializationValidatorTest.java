package com.maplewood.course.validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.maplewood.common.enums.SemesterName;
import com.maplewood.course.entity.Course;
import com.maplewood.school.entity.Classroom;
import com.maplewood.school.entity.RoomType;
import com.maplewood.school.entity.Semester;
import com.maplewood.school.entity.Specialization;
import com.maplewood.school.entity.Teacher;

/**
 * Unit tests for teacher specialization validation in CourseSectionValidator
 * Tests that teacher's specialization matches course's specialization
 */
@DisplayName("Teacher Specialization Validation Tests")
class TeacherSpecializationValidatorTest {

    private CourseSectionValidator validator;
    private Course course;
    private Teacher teacher;
    private Classroom classroom;
    private Semester semester;
    private Specialization computerScience;
    private Specialization engineering;
    private RoomType computerLabRoomType;

    @SuppressWarnings("unused")
    @BeforeEach
    void setUp() {
        validator = new CourseSectionValidator();

        // Create specializations
        computerScience = new Specialization();
        computerScience.setId(1L);
        computerScience.setName("Computer Science");

        engineering = new Specialization();
        engineering.setId(2L);
        engineering.setName("Engineering");

        // Create room type
        computerLabRoomType = new RoomType();
        computerLabRoomType.setId(1L);
        computerLabRoomType.setName("Computer Lab");
        computerScience.setRoomType(computerLabRoomType);

        // Create course (Computer Science)
        course = new Course();
        course.setId(101L);
        course.setCode("CS101");
        course.setName("Introduction to Programming");
        course.setSpecialization(computerScience);
        course.setSemesterOrder(1);  // Fall

        // Create teacher (Computer Science)
        teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        teacher.setSpecialization(computerScience);

        // Create classroom
        classroom = new Classroom();
        classroom.setId(1L);
        classroom.setName("Lab A");
        classroom.setRoomType(computerLabRoomType);

        // Create semester
        semester = new Semester();
        semester.setId(1L);
        semester.setName(SemesterName.FALL);
        semester.setYear(2024);
        semester.setOrderInYear(1);
    }

    @Test
    @DisplayName("Should pass when teacher and course have matching specialization")
    void validateTeacherSpecialization_ShouldPass_WhenSpecializationsMatch() {
        // Arrange: Both course and teacher are in Computer Science

        // Act & Assert
        assertDoesNotThrow(() -> {
            validator.validate(course, teacher, classroom, semester);
        });
    }

    @Test
    @DisplayName("Should throw exception when teacher specialization differs from course")
    void validateTeacherSpecialization_ShouldThrowException_WhenSpecializationsMismatch() {
        // Arrange: Teacher is in Engineering, course is in Computer Science
        teacher.setSpecialization(engineering);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            validator.validate(course, teacher, classroom, semester);
        });

        assertTrue(ex.getMessage().contains("specializes in Engineering"));
        assertTrue(ex.getMessage().contains("Computer Science"));
    }

    @Test
    @DisplayName("Should throw exception when course has null specialization")
    void validateTeacherSpecialization_ShouldThrowException_WhenCourseSpecializationNull() {
        // Arrange: Course with no specialization
        course.setSpecialization(null);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            validator.validate(course, teacher, classroom, semester);
        });

        assertTrue(ex.getMessage().contains("must have specializations defined"));
    }

    @Test
    @DisplayName("Should throw exception when teacher has null specialization")
    void validateTeacherSpecialization_ShouldThrowException_WhenTeacherSpecializationNull() {
        // Arrange: Teacher with no specialization
        teacher.setSpecialization(null);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            validator.validate(course, teacher, classroom, semester);
        });

        assertTrue(ex.getMessage().contains("must have specializations defined"));
    }
}
