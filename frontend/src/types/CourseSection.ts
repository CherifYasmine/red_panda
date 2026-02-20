import type { DayOfWeek } from './common';
import type { Classroom } from './Classroom';
import type { Course } from './Course';
import type { Teacher } from './Teacher';
import type { Semester } from './Semester';

/**
 * Course Section Meeting Type
 * Represents a single meeting of a course section
 */
export interface CourseSectionMeeting {
  id: number;
  dayOfWeek: DayOfWeek; // 1-5 (Monday-Friday)
  startTime: string; // HH:mm format
  endTime: string; // HH:mm format
  createdAt?: string;
  updatedAt?: string;
}

/**
 * Course Section Type
 * Represents a specific offering of a course in a semester
 */

export interface CourseSection {
  id: number;
  course: Course;
  teacher: Teacher;
  classroom: Classroom;
  semester: Semester;
  capacity: number;
  enrollmentCount: number;
  createdAt: string;
  updatedAt: string;
}
