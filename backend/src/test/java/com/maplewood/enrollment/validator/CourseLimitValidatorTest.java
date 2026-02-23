package com.maplewood.enrollment.validator;

import java.math.BigDecimal;
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

import com.maplewood.common.enums.CourseHistoryStatus;
import com.maplewood.common.enums.EnrollmentStatus;
import com.maplewood.common.enums.SemesterName;
import com.maplewood.common.exception.ScheduleConflictException;
import com.maplewood.course.entity.Course;
import com.maplewood.course.entity.CourseSection;
import com.maplewood.course.repository.CourseSectionMeetingRepository;
import com.maplewood.enrollment.entity.CurrentEnrollment;
import com.maplewood.enrollment.repository.CurrentEnrollmentRepository;
import com.maplewood.school.entity.Classroom;
import com.maplewood.school.entity.Semester;
import com.maplewood.school.entity.Teacher;
import com.maplewood.student.entity.Student;
import com.maplewood.student.repository.StudentCourseHistoryRepository;

/**
 * Unit tests for course load limit validation (max 5 courses) in CurrentEnrollmentValidator
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Course Limit Validation Tests")
class CourseLimitValidatorTest {

    @Mock(strictness = Mock.Strictness.LENIENT)
    private CurrentEnrollmentRepository enrollmentRepository;

    @Mock(strictness = Mock.Strictness.LENIENT)
    private StudentCourseHistoryRepository courseHistoryRepository;

    @Mock(strictness = Mock.Strictness.LENIENT)
    private CourseSectionMeetingRepository meetingRepository;

    @InjectMocks
    private CurrentEnrollmentValidator validator;

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
    @DisplayName("Should throw ScheduleConflictException when student at course limit")
    void validateCourseLimit_ShouldThrowException_WhenAtLimit() {
        // Arrange: Student already enrolled in 5 courses
        when(enrollmentRepository.countByStudent_IdAndCourse_IdAndSemester_Id(
                student.getId(), course.getId(), semester.getId()))
            .thenReturn(0L);
        
        when(courseHistoryRepository.existsByStudentAndCourseAndStatus(
                student, course, CourseHistoryStatus.PASSED))
            .thenReturn(false);
        
        when(enrollmentRepository.countByStudent_IdAndCourseSection_Semester_Id(
                student.getId(), semester.getId()))
            .thenReturn(5L);  // Already at limit

        // Act & Assert
        ScheduleConflictException ex = assertThrows(ScheduleConflictException.class, () -> {
            validator.validate(enrollment);
        });
        
        assertTrue(ex.getMessage().contains("5 courses"));
    }

    @Test
    @DisplayName("Should pass validation when under course limit")
    void validateCourseLimit_ShouldPass_WhenUnderLimit() {
        // Arrange: Student has 3 courses
        when(enrollmentRepository.countByStudent_IdAndCourse_IdAndSemester_Id(
                student.getId(), course.getId(), semester.getId()))
            .thenReturn(0L);
        
        when(courseHistoryRepository.existsByStudentAndCourseAndStatus(
                student, course, CourseHistoryStatus.PASSED))
            .thenReturn(false);
        
        when(enrollmentRepository.countByStudent_IdAndCourseSection_Semester_Id(
                student.getId(), semester.getId()))
            .thenReturn(3L);  // Under limit
        
        when(courseHistoryRepository.existsByStudentAndCourseAndStatus(
                student, prerequisite, CourseHistoryStatus.PASSED))
            .thenReturn(true);
        
        when(enrollmentRepository.findByStudent(student))
            .thenReturn(List.of());
        
        when(meetingRepository.findBySection(section))
            .thenReturn(List.of());

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(enrollment));
    }
}
