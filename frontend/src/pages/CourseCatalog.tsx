import { useState, useEffect } from 'react';
import { useCourseStore } from '../stores/courseStore';
import { CourseCard } from '../components/CourseCard';
import { CourseFilter } from '../components/CourseFilter';
import { THEME } from '../constants/theme';

const PAGE_SIZE = 10;

export function CourseCatalog() {
  const { courses, totalPages, currentPage, isLoading, error, filters, fetchCourses } = useCourseStore();
  const [pageSize] = useState(PAGE_SIZE);

  // Fetch courses on mount
  useEffect(() => {
    fetchCourses(currentPage, pageSize, filters);
  }, [currentPage, pageSize, filters, fetchCourses]);

  // Fetch courses when filters change (reset to page 0)
  const handleFilterChange = () => {
    fetchCourses(0, pageSize, filters);
  };

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

  return (
    <div>
      {/* Header */}
      <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-8 mb-8 border-2 ${THEME.colors.borders.light}`}>
        <h1 className={`text-3xl font-bold ${THEME.colors.text.primary} mb-2`}>
          Course Catalog
        </h1>
        <p className={`${THEME.colors.text.secondary} text-lg`}>
          Browse and enroll in available courses for your academic journey
        </p>
      </div>

      {/* Filters */}
      <div className="mb-8">
        <CourseFilter onFilterChange={handleFilterChange} />
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
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
            {courses.map((course) => (
              <CourseCard key={course.id} course={course} />
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

                {/* Page Number Dots */}
                <div className="flex gap-2">
                  {Array.from({ length: totalPages }, (_, i) => (
                    <button
                      key={i}
                      onClick={() => handleGoToPage(i)}
                      className={`w-10 h-10 rounded-lg font-semibold transition-all ${
                        i === currentPage
                          ? `bg-gradient-to-r ${THEME.colors.gradients.button} text-white`
                          : `border-2 ${THEME.colors.borders.light} ${THEME.colors.text.primary} hover:bg-slate-50`
                      }`}
                    >
                      {i + 1}
                    </button>
                  ))}
                </div>
              </div>

              {/* Next Button */}
              <button
                onClick={handleNextPage}
                disabled={currentPage >= totalPages - 1}
                className={`px-4 py-2 rounded-lg font-semibold transition-all ${
                  currentPage >= totalPages - 1
                    ? 'bg-slate-100 text-slate-400 cursor-not-allowed'
                    : `bg-gradient-to-r ${THEME.colors.gradients.button} text-white hover:shadow-md`
                }`}
              >
                Next →
              </button>
            </div>

            {/* Results Info */}
            <div className={`text-center mt-4 ${THEME.colors.text.muted} text-sm`}>
              Showing {courses.length} courses per page
            </div>
          </div>
        </>
      )}

      {/* Empty State */}
      {courses && courses.length === 0 && !isLoading && (
        <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-12 border-2 ${THEME.colors.borders.light} text-center`}>
          <svg className="w-16 h-16 mx-auto mb-4 text-slate-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6.253v13m0-13C6.5 6.253 2 10.998 2 17s4.5 10.747 10 10.747c5.5 0 10-4.998 10-10.747S17.5 6.253 12 6.253z" />
          </svg>
          <h3 className={`text-lg font-semibold ${THEME.colors.text.primary} mb-2`}>No courses available</h3>
          <p className={THEME.colors.text.secondary}>Try adjusting your filters or check back later</p>
        </div>
      )}
    </div>
  );
}
