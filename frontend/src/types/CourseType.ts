/**
 * Course Type Enum
 * Matches backend CourseType enum: CORE, ELECTIVE
 */
export const CourseType = {
  CORE: 'CORE',
  ELECTIVE: 'ELECTIVE',
} as const;

export type CourseType = typeof CourseType[keyof typeof CourseType];
