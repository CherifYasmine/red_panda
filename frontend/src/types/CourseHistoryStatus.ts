/**
 * Course History Status Enum
 * Matches backend CourseHistoryStatus enum
 */
export const CourseHistoryStatus = {
  PASSED: 'PASSED',
  FAILED: 'FAILED',
  INCOMPLETE: 'INCOMPLETE',
  TRANSFERRED: 'TRANSFERRED',
} as const;

export type CourseHistoryStatus = typeof CourseHistoryStatus[keyof typeof CourseHistoryStatus];
