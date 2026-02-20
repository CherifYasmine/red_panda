import { create } from 'zustand';
import client from '../api/client';
import type { CurrentEnrollment, StudentCourseHistory } from '../types/Enrollment';
import type { PaginatedResponse } from '../types/common';

interface EnrollmentState {
  currentEnrollments: CurrentEnrollment[] | null;
  courseHistory: StudentCourseHistory[] | null;
  isLoading: boolean;
  error: string | null;

  // Actions
  fetchCurrentEnrollments: (studentId: string) => Promise<void>;
  fetchCourseHistory: (studentId: string) => Promise<void>;
}

/**
 * Enrollment Store - Manages student enrollments and course history
 */
export const useEnrollmentStore = create<EnrollmentState>((set) => ({
  currentEnrollments: null,
  courseHistory: null,
  isLoading: false,
  error: null,

  /**
   * Fetch current enrollments for this semester
   */
  fetchCurrentEnrollments: async (studentId: string) => {
    set({ isLoading: true, error: null });
    try {
      const response = await client.get<CurrentEnrollment[]>(`/enrollments/student/${studentId}`);
      set({
        currentEnrollments: response.data,
        isLoading: false,
      });
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to fetch enrollments';
      set({
        error: errorMessage,
        isLoading: false,
      });
    }
  },

  /**
   * Fetch past course history
   */
  fetchCourseHistory: async (studentId: string) => {
    set({ isLoading: true, error: null });
    try {
      const response = await client.get<PaginatedResponse<StudentCourseHistory>>(`/student-course-history/student/${studentId}`);
      set({
        courseHistory: response.data.content,
        isLoading: false,
      });
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to fetch course history';
      set({
        error: errorMessage,
        isLoading: false,
      });
    }
  },
}));
