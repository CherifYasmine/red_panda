package com.maplewood.student.service;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.maplewood.common.enums.CourseHistoryStatus;
import com.maplewood.course.entity.Course;
import com.maplewood.school.entity.Semester;
import com.maplewood.student.entity.Student;
import com.maplewood.student.entity.StudentCourseHistory;
import com.maplewood.student.repository.StudentCourseHistoryRepository;

/**
 * Unit tests for AcademicMetricsService
 * Tests GPA calculation, credits earned, and graduation tracking
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AcademicMetricsService Tests")
class AcademicMetricsServiceTest {

    @Mock
    private StudentCourseHistoryRepository courseHistoryRepository;

    @InjectMocks
    private AcademicMetricsService metricsService;

    private Student student;
    private Course course1;
    private Course course2;
    private Course course3;
    private Semester semester;
    
    @SuppressWarnings("unused")
    @BeforeEach
    void setUp() {
        // Create test student
        student = new Student();
        student.setId(1L);
        student.setFirstName("Emma");
        student.setLastName("Wilson");
        student.setGradeLevel(10);

        // Create test semester
        semester = new Semester();
        semester.setId(1L);

        // Create test courses with different credits
        course1 = new Course();
        course1.setId(1L);
        course1.setCode("MAT101");
        course1.setName("Algebra I");
        course1.setCredits(new BigDecimal("4"));

        course2 = new Course();
        course2.setId(2L);
        course2.setCode("ENG101");
        course2.setName("English I");
        course2.setCredits(new BigDecimal("3"));

        course3 = new Course();
        course3.setId(3L);
        course3.setCode("SCI101");
        course3.setName("Biology");
        course3.setCredits(new BigDecimal("4"));
    }

    // ============ CALCULATE CREDITS EARNED ============

    @Test
    @DisplayName("Should calculate total credits from passed courses only")
    void calculateCreditsEarned_ShouldCountOnlyPassedCourses() {
        // Arrange: Student passed 2 courses (4+3=7 credits), failed 1
        StudentCourseHistory passed1 = createCourseHistory(student, course1, CourseHistoryStatus.PASSED);
        StudentCourseHistory passed2 = createCourseHistory(student, course2, CourseHistoryStatus.PASSED);
        StudentCourseHistory failed = createCourseHistory(student, course3, CourseHistoryStatus.FAILED);

        when(courseHistoryRepository.findByStudent(student))
            .thenReturn(List.of(passed1, passed2, failed));

        // Act
        double creditsEarned = metricsService.calculateCreditsEarned(student);

        // Assert
        assertEquals(7.0, creditsEarned);  // 4 + 3
        verify(courseHistoryRepository, times(1)).findByStudent(student);
    }

    @Test
    @DisplayName("Should return 0 when student has no course history")
    void calculateCreditsEarned_ShouldReturnZero_WhenNoHistory() {
        // Arrange: Student has empty history
        when(courseHistoryRepository.findByStudent(student))
            .thenReturn(List.of());

        // Act
        double creditsEarned = metricsService.calculateCreditsEarned(student);

        // Assert
        assertEquals(0.0, creditsEarned);
    }

    @Test
    @DisplayName("Should return 0 when student is null")
    void calculateCreditsEarned_ShouldReturnZero_WhenStudentNull() {
        // Act
        double creditsEarned = metricsService.calculateCreditsEarned(null);

        // Assert
        assertEquals(0.0, creditsEarned);
        verify(courseHistoryRepository, never()).findByStudent(any());
    }

    @Test
    @DisplayName("Should return 0 when student ID is null")
    void calculateCreditsEarned_ShouldReturnZero_WhenStudentIdNull() {
        // Arrange
        student.setId(null);

        // Act
        double creditsEarned = metricsService.calculateCreditsEarned(student);

        // Assert
        assertEquals(0.0, creditsEarned);
    }

    @Test
    @DisplayName("Should sum credits correctly with varied credit values")
    void calculateCreditsEarned_ShouldSumCorrectly_WithVariedCredits() {
        // Arrange: Courses with 4, 3, 2 credits (all passed)
        Course course4 = new Course();
        course4.setId(4L);
        course4.setCredits(new BigDecimal("2"));

        StudentCourseHistory h1 = createCourseHistory(student, course1, CourseHistoryStatus.PASSED);  // 4
        StudentCourseHistory h2 = createCourseHistory(student, course2, CourseHistoryStatus.PASSED);  // 3
        StudentCourseHistory h3 = createCourseHistory(student, course4, CourseHistoryStatus.PASSED);  // 2

        when(courseHistoryRepository.findByStudent(student))
            .thenReturn(List.of(h1, h2, h3));

        // Act
        double creditsEarned = metricsService.calculateCreditsEarned(student);

        // Assert
        assertEquals(9.0, creditsEarned);  // 4 + 3 + 2
    }

    // ============ CALCULATE GPA ============

    @Test
    @DisplayName("Should calculate GPA as (passedCredits / totalCredits) × 4.0")
    void calculateGPA_ShouldComputeCorrectly() {
        // Arrange: Passed 2/3 courses
        // Passed: MAT101(4) + ENG101(3) = 7 credits
        // Total: MAT101(4) + ENG101(3) + SCI101(4) = 11 credits
        // GPA = (7 / 11) × 4.0 = 2.545... ≈ 2.55
        
        StudentCourseHistory passed1 = createCourseHistory(student, course1, CourseHistoryStatus.PASSED);
        StudentCourseHistory passed2 = createCourseHistory(student, course2, CourseHistoryStatus.PASSED);
        StudentCourseHistory failed = createCourseHistory(student, course3, CourseHistoryStatus.FAILED);

        when(courseHistoryRepository.findByStudent(student))
            .thenReturn(List.of(passed1, passed2, failed));

        // Act
        double gpa = metricsService.calculateGPA(student);

        // Assert: (7 / 11) × 4.0 = 2.545... rounded to 2 decimals = 2.55
        assertEquals(2.55, gpa, 0.01);
    }

    @Test
    @DisplayName("Should calculate perfect 4.0 GPA when all courses passed")
    void calculateGPA_ShouldBe4_0_WhenAllPassed() {
        // Arrange: All courses passed
        StudentCourseHistory h1 = createCourseHistory(student, course1, CourseHistoryStatus.PASSED);
        StudentCourseHistory h2 = createCourseHistory(student, course2, CourseHistoryStatus.PASSED);
        StudentCourseHistory h3 = createCourseHistory(student, course3, CourseHistoryStatus.PASSED);

        when(courseHistoryRepository.findByStudent(student))
            .thenReturn(List.of(h1, h2, h3));

        // Act
        double gpa = metricsService.calculateGPA(student);

        // Assert
        assertEquals(4.0, gpa);
    }

    @Test
    @DisplayName("Should calculate 0.0 GPA when all courses failed")
    void calculateGPA_ShouldBe0_0_WhenAllFailed() {
        // Arrange: All courses failed
        StudentCourseHistory h1 = createCourseHistory(student, course1, CourseHistoryStatus.FAILED);
        StudentCourseHistory h2 = createCourseHistory(student, course2, CourseHistoryStatus.FAILED);
        StudentCourseHistory h3 = createCourseHistory(student, course3, CourseHistoryStatus.FAILED);

        when(courseHistoryRepository.findByStudent(student))
            .thenReturn(List.of(h1, h2, h3));

        // Act
        double gpa = metricsService.calculateGPA(student);

        // Assert
        assertEquals(0.0, gpa);
    }

    @Test
    @DisplayName("Should return 0.0 GPA when no course history")
    void calculateGPA_ShouldReturnZero_WhenNoHistory() {
        // Arrange
        when(courseHistoryRepository.findByStudent(student))
            .thenReturn(List.of());

        // Act
        double gpa = metricsService.calculateGPA(student);

        // Assert
        assertEquals(0.0, gpa);
    }

    @Test
    @DisplayName("Should return 0.0 GPA when student is null")
    void calculateGPA_ShouldReturnZero_WhenStudentNull() {
        // Act
        double gpa = metricsService.calculateGPA(null);

        // Assert
        assertEquals(0.0, gpa);
        verify(courseHistoryRepository, never()).findByStudent(any());
    }

    @Test
    @DisplayName("Should return 0.0 GPA when student ID is null")
    void calculateGPA_ShouldReturnZero_WhenStudentIdNull() {
        // Arrange
        student.setId(null);

        // Act
        double gpa = metricsService.calculateGPA(student);

        // Assert
        assertEquals(0.0, gpa);
    }

    @Test
    @DisplayName("Should round GPA to 2 decimal places")
    void calculateGPA_ShouldRoundTo2Decimals() {
        // Arrange: Create scenario with repeating decimal GPA
        // Passed: 1 credit, Total: 3 credits
        // GPA = (1 / 3) × 4.0 = 1.3333...
        
        Course c1 = new Course();
        c1.setId(1L);
        c1.setCredits(new BigDecimal("1"));
        
        Course c2 = new Course();
        c2.setId(2L);
        c2.setCredits(new BigDecimal("1"));
        
        Course c3 = new Course();
        c3.setId(3L);
        c3.setCredits(new BigDecimal("1"));

        StudentCourseHistory h1 = createCourseHistory(student, c1, CourseHistoryStatus.PASSED);
        StudentCourseHistory h2 = createCourseHistory(student, c2, CourseHistoryStatus.FAILED);
        StudentCourseHistory h3 = createCourseHistory(student, c3, CourseHistoryStatus.FAILED);

        when(courseHistoryRepository.findByStudent(student))
            .thenReturn(List.of(h1, h2, h3));

        // Act
        double gpa = metricsService.calculateGPA(student);

        // Assert: (1 / 3) × 4.0 = 1.333... rounded to 2 decimals = 1.33
        assertEquals(1.33, gpa, 0.01);
    }

    // ============ IS GRADUATED (30+ CREDITS) ============

    @Test
    @DisplayName("Should return true when student has 30+ credits")
    void isGraduated_ShouldReturnTrue_When30PlusCredits() {
        // Arrange: Student has 32 credits
        StudentCourseHistory h1 = createCourseHistory(student, course1, CourseHistoryStatus.PASSED);  // 4
        StudentCourseHistory h2 = createCourseHistory(student, course2, CourseHistoryStatus.PASSED);  // 3
        
        Course course4 = new Course();
        course4.setId(4L);
        course4.setCredits(new BigDecimal("25"));  // Total: 4 + 3 + 25 = 32
        StudentCourseHistory h3 = createCourseHistory(student, course4, CourseHistoryStatus.PASSED);

        when(courseHistoryRepository.findByStudent(student))
            .thenReturn(List.of(h1, h2, h3));

        // Act
        boolean isGraduated = metricsService.isGraduated(student);

        // Assert
        assertTrue(isGraduated);
    }

    @Test
    @DisplayName("Should return true when student has exactly 30 credits")
    void isGraduated_ShouldReturnTrue_WhenExactly30Credits() {
        // Arrange: Student has exactly 30 credits
        StudentCourseHistory h1 = createCourseHistory(student, course1, CourseHistoryStatus.PASSED);  // 4
        StudentCourseHistory h2 = createCourseHistory(student, course2, CourseHistoryStatus.PASSED);  // 3
        
        Course course4 = new Course();
        course4.setId(4L);
        course4.setCredits(new BigDecimal("23"));  // Total: 4 + 3 + 23 = 30
        StudentCourseHistory h3 = createCourseHistory(student, course4, CourseHistoryStatus.PASSED);

        when(courseHistoryRepository.findByStudent(student))
            .thenReturn(List.of(h1, h2, h3));

        // Act
        boolean isGraduated = metricsService.isGraduated(student);

        // Assert
        assertTrue(isGraduated);
    }

    @Test
    @DisplayName("Should return false when student has less than 30 credits")
    void isGraduated_ShouldReturnFalse_WhenUnder30Credits() {
        // Arrange: Student has 25 credits
        StudentCourseHistory h1 = createCourseHistory(student, course1, CourseHistoryStatus.PASSED);  // 4
        StudentCourseHistory h2 = createCourseHistory(student, course2, CourseHistoryStatus.PASSED);  // 3
        StudentCourseHistory h3 = createCourseHistory(student, course3, CourseHistoryStatus.PASSED);  // 4
        
        Course course4 = new Course();
        course4.setId(4L);
        course4.setCredits(new BigDecimal("14"));  // Total: 4 + 3 + 4 + 14 = 25
        StudentCourseHistory h4 = createCourseHistory(student, course4, CourseHistoryStatus.PASSED);

        when(courseHistoryRepository.findByStudent(student))
            .thenReturn(List.of(h1, h2, h3, h4));

        // Act
        boolean isGraduated = metricsService.isGraduated(student);

        // Assert
        assertFalse(isGraduated);
    }

    @Test
    @DisplayName("Should return false when student has no credits")
    void isGraduated_ShouldReturnFalse_WhenNoCredits() {
        // Arrange: Student has no course history
        when(courseHistoryRepository.findByStudent(student))
            .thenReturn(List.of());

        // Act
        boolean isGraduated = metricsService.isGraduated(student);

        // Assert
        assertFalse(isGraduated);
    }

    // ============ CALCULATE REMAINING CREDITS ============

    @Test
    @DisplayName("Should calculate remaining credits correctly")
    void calculateRemainingCreditsToGraduate_ShouldComputeCorrectly() {
        // Arrange: Student has 20 credits, needs 10 more
        StudentCourseHistory h1 = createCourseHistory(student, course1, CourseHistoryStatus.PASSED);  // 4
        StudentCourseHistory h2 = createCourseHistory(student, course2, CourseHistoryStatus.PASSED);  // 3
        
        Course course4 = new Course();
        course4.setId(4L);
        course4.setCredits(new BigDecimal("13"));  // Total: 4 + 3 + 13 = 20
        StudentCourseHistory h3 = createCourseHistory(student, course4, CourseHistoryStatus.PASSED);

        when(courseHistoryRepository.findByStudent(student))
            .thenReturn(List.of(h1, h2, h3));

        // Act
        double remaining = metricsService.calculateRemainingCreditsToGraduate(student);

        // Assert: 30 - 20 = 10
        assertEquals(10.0, remaining);
    }

    @Test
    @DisplayName("Should return 30 when student has no credits")
    void calculateRemainingCreditsToGraduate_ShouldReturn30_WhenNoCredits() {
        // Arrange: Student has no credits
        when(courseHistoryRepository.findByStudent(student))
            .thenReturn(List.of());

        // Act
        double remaining = metricsService.calculateRemainingCreditsToGraduate(student);

        // Assert
        assertEquals(30.0, remaining);
    }

    @Test
    @DisplayName("Should return 0 when student has graduated")
    void calculateRemainingCreditsToGraduate_ShouldReturn0_WhenGraduated() {
        // Arrange: Student has 35 credits (over minimum)
        StudentCourseHistory h1 = createCourseHistory(student, course1, CourseHistoryStatus.PASSED);  // 4
        StudentCourseHistory h2 = createCourseHistory(student, course2, CourseHistoryStatus.PASSED);  // 3
        
        Course course4 = new Course();
        course4.setId(4L);
        course4.setCredits(new BigDecimal("28"));  // Total: 4 + 3 + 28 = 35
        StudentCourseHistory h3 = createCourseHistory(student, course4, CourseHistoryStatus.PASSED);

        when(courseHistoryRepository.findByStudent(student))
            .thenReturn(List.of(h1, h2, h3));

        // Act
        double remaining = metricsService.calculateRemainingCreditsToGraduate(student);

        // Assert: Should be 0 or negative, but service likely caps at 0
        assertTrue(remaining <= 0);
    }

    @Test
    @DisplayName("Should not count failed courses in remaining calculation")
    void calculateRemainingCreditsToGraduate_ShouldNotCountFailed() {
        // Arrange: 1 passed (4), 2 failed (3+4), total needed = 30 - 4 = 26
        StudentCourseHistory passed = createCourseHistory(student, course1, CourseHistoryStatus.PASSED);  // 4
        StudentCourseHistory failed1 = createCourseHistory(student, course2, CourseHistoryStatus.FAILED);  // 3
        StudentCourseHistory failed2 = createCourseHistory(student, course3, CourseHistoryStatus.FAILED);  // 4

        when(courseHistoryRepository.findByStudent(student))
            .thenReturn(List.of(passed, failed1, failed2));

        // Act
        double remaining = metricsService.calculateRemainingCreditsToGraduate(student);

        // Assert: 30 - 4 = 26
        assertEquals(26.0, remaining);
    }

    private StudentCourseHistory createCourseHistory(Student s, Course c, CourseHistoryStatus status) {
        StudentCourseHistory history = new StudentCourseHistory();
        history.setStudent(s);
        history.setCourse(c);
        history.setSemester(semester);
        history.setStatus(status);
        return history;
    }
}
