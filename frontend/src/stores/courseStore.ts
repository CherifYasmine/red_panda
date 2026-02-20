import { create } from 'zustand';
import client from '../api/client';
import type { Course } from '../types/Course';
import type { Specialization } from '../types/Specialization';
import type { PaginatedResponse } from '../types/common';
import type { CourseType } from '../types/CourseType';

interface CourseFilters {
  specialization?: number | null;
  type?: CourseType | null;
  gradeLevel?: number | null;
  semesterOrder?: number | null;
  activeOnly?: boolean;
}

interface CourseState {
  courses: Course[] | null;
  specializations: Specialization[] | null;
  totalPages: number;
  currentPage: number;
  isLoading: boolean;
  error: string | null;
  filters: CourseFilters;

  // Actions
  fetchCourses: (page: number, size: number, filters?: CourseFilters) => Promise<void>;
  fetchSpecializations: () => Promise<void>;
  setFilters: (filters: CourseFilters) => void;
  resetFilters: () => void;
  resetCourses: () => void;
}

/**
 * Course Store - Manages course catalog with pagination and filtering
 */
export const useCourseStore = create<CourseState>((set) => ({
  courses: null,
  specializations: null,
  totalPages: 0,
  currentPage: 0,
  isLoading: false,
  error: null,
  filters: {},

  /**
   * Fetch specializations for filter dropdown
   */
  fetchSpecializations: async () => {
    try {
      const response = await client.get<Specialization[]>('/specializations');
      set({
        specializations: response.data,
      });
    } catch (err) {
      console.error('Failed to fetch specializations:', err);
    }
  },

  /**
   * Fetch courses with pagination and filters
   */
  fetchCourses: async (page: number, size: number, filters?: CourseFilters) => {
    set({ isLoading: true, error: null });
    try {
      const params: Record<string, string | number | boolean> = { page, size };

      // Apply filters if provided
      const filterToUse = filters || {};
      if (filterToUse.specialization) params.specialization = filterToUse.specialization;
      if (filterToUse.type) params.type = filterToUse.type;
      if (filterToUse.gradeLevel) params.gradeLevel = filterToUse.gradeLevel;
      if (filterToUse.semesterOrder) params.semesterOrder = filterToUse.semesterOrder;
      if (filterToUse.activeOnly) params.activeOnly = filterToUse.activeOnly;

      // Use search endpoint if filters are applied, otherwise use regular endpoint
      const hasFilters = Object.keys(filterToUse).length > 0;
      const endpoint = hasFilters ? '/courses/search' : '/courses';

      const response = await client.get<PaginatedResponse<Course>>(endpoint, {
        params,
      });

      set({
        courses: response.data.content,
        totalPages: response.data.page.totalPages,
        currentPage: response.data.page.number,
        isLoading: false,
      });
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to fetch courses';
      set({
        error: errorMessage,
        isLoading: false,
      });
    }
  },

  /**
   * Set filters and fetch courses from page 0
   */
  setFilters: (filters: CourseFilters) => {
    set({ filters });
  },

  /**
   * Reset all filters
   */
  resetFilters: () => {
    set({ filters: {} });
  },

  /**
   * Reset course state
   */
  resetCourses: () => {
    set({
      courses: null,
      totalPages: 0,
      currentPage: 0,
      error: null,
      filters: {},
    });
  },
}));
