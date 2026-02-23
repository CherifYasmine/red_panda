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
 * Unit tests for semester order validation in CourseSectionValidator
 * Tests that course semester order matches the active semester's order (Fall/Spring)
 */
@DisplayName("Semester Order Validation Tests")
class SemesterOrderValidatorTest {

    private CourseSectionValidator validator;
    private Course course;
    private Semester semester;

    @SuppressWarnings("unused")
    @BeforeEach
    void setUp() {
        validator = new CourseSectionValidator();

        // Create fall course (semester order 1 = fall)
        course = new Course();
        course.setId(101L);
        course.setCode("CS101");
        course.setName("Introduction to Programming");
        course.setSemesterOrder(1);  // Fall course

        // Create fall semester
        semester = new Semester();
        semester.setId(1L);
        semester.setName(SemesterName.FALL);
        semester.setYear(2024);
        semester.setOrderInYear(1);  // Fall = 1
    }

    @Test
    @DisplayName("Should pass when fall course is created in fall semester")
    void validateSemesterOrder_ShouldPass_WhenFallCourseInFallSemester() {
        // Arrange: Course is fall, semester is fall
        Specialization computerScience = new Specialization();
        computerScience.setId(1L);
        computerScience.setName("Computer Science");
        
        RoomType computerLab = new RoomType();
        computerLab.setId(1L);
        computerLab.setName("Computer Lab");
        computerScience.setRoomType(computerLab);
        
        course.setSpecialization(computerScience);
        
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        teacher.setSpecialization(computerScience);
        
        Classroom classroom = new Classroom();
        classroom.setId(1L);
        classroom.setName("Lab A");
        classroom.setRoomType(computerLab);

        // Act & Assert
        assertDoesNotThrow(() -> {
            validator.validate(course, teacher, classroom, semester);
        });
    }

    @Test
    @DisplayName("Should pass when spring course is created in spring semester")
    void validateSemesterOrder_ShouldPass_WhenSpringCourseInStringSemester() {
        // Arrange: Convert to spring course (order 2) and spring semester
        course.setSemesterOrder(2);  // Spring course
        semester.setOrderInYear(2);   // Spring = 2
        semester.setName(SemesterName.SPRING);
        
        Specialization computerScience = new Specialization();
        computerScience.setId(1L);
        computerScience.setName("Computer Science");
        
        RoomType computerLab = new RoomType();
        computerLab.setId(1L);
        computerLab.setName("Computer Lab");
        computerScience.setRoomType(computerLab);
        
        course.setSpecialization(computerScience);
        
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        teacher.setSpecialization(computerScience);
        
        Classroom classroom = new Classroom();
        classroom.setId(1L);
        classroom.setName("Lab A");
        classroom.setRoomType(computerLab);

        // Act & Assert
        assertDoesNotThrow(() -> {
            validator.validate(course, teacher, classroom, semester);
        });
    }

    @Test
    @DisplayName("Should throw exception when fall course attempted in spring semester")
    void validateSemesterOrder_ShouldThrowException_WhenFallCourseInSpringSemester() {
        // Arrange: Fall course but spring semester is active
        course.setSemesterOrder(1);  // Fall course
        semester.setOrderInYear(2);   // Spring semester
        semester.setName(SemesterName.SPRING);
        
        Specialization computerScience = new Specialization();
        computerScience.setId(1L);
        computerScience.setName("Computer Science");
        
        RoomType computerLab = new RoomType();
        computerLab.setId(1L);
        computerLab.setName("Computer Lab");
        computerScience.setRoomType(computerLab);
        
        course.setSpecialization(computerScience);
        
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        teacher.setSpecialization(computerScience);
        
        Classroom classroom = new Classroom();
        classroom.setId(1L);
        classroom.setName("Lab A");
        classroom.setRoomType(computerLab);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            validator.validate(course, teacher, classroom, semester);
        });

        assertTrue(ex.getMessage().contains("Fall course"));
        assertTrue(ex.getMessage().contains("Spring"));
    }

    @Test
    @DisplayName("Should throw exception when spring course attempted in fall semester")
    void validateSemesterOrder_ShouldThrowException_WhenSpringCourseInFallSemester() {
        // Arrange: Spring course but fall semester is active
        course.setSemesterOrder(2);  // Spring course
        semester.setOrderInYear(1);  // Fall semester
        semester.setName(SemesterName.FALL);
        
        Specialization computerScience = new Specialization();
        computerScience.setId(1L);
        computerScience.setName("Computer Science");
        
        RoomType computerLab = new RoomType();
        computerLab.setId(1L);
        computerLab.setName("Computer Lab");
        computerScience.setRoomType(computerLab);
        
        course.setSpecialization(computerScience);
        
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        teacher.setSpecialization(computerScience);
        
        Classroom classroom = new Classroom();
        classroom.setId(1L);
        classroom.setName("Lab A");
        classroom.setRoomType(computerLab);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            validator.validate(course, teacher, classroom, semester);
        });

        assertTrue(ex.getMessage().contains("Spring course"));
        assertTrue(ex.getMessage().contains("Fall"));
    }

    @Test
    @DisplayName("Should throw exception when course has no semester order")
    void validateSemesterOrder_ShouldThrowException_WhenCourseSemesterOrderNull() {
        // Arrange: Course with undefined semester order
        course.setSemesterOrder(null);
        
        Specialization computerScience = new Specialization();
        computerScience.setId(1L);
        computerScience.setName("Computer Science");
        
        RoomType computerLab = new RoomType();
        computerLab.setId(1L);
        computerLab.setName("Computer Lab");
        computerScience.setRoomType(computerLab);
        
        course.setSpecialization(computerScience);
        
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        teacher.setSpecialization(computerScience);
        
        Classroom classroom = new Classroom();
        classroom.setId(1L);
        classroom.setName("Lab A");
        classroom.setRoomType(computerLab);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            validator.validate(course, teacher, classroom, semester);
        });

        assertTrue(ex.getMessage().contains("must have semester order defined"));
    }

    @Test
    @DisplayName("Should throw exception when semester has no order")
    void validateSemesterOrder_ShouldThrowException_WhenSemesterOrderNull() {
        // Arrange: Semester with no order
        semester.setOrderInYear(null);
        
        Specialization computerScience = new Specialization();
        computerScience.setId(1L);
        computerScience.setName("Computer Science");
        
        RoomType computerLab = new RoomType();
        computerLab.setId(1L);
        computerLab.setName("Computer Lab");
        computerScience.setRoomType(computerLab);
        
        course.setSpecialization(computerScience);
        
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        teacher.setSpecialization(computerScience);
        
        Classroom classroom = new Classroom();
        classroom.setId(1L);
        classroom.setName("Lab A");
        classroom.setRoomType(computerLab);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            validator.validate(course, teacher, classroom, semester);
        });

        assertTrue(ex.getMessage().contains("must have semester order defined"));
    }
}
