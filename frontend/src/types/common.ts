/**
 * Common Enums and shared types
 */

/**
 * Day of Week (1-5 for Monday-Friday)
 * Matches backend convention where 1=Monday, 5=Friday
 * Note: Backend only supports Monday-Friday (no weekends)
 */
export const DayOfWeek = {
  MONDAY: 1,
  TUESDAY: 2,
  WEDNESDAY: 3,
  THURSDAY: 4,
  FRIDAY: 5,
} as const;

export type DayOfWeek = typeof DayOfWeek[keyof typeof DayOfWeek];

/**
 * Helper function to get day name from day value
 */
export function getDayName(dayValue: number): string {
  const days: Record<number, string> = {
    1: 'Monday',
    2: 'Tuesday',
    3: 'Wednesday',
    4: 'Thursday',
    5: 'Friday',
  };
  return days[dayValue] || 'Unknown';
}

/**
 * Paginated Response Type
 * Matches Spring Data Page structure
 */
export interface PaginatedResponse<T> {
  content: T[];
  page: {
    size: number;
    number: number;
    totalElements: number;
    totalPages: number;
  };
}
