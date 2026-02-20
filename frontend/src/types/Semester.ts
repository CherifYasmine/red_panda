/**
 * Semester Entity
 * Maps to semesters table in backend
 */
export interface Semester {
  id: number;
  name: string; // 'FALL' | 'SPRING'
  year: number;
  orderInYear: number; // 1 for Fall, 2 for Spring
  startDate: string;
  endDate: string;
  isActive: boolean;
  createdAt: string;
}
