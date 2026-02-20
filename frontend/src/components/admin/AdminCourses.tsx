import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { THEME } from '../../constants/theme';
import { CourseFilter } from '../course/CourseFilter';
import { useCourseStore } from '../../stores/courseStore';
import type { Course } from '../../types/Course';

const PAGE_SIZE = 10;

export function AdminCourses() {
  const navigate = useNavigate();
  const { courses, totalPages, currentPage, isLoading, error, filters, fetchCourses } = useCourseStore();
  const [pageSize] = useState(PAGE_SIZE);

  // Fetch courses when filters change (always reset to page 0)
  useEffect(() => {
    fetchCourses(0, pageSize, filters);
  }, [filters, pageSize, fetchCourses]);

  // Handle pagination
  const handleNextPage = () => {
    if (currentPage < totalPages - 1) {
      fetchCourses(currentPage + 1, pageSize, filters);
    }
  };

  const handlePrevPage = () => {
    if (currentPage > 0) {
      fetchCourses(currentPage - 1, pageSize, filters);
    }
  };

  const handleGoToPage = (page: number) => {
    if (page >= 0 && page < totalPages) {
      fetchCourses(page, pageSize, filters);
    }
  };

  const handleCourseClick = (courseId: number) => {
    navigate(`/admin/courses/${courseId}`);
  };

  return (
    <div>
      {/* Header */}
      <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-8 mb-8 border-2 ${THEME.colors.borders.light}`}>
        <h1 className={`text-3xl font-bold ${THEME.colors.text.primary} mb-2`}>
          📚 Manage Courses
        </h1>
        <p className={`${THEME.colors.text.secondary} text-lg`}>
          Select a course to manage its sections and meetings
        </p>
      </div>

      {/* Filters */}
      <div className="mb-8">
        <CourseFilter />
      </div>

      {/* Error Message */}
      {error && (
        <div className="mb-6 p-4 bg-red-50 border-l-4 border-red-500 rounded">
          <p className="text-red-700 font-semibold">Error loading courses</p>
          <p className="text-red-600 text-sm">{error}</p>
        </div>
      )}

      {/* Loading State */}
      {isLoading && !courses && (
        <div className="flex justify-center items-center py-20">
          <div className="animate-spin">
            <svg className="w-10 h-10 text-cyan-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.5a8.5 8.5 0 1 1-2.5 5" />
            </svg>
          </div>
          <span className={`ml-3 ${THEME.colors.text.secondary}`}>Loading courses...</span>
        </div>
      )}

      {/* Courses Grid */}
      {courses && courses.length > 0 && (
        <>
          <div className="space-y-4 mb-8">
            {courses.map((course: Course) => (
              <button
                key={course.id}
                onClick={() => handleCourseClick(course.id)}
                className={`w-full p-6 text-left rounded-xl border-2 transition-all ${THEME.colors.backgrounds.card} ${THEME.colors.borders.light} hover:shadow-lg hover:border-cyan-400`}
              >
                <div className="flex justify-between items-start">
                  <div className="flex-1">
                    <h3 className={`text-lg font-bold ${THEME.colors.text.primary} mb-2`}>
                      {course.code} - {course.name}
                    </h3>
                    <p className={`${THEME.colors.text.secondary} text-sm`}>
                      📚 {course.specialization.name} • {course.credits} credits • Grade {course.gradeLevelMin}-{course.gradeLevelMax}
                    </p>
                  </div>
                  <span className="text-2xl ml-4">→</span>
                </div>
              </button>
            ))}
          </div>

          {/* Pagination Controls */}
          <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-6 border-2 ${THEME.colors.borders.light}`}>
            <div className="flex items-center justify-between">
              {/* Previous Button */}
              <button
                onClick={handlePrevPage}
                disabled={currentPage === 0}
                className={`px-4 py-2 rounded-lg font-semibold transition-all ${
                  currentPage === 0
                    ? 'bg-slate-100 text-slate-400 cursor-not-allowed'
                    : `bg-gradient-to-r ${THEME.colors.gradients.button} text-white hover:shadow-md`
                }`}
              >
                ← Previous
              </button>

              {/* Page Info and Navigation */}
              <div className="flex items-center gap-4">
                <span className={`${THEME.colors.text.secondary} font-semibold`}>
                  Page {currentPage + 1} of {totalPages}
                </span>

                {/* Page Dots */}
                <div className="flex gap-2">
                  {Array.from({ length: totalPages }).map((_, i) => (
                    <button
                      key={i}
                      onClick={() => handleGoToPage(i)}
                      className={`h-3 transition-all rounded-full ${
                        i === currentPage
                          ? `bg-gradient-to-r ${THEME.colors.gradients.button} w-8`
                          : `bg-slate-300 w-3 hover:opacity-70`
                      }`}
                    />
                  ))}
                </div>
              </div>

              {/* Next Button */}
              <button
                onClick={handleNextPage}
                disabled={currentPage === totalPages - 1}
                className={`px-4 py-2 rounded-lg font-semibold transition-all ${
                  currentPage === totalPages - 1
                    ? 'bg-slate-100 text-slate-400 cursor-not-allowed'
                    : `bg-gradient-to-r ${THEME.colors.gradients.button} text-white hover:shadow-md`
                }`}
              >
                Next →
              </button>
            </div>
          </div>
        </>
      )}

      {/* No Courses */}
      {!isLoading && courses && courses.length === 0 && (
        <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-12 text-center border-2 ${THEME.colors.borders.light}`}>
          <p className={`${THEME.colors.text.secondary} text-lg`}>
            No courses found matching your filters
          </p>
        </div>
      )}
    </div>
  );
}
