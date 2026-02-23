package com.maplewood.enrollment.validator;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.maplewood.common.enums.EnrollmentStatus;
import com.maplewood.common.enums.SemesterName;
import com.maplewood.common.exception.ScheduleConflictException;
import com.maplewood.course.entity.Course;
import com.maplewood.course.entity.CourseSection;
import com.maplewood.enrollment.entity.CurrentEnrollment;
import com.maplewood.enrollment.validator.enrollment.CapacityValidator;
import com.maplewood.school.entity.Classroom;
import com.maplewood.school.entity.Semester;
import com.maplewood.school.entity.Teacher;
import com.maplewood.student.entity.Student;

/**
 * Unit tests for CapacityValidator
 * Ensures section hasn't reached maximum capacity
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Capacity Validation Tests")
class CapacityValidatorTest {

    @InjectMocks
    private CapacityValidator validator;

    private Student student;
    private Course course;
    private Course prerequisite;
    private CourseSection section;
    private Semester semester;
    private Teacher teacher;
    private Classroom classroom;
    private CurrentEnrollment enrollment;

    @SuppressWarnings("unused")
    @BeforeEach
    void setUp() {
        student = new Student();
        student.setId(1L);
        student.setFirstName("John");
        student.setLastName("Doe");
        student.setGradeLevel(10);
        student.setEnrollmentYear(2023);

        prerequisite = new Course();
        prerequisite.setId(100L);
        prerequisite.setCode("MAT101");
        prerequisite.setName("Algebra I");
        prerequisite.setCredits(new BigDecimal("4"));
        prerequisite.setGradeLevelMin(9);
        prerequisite.setGradeLevelMax(12);
        prerequisite.setSemesterOrder(1);

        course = new Course();
        course.setId(101L);
        course.setCode("MAT102");
        course.setName("Geometry");
        course.setCredits(new BigDecimal("4"));
        course.setGradeLevelMin(9);
        course.setGradeLevelMax(12);
        course.setPrerequisite(prerequisite);
        course.setSemesterOrder(1);

        semester = new Semester();
        semester.setId(1L);
        semester.setName(SemesterName.FALL);
        semester.setYear(2024);
        semester.setOrderInYear(1);

        teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("Ms.");
        teacher.setLastName("Smith");

        classroom = new Classroom();
        classroom.setId(1L);
        classroom.setName("Room 101");

        section = new CourseSection();
        section.setId(1L);
        section.setCourse(course);
        section.setTeacher(teacher);
        section.setClassroom(classroom);
        section.setSemester(semester);
        section.setCapacity(10);
        section.setEnrollmentCount(0);
        section.setVersion(0L);

        enrollment = new CurrentEnrollment();
        enrollment.setId(null);
        enrollment.setStudent(student);
        enrollment.setCourseSection(section);
        enrollment.setSemester(semester);
        enrollment.setStatus(EnrollmentStatus.ENROLLED);
    }

    @Test
    @DisplayName("Should throw ScheduleConflictException when section at capacity")
    @SuppressWarnings("unused")
    void validate_ShouldThrowException_WhenAtCapacity() {
        // Arrange: Section at capacity (10/10)
        section.setEnrollmentCount(10);

        // Act & Assert
        ScheduleConflictException exception = assertThrows(ScheduleConflictException.class, () -> {
            validator.validate(enrollment);
        });
        assertNotNull(exception);
    }

    @Test
    @DisplayName("Should pass validation when section has capacity")
    void validate_ShouldPass_WhenHasCapacity() {
        // Arrange: Section has capacity (5/10)
        section.setEnrollmentCount(5);

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(enrollment));
    }

    @Test
    @DisplayName("Should throw exception when capacity is at exact limit")
    @SuppressWarnings("unused")
    void validate_ShouldThrowException_WhenExactlyAtCapacity() {
        // Arrange: Enrollment count equals capacity
        section.setCapacity(10);
        section.setEnrollmentCount(10);

        // Act & Assert
        ScheduleConflictException exception = assertThrows(ScheduleConflictException.class, () -> {
            validator.validate(enrollment);
        });
        assertNotNull(exception);
    }
}
