import { useCallback } from 'react';
import { useAuthStore } from '../stores/authStore';

/**
 * Custom hook for managing and fetching student data
 * Provides a way to refresh student info across the app
 */
export function useStudent() {
  const { student, refreshStudent, studentId } = useAuthStore();

  const refresh = useCallback(async () => {
    await refreshStudent();
  }, [refreshStudent]);

  return {
    student,
    studentId,
    refresh,
    isLoading: student === null && studentId !== null,
  };
}
