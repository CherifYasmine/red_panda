import type { CourseSection } from './CourseSection';
import type { Semester } from './Semester';
import type { EnrollmentStatus } from './EnrollmentStatus';
import type { CourseHistoryStatus } from './CourseHistoryStatus';
import type { Course } from './Course';

/**
 * Current Enrollment Type
 * Represents an active enrollment of a student in a course section
 */
export interface CurrentEnrollment {
  id: number;
  studentId: number;
  courseSection: CourseSection;
  semester: Semester;
  grade?: string | null; // Final grade (null until course ends)
  status: EnrollmentStatus;
  createdAt: string;
  updatedAt: string;
}

/**
 * Student Course History Type
 * Represents a completed enrollment with final grade
 */
export interface StudentCourseHistory {
  id: number;
  studentId: number;
  course: Course;
  semester: Semester;
  status: CourseHistoryStatus;
  createdAt: string;
}
