import { useState, useEffect } from 'react';
import { useCourseStore } from '../stores/courseStore';
import { THEME } from '../constants/theme';
import { CourseType as CourseTypeEnum } from '../types/CourseType';

interface CourseFilterProps {
  onFilterChange: () => void;
}

const GRADE_LEVELS = [9, 10, 11, 12];
const SEMESTERS = [
  { value: 1, label: 'Fall' },
  { value: 2, label: 'Spring' },
];

export function CourseFilter({ onFilterChange }: CourseFilterProps) {
  const { specializations, filters, setFilters, fetchSpecializations, resetFilters } = useCourseStore();
  const [localFilters, setLocalFilters] = useState(filters);
  const [isOpen, setIsOpen] = useState(false);

  useEffect(() => {
    fetchSpecializations();
  }, [fetchSpecializations]);

  const handleFilterChange = (key: string, value: string | number | boolean | null) => {
    const updated = { ...localFilters, [key]: value };
    setLocalFilters(updated);
    setFilters(updated);
    onFilterChange();
  };

  const handleReset = () => {
    setLocalFilters({});
    resetFilters();
    onFilterChange();
  };

  const hasActiveFilters = Object.values(localFilters).some(v => v !== null && v !== undefined && v !== false);

  return (
    <div className={`${THEME.colors.backgrounds.card} rounded-2xl border-2 ${THEME.colors.borders.light} overflow-hidden`}>
      {/* Filter Header */}
      <button
        onClick={() => setIsOpen(!isOpen)}
        className={`w-full px-6 py-4 flex items-center justify-between hover:bg-slate-50 transition-colors`}
      >
        <div className="flex items-center gap-3">
          <svg className="w-5 h-5 text-cyan-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" />
          </svg>
          <span className={`text-lg font-semibold ${THEME.colors.text.primary}`}>Filters</span>
          {hasActiveFilters && (
            <span className="ml-2 px-2 py-1 bg-cyan-100 text-cyan-700 rounded-full text-xs font-semibold">
              Active
            </span>
          )}
        </div>
        <svg
          className={`w-5 h-5 ${THEME.colors.text.secondary} transition-transform ${isOpen ? 'rotate-180' : ''}`}
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 14l-7 7m0 0l-7-7m7 7V3" />
        </svg>
      </button>

      {/* Filter Content */}
      {isOpen && (
        <div className={`px-6 py-4 border-t-2 ${THEME.colors.borders.light} bg-gradient-to-br from-slate-50 to-slate-100`}>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-6">
            {/* Specialization Filter */}
            <div>
              <label className={`block text-sm font-semibold ${THEME.colors.text.primary} mb-2`}>
                Specialization
              </label>
              <select
                value={localFilters.specialization || ''}
                onChange={(e) => handleFilterChange('specialization', e.target.value ? parseInt(e.target.value) : null)}
                className={`w-full px-3 py-2 border-2 ${THEME.colors.borders.light} rounded-lg focus:outline-none focus:ring-2 focus:ring-cyan-500 ${THEME.colors.text.primary}`}
              >
                <option value="">All Specializations</option>
                {specializations?.map((spec) => (
                  <option key={spec.id} value={spec.id}>
                    {spec.name}
                  </option>
                ))}
              </select>
            </div>

            {/* Course Type Filter */}
            <div>
              <label className={`block text-sm font-semibold ${THEME.colors.text.primary} mb-2`}>
                Course Type
              </label>
              <select
                value={localFilters.type || ''}
                onChange={(e) => handleFilterChange('type', e.target.value ? e.target.value : null)}
                className={`w-full px-3 py-2 border-2 ${THEME.colors.borders.light} rounded-lg focus:outline-none focus:ring-2 focus:ring-cyan-500 ${THEME.colors.text.primary}`}
              >
                <option value="">All Types</option>
                <option value={CourseTypeEnum.CORE}>Core</option>
                <option value={CourseTypeEnum.ELECTIVE}>Elective</option>
              </select>
            </div>

            {/* Grade Level Filter */}
            <div>
              <label className={`block text-sm font-semibold ${THEME.colors.text.primary} mb-2`}>
                Grade Level
              </label>
              <select
                value={localFilters.gradeLevel || ''}
                onChange={(e) => handleFilterChange('gradeLevel', e.target.value ? parseInt(e.target.value) : null)}
                className={`w-full px-3 py-2 border-2 ${THEME.colors.borders.light} rounded-lg focus:outline-none focus:ring-2 focus:ring-cyan-500 ${THEME.colors.text.primary}`}
              >
                <option value="">All Grades</option>
                {GRADE_LEVELS.map((grade) => (
                  <option key={grade} value={grade}>
                    Grade {grade}
                  </option>
                ))}
              </select>
            </div>

            {/* Semester Filter */}
            <div>
              <label className={`block text-sm font-semibold ${THEME.colors.text.primary} mb-2`}>
                Semester
              </label>
              <select
                value={localFilters.semesterOrder || ''}
                onChange={(e) => handleFilterChange('semesterOrder', e.target.value ? parseInt(e.target.value) : null)}
                className={`w-full px-3 py-2 border-2 ${THEME.colors.borders.light} rounded-lg focus:outline-none focus:ring-2 focus:ring-cyan-500 ${THEME.colors.text.primary}`}
              >
                <option value="">All Semesters</option>
                {SEMESTERS.map((sem) => (
                  <option key={sem.value} value={sem.value}>
                    {sem.label}
                  </option>
                ))}
              </select>
            </div>

            {/* Active Only Filter */}
            {/* <div>
              <label className={`block text-sm font-semibold ${THEME.colors.text.primary} mb-2`}>
                Availability
              </label>
              <div className="flex items-center gap-2 h-10">
                <input
                  type="checkbox"
                  checked={localFilters.activeOnly || false}
                  onChange={(e) => handleFilterChange('activeOnly', e.target.checked)}
                  className="w-4 h-4 rounded border-2 border-cyan-300 accent-cyan-600 cursor-pointer"
                />
                <label className={`text-sm ${THEME.colors.text.secondary} cursor-pointer`}>
                  Active sections only
                </label>
              </div>
            </div> */}
          </div>

          {/* Reset Button */}
          {hasActiveFilters && (
            <button
              onClick={handleReset}
              className={`w-full px-4 py-2 border-2 ${THEME.colors.borders.light} ${THEME.colors.text.accent} font-semibold rounded-lg hover:bg-cyan-50 transition-all`}
            >
              Clear Filters
            </button>
          )}
        </div>
      )}
    </div>
  );
}
