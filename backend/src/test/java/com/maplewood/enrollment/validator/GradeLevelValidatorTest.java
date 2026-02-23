package com.maplewood.enrollment.validator;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.maplewood.common.enums.EnrollmentStatus;
import com.maplewood.common.enums.SemesterName;
import com.maplewood.common.exception.EnrollmentValidationException;
import com.maplewood.course.entity.Course;
import com.maplewood.course.entity.CourseSection;
import com.maplewood.enrollment.entity.CurrentEnrollment;
import com.maplewood.enrollment.validator.enrollment.GradeLevelValidator;
import com.maplewood.school.entity.Classroom;
import com.maplewood.school.entity.Semester;
import com.maplewood.school.entity.Teacher;
import com.maplewood.student.entity.Student;

/**
 * Unit tests for GradeLevelValidator
 * Ensures student's grade level is within course's min/max range
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Grade Level Validation Tests")
class GradeLevelValidatorTest {

    @InjectMocks
    private GradeLevelValidator validator;

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
    @DisplayName("Should throw EnrollmentValidationException when grade level too low")
    void validate_ShouldThrowException_WhenGradeLevelTooLow() {
        // Arrange: Student is grade 8, course requires 9-12
        student.setGradeLevel(8);

        // Act & Assert
        EnrollmentValidationException exception = assertThrows(EnrollmentValidationException.class, () -> {
            validator.validate(enrollment);
        });
        
        assertEquals("GRADE_LEVEL_NOT_ALLOWED", exception.getErrorType());
    }

    @Test
    @DisplayName("Should throw EnrollmentValidationException when grade level too high")
    @SuppressWarnings("unused")
    void validate_ShouldThrowException_WhenGradeLevelTooHigh() {
        // Arrange: Student is grade 12, course is for grade 9-10 only
        student.setGradeLevel(12);
        course.setGradeLevelMax(10);

        // Act & Assert
        EnrollmentValidationException exception = assertThrows(EnrollmentValidationException.class, () -> {
            validator.validate(enrollment);
        });
        assertEquals("GRADE_LEVEL_NOT_ALLOWED", exception.getErrorType());
    }

    @Test
    @DisplayName("Should pass validation when grade level is appropriate")
    void validate_ShouldPass_WhenGradeLevelIsValid() {
        // Arrange: Grade 10 is within 9-12
        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(enrollment));
    }
}
