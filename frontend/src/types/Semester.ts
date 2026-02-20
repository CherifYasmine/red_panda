/**
 * Semester Entity
 * Maps to semesters table in backend
 */
export interface Semester {
  id: number;
  year: number;
  semesterNumber: number; // 1 for Fall, 2 for Spring
  startDate: string;
  endDate: string;
  createdAt: string;
}
