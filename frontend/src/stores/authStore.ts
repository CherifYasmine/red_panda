import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import type { AxiosError } from 'axios';
import client from '../api/client';

interface AuthState {
  studentId: string | null;
  email: string | null;
  isLoggedIn: boolean;
  isLoading: boolean;
  error: string | null;

  // Actions
  login: (studentId: string, email: string) => Promise<void>;
  logout: () => void;
  clearError: () => void;
  restoreSession: () => void;
}

/**
 * Auth Store
 */
export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      studentId: null,
      email: null,
      isLoggedIn: false,
      isLoading: false,
      error: null,

      /**
       * Login - Verify student exists in backend and store credentials
       */
      login: async (studentId: string, email: string) => {
        set({ isLoading: true, error: null });

        try {
          const response = await client.get(`/students/search/email?email=${email}`);
          
          if (response.data.id.toString() === studentId) {
            set({
              studentId,
              email,
              isLoggedIn: true,
              isLoading: false,
              error: null,
            });
          } else {
            throw new Error('Student ID does not match email');
          }
        } catch (err) {
          let errorMessage = 'Login failed';
          
          if (err instanceof Error) {
            if ('response' in err) {
              const axiosErr = err as AxiosError<{ message?: string }>;
              errorMessage = axiosErr.response?.data?.message || axiosErr.message;
            } else {
              errorMessage = err.message;
            }
          }
          
          set({
            isLoading: false,
            error: errorMessage,
          });
          throw err;
        }
      },

      /**
       * Logout
       */
      logout: () => {
        set({
          studentId: null,
          email: null,
          isLoggedIn: false,
          error: null,
        });
      },

      /**
       * Clear error message
       */
      clearError: () => {
        set({ error: null });
      },

      /**
       * Restore session from localStorage on app load
       */
      restoreSession: () => {
        const stored = typeof window !== 'undefined' 
          ? localStorage.getItem('auth-storage')
          : null;
        
        if (stored) {
          try {
            const parsed = JSON.parse(stored);
            if (parsed.state?.studentId && parsed.state?.email) {
              set({
                studentId: parsed.state.studentId,
                email: parsed.state.email,
                isLoggedIn: true,
              });
            }
          } catch (err) {
            console.error('Failed to restore session:', err);
          }
        }
      },
    }),
    {
      name: 'auth-storage',
      partialize: (state) => ({
        studentId: state.studentId,
        email: state.email,
        isLoggedIn: state.isLoggedIn,
      }),
    }
  )
);
