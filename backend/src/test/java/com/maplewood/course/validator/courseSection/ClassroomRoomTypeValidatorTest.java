package com.maplewood.course.validator.courseSection;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.maplewood.course.entity.Course;
import com.maplewood.course.validator.CourseSectionValidator;
import com.maplewood.school.entity.Classroom;
import com.maplewood.school.entity.RoomType;
import com.maplewood.school.entity.Semester;
import com.maplewood.school.entity.Specialization;
import com.maplewood.school.entity.Teacher;

/**
 * Unit tests for classroom room type validation in CourseSectionValidator
 * Tests that classroom's room type matches course specialization's required room type
 */
@DisplayName("Classroom Room Type Validation Tests")
class ClassroomRoomTypeValidatorTest {

    private CourseSectionValidator validator;
    private Course course;
    private Classroom classroom;
    private Specialization computerScience;
    private RoomType computerLabRoomType;
    private RoomType engineeringLabRoomType;
    
    @SuppressWarnings("unused")
    @BeforeEach
    void setUp() {
        validator = new CourseSectionValidator();

        // Create room types
        computerLabRoomType = new RoomType();
        computerLabRoomType.setId(1L);
        computerLabRoomType.setName("Computer Lab");
        computerLabRoomType.setDescription("Lab with computers");

        engineeringLabRoomType = new RoomType();
        engineeringLabRoomType.setId(2L);
        engineeringLabRoomType.setName("Engineering Lab");
        engineeringLabRoomType.setDescription("Lab with engineering equipment");

        // Create specialization with room type requirement
        computerScience = new Specialization();
        computerScience.setId(1L);
        computerScience.setName("Computer Science");
        computerScience.setRoomType(computerLabRoomType);

        // Create course
        course = new Course();
        course.setId(101L);
        course.setCode("CS101");
        course.setName("Introduction to Programming");
        course.setSpecialization(computerScience);

        // Create classroom with matching room type
        classroom = new Classroom();
        classroom.setId(1L);
        classroom.setName("Lab A");
        classroom.setRoomType(computerLabRoomType);
    }

    @Test
    @DisplayName("Should pass when classroom room type matches course specialization requirement")
    void validateClassroomRoomType_ShouldPass_WhenRoomTypesMatch() {
        // Arrange: Both course and classroom require/have Computer Lab
        course.setSemesterOrder(1);
        
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        teacher.setSpecialization(computerScience);
        
        Semester semester = new Semester();
        semester.setId(1L);
        semester.setOrderInYear(1);

        // Act & Assert
        assertDoesNotThrow(() -> {
            validator.validate(course, teacher, classroom, semester);
        });
    }

    @Test
    @DisplayName("Should throw exception when classroom room type differs from course requirement")
    void validateClassroomRoomType_ShouldThrowException_WhenRoomTypesMismatch() {
        // Arrange: Classroom is Engineering Lab, course requires Computer Lab
        course.setSemesterOrder(1);
        classroom.setRoomType(engineeringLabRoomType);
        
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        teacher.setSpecialization(computerScience);
        
        Semester semester = new Semester();
        semester.setId(1L);
        semester.setOrderInYear(1);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            validator.validate(course, teacher, classroom, semester);
        });

        assertTrue(ex.getMessage().contains("Engineering Lab"));
        assertTrue(ex.getMessage().contains("Computer Lab"));
    }
}
