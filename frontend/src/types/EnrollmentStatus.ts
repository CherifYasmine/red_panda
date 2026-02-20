/**
 * Enrollment Status Enum
 * Matches backend EnrollmentStatus enum
 */
export const EnrollmentStatus = {
  ENROLLED: 'ENROLLED',
  WITHDRAWN: 'WITHDRAWN',
  COMPLETED: 'COMPLETED',
  FAILED: 'FAILED',
} as const;

export type EnrollmentStatus = typeof EnrollmentStatus[keyof typeof EnrollmentStatus];
