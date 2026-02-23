package com.maplewood.enrollment.validator;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.maplewood.common.enums.DayOfWeek;
import com.maplewood.common.enums.EnrollmentStatus;
import com.maplewood.common.enums.SemesterName;
import com.maplewood.common.exception.ScheduleConflictException;
import com.maplewood.course.entity.Course;
import com.maplewood.course.entity.CourseSection;
import com.maplewood.course.entity.CourseSectionMeeting;
import com.maplewood.course.repository.CourseSectionMeetingRepository;
import com.maplewood.enrollment.entity.CurrentEnrollment;
import com.maplewood.enrollment.repository.CurrentEnrollmentRepository;
import com.maplewood.enrollment.validator.enrollment.ScheduleConflictEnrollmentValidator;
import com.maplewood.school.entity.Classroom;
import com.maplewood.school.entity.Semester;
import com.maplewood.school.entity.Teacher;
import com.maplewood.student.entity.Student;

/**
 * Unit tests for ScheduleConflictEnrollmentValidator
 * Ensures student's new meeting times don't overlap with already enrolled courses
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Schedule Conflict Validation Tests")
class ScheduleConflictValidatorTest {

    @Mock
    private CurrentEnrollmentRepository enrollmentRepository;

    @Mock
    private CourseSectionMeetingRepository meetingRepository;

    @InjectMocks
    private ScheduleConflictEnrollmentValidator validator;

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
    @DisplayName("Should throw ScheduleConflictException when meeting times overlap")
    @SuppressWarnings("unused")
    void validate_ShouldThrowException_WhenTimesOverlap() {
        // Arrange: Create overlapping meetings
        CourseSectionMeeting newMeeting = new CourseSectionMeeting();
        newMeeting.setSection(section);
        newMeeting.setDayOfWeekEnum(DayOfWeek.MONDAY);
        newMeeting.setStartTime(LocalTime.of(9, 0));
        newMeeting.setEndTime(LocalTime.of(10, 0));
        
        CourseSection existingSection = new CourseSection();
        existingSection.setId(2L);
        Course existingCourse = new Course();
        existingCourse.setName("Physics");
        existingSection.setCourse(existingCourse);
        existingSection.setSemester(semester);

        CourseSectionMeeting existingMeeting = new CourseSectionMeeting();
        existingMeeting.setSection(existingSection);
        existingMeeting.setDayOfWeekEnum(DayOfWeek.MONDAY);
        existingMeeting.setStartTime(LocalTime.of(9, 30));  // Overlaps!
        existingMeeting.setEndTime(LocalTime.of(10, 30));
                
        CurrentEnrollment existingEnrollment = new CurrentEnrollment();
        existingEnrollment.setCourseSection(existingSection);
        
        when(enrollmentRepository.findByStudent(student))
            .thenReturn(List.of(existingEnrollment));
        
        when(meetingRepository.findBySection(section))
            .thenReturn(List.of(newMeeting));
        
        when(meetingRepository.findBySection(existingSection))
            .thenReturn(List.of(existingMeeting));

        // Act & Assert
        ScheduleConflictException exception = assertThrows(ScheduleConflictException.class, () -> {
            validator.validate(enrollment);
        });
        assertNotNull(exception);
    }

    @Test
    @DisplayName("Should pass validation when no schedule conflicts")
    void validate_ShouldPass_WhenNoConflicts() {
        // Arrange: Valid scenario with no schedule conflicts
        when(meetingRepository.findBySection(section))
            .thenReturn(List.of());

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(enrollment));
    }

    @Test
    @DisplayName("Should pass validation when no new meetings scheduled")
    void validate_ShouldPass_WhenNoNewMeetings() {
        // Arrange: New section has no meetings
        when(meetingRepository.findBySection(section))
            .thenReturn(List.of());

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(enrollment));
    }
}
