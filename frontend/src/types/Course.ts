import type { Specialization } from './Specialization';
import type { CourseType } from './CourseType';

/**
 * Course Type
 * Maps to courses table in backend
 */
export interface Course {
  id: number;
  code: string;
  name: string;
  description?: string;
  credits: number;
  hoursPerWeek: number;
  specialization: Specialization;
  prerequisite?: Course | null;
  courseType: CourseType;
  gradeLevelMin?: number;
  gradeLevelMax?: number;
  semesterOrder: number; // 1 for Fall, 2 for Spring
  createdAt: string;
}
