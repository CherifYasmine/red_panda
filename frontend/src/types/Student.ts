/**
 * Student Status - Matches backend StudentStatus enum
 * Using const object instead of enum for erasability
 */
export const StudentStatus = {
  ACTIVE: 'ACTIVE',
  INACTIVE: 'INACTIVE',
  GRADUATED: 'GRADUATED',
} as const;

export type StudentStatus = typeof StudentStatus[keyof typeof StudentStatus];

/**
 * Academic Metrics DTO
 */
export interface AcademicMetrics {
  gpa: number;
  creditsEarned: number;
  remainingCreditsToGraduate: number;
  isGraduated: boolean;
}

/**
 * Student Interface
 */
export interface Student {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  gradeLevel: number;
  status: StudentStatus;
  createdAt: string;
  updatedAt: string;
  academicMetrics?: AcademicMetrics;
}

/**
 * Student for Login
 */
export interface StudentLogin {
  studentId: string;
  email: string;
}
