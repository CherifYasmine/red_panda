import { useState, useEffect } from 'react';
import { THEME } from '../constants/theme';
import client from '../api/client';
import { useAuthStore } from '../stores/authStore';
import { getErrorMessage } from '../types/BackendError';
import type { StudentCourseHistory } from '../types/Enrollment';

const PAGE_SIZE = 10;

const STATUS_COLORS: Record<string, string> = {
  'PASSED': 'bg-green-100 text-green-800 border-green-300',
  'FAILED': 'bg-red-100 text-red-800 border-red-300',
  'IN_PROGRESS': 'bg-yellow-100 text-yellow-800 border-yellow-300',
  'WITHDRAWN': 'bg-gray-100 text-gray-800 border-gray-300',
};

const STATUS_ICONS: Record<string, string> = {
  'PASSED': '✓',
  'FAILED': '✗',
  'IN_PROGRESS': '→',
  'WITHDRAWN': '◁',
};

export function StudentHistory() {
  const { studentId } = useAuthStore();
  const [history, setHistory] = useState<StudentCourseHistory[]>([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchHistory = async () => {
      try {
        setIsLoading(true);
        setError(null);

        if (!studentId) {
          setError('Student not authenticated');
          return;
        }        
        const res = await client.get<{
          content: StudentCourseHistory[];
          page: {
            number: number;
            size: number;
            totalPages: number;
            totalElements: number;
          };
        }>(`/students/${studentId}/course-history?page=${currentPage}&size=${PAGE_SIZE}`);        
        setHistory(res.data.content || []);
        setTotalPages(res.data.page?.totalPages || 0);
        setTotalElements(res.data.page?.totalElements || 0);
      } catch (err) {
        console.error('Error fetching history:', err);
        setError(getErrorMessage(err, 'Failed to load course history'));
      } finally {
        setIsLoading(false);
      }
    };

    fetchHistory();
  }, [studentId, currentPage]);

  if (isLoading) {
    return (
      <div className="flex justify-center items-center py-20">
        <div className="animate-spin">
          <svg className="w-10 h-10 text-cyan-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.5a8.5 8.5 0 1 1-2.5 5" />
          </svg>
        </div>
        <span className={`ml-3 ${THEME.colors.text.secondary}`}>Loading course history...</span>
      </div>
    );
  }

  const getSemesterLabel = (orderInYear: number): string => {
    return orderInYear === 1 ? 'Fall' : 'Spring';
  };

  const groupedBySemester = history.reduce(
    (acc, course) => {
      const key = `${course.semester.year}-${course.semester.orderInYear}`;
      if (!acc[key]) {
        acc[key] = [];
      }
      acc[key].push(course);
      return acc;
    },
    {} as Record<string, StudentCourseHistory[]>
  );

  // Sort semesters (most recent first)
  const sortedSemesters = Object.keys(groupedBySemester).sort((a, b) => {
    const [yearA, semA] = a.split('-').map(Number);
    const [yearB, semB] = b.split('-').map(Number);
    if (yearB !== yearA) return yearB - yearA;
    return semB - semA;
  });

  return (
    <div className="w-full">
      {/* Header */}
      <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-8 mb-8 border-2 ${THEME.colors.borders.light}`}>
        <h1 className={`text-3xl font-bold ${THEME.colors.text.primary} mb-2`}>Course History</h1>
        <p className={`${THEME.colors.text.secondary}`}>
          {totalElements} course{totalElements !== 1 ? 's' : ''} completed
        </p>
      </div>

      {/* Error */}
      {error && (
        <div className="mb-6 p-4 bg-red-50 border-l-4 border-red-500 rounded">
          <p className="text-red-700 font-semibold">{error}</p>
        </div>
      )}

      {/* Empty State */}
      {totalElements === 0 ? (
        <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-12 border-2 ${THEME.colors.borders.light} text-center`}>
          <div className="mb-4">
            <svg className="w-16 h-16 text-slate-300 mx-auto" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M12 6.253v13m0-13C6.5 6.253 2 10.998 2 17s4.5 10.747 10 10.747c5.5 0 10-4.998 10-10.747S17.5 6.253 12 6.253z" />
            </svg>
          </div>
          <h2 className={`text-xl font-bold ${THEME.colors.text.primary} mb-2`}>No Course History</h2>
          <p className={`${THEME.colors.text.secondary}`}>You haven't completed any courses yet</p>
        </div>
      ) : (
        <div className="space-y-8">
          {sortedSemesters.map((semesterKey) => {
            const [year, semOrder] = semesterKey.split('-').map(Number);
            const semLabel = getSemesterLabel(semOrder);
            const courses = groupedBySemester[semesterKey];

            return (
              <div key={semesterKey}>
                {/* Semester Header */}
                <div className="mb-4">
                  <h2 className={`text-2xl font-bold ${THEME.colors.text.primary}`}>
                    {semLabel} {year}
                  </h2>
                  <p className={`text-sm ${THEME.colors.text.muted}`}>
                    {courses.length} course{courses.length !== 1 ? 's' : ''}
                  </p>
                </div>

                {/* Courses Table */}
                <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-6 border-2 ${THEME.colors.borders.light} overflow-x-auto`}>
                  <table className="w-full">
                    <thead>
                      <tr className="border-b-2 border-slate-200">
                        <th className={`px-4 py-3 text-left text-sm font-semibold ${THEME.colors.text.primary}`}>
                          Course Code
                        </th>
                        <th className={`px-4 py-3 text-left text-sm font-semibold ${THEME.colors.text.primary}`}>
                          Course Name
                        </th>
                        <th className={`px-4 py-3 text-center text-sm font-semibold ${THEME.colors.text.primary}`}>
                          Status
                        </th>
                      </tr>
                    </thead>
                    <tbody>
                      {courses.map((course) => {
                        const statusColor = STATUS_COLORS[course.status] || 'bg-slate-100 text-slate-800';
                        const statusIcon = STATUS_ICONS[course.status] || '•';

                        return (
                          <tr key={course.id} className="border-b border-slate-100 hover:bg-slate-50 transition-colors">
                            <td className={`px-4 py-4 font-bold ${THEME.colors.text.primary}`}>
                              {course.course.code}
                            </td>
                            <td className={`px-4 py-4 ${THEME.colors.text.primary}`}>
                              <div>
                                <p className="font-medium">{course.course.name}</p>
                                <p className={`text-xs ${THEME.colors.text.muted} mt-1`}>
                                  {course.course.credits} credits • {course.course.hoursPerWeek} hours/week
                                </p>
                              </div>
                            </td>
                            <td className={`px-4 py-4 text-center`}>
                              <span className={`inline-flex items-center gap-1 px-3 py-1 rounded-full border-2 text-sm font-semibold ${statusColor}`}>
                                <span>{statusIcon}</span>
                                {course.status.charAt(0).toUpperCase() + course.status.slice(1).toLowerCase().replace('_', ' ')}
                              </span>
                            </td>
                          </tr>
                        );
                      })}
                    </tbody>
                  </table>
                </div>
              </div>
            );
          })}

          {/* Pagination Controls */}
          <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-6 border-2 ${THEME.colors.borders.light}`}>
            <div className="flex items-center justify-between">
              {/* Previous Button */}
              <button
                onClick={() => setCurrentPage(Math.max(0, currentPage - 1))}
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
                      onClick={() => setCurrentPage(i)}
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
                onClick={() => setCurrentPage(Math.min(totalPages - 1, currentPage + 1))}
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
              Showing {history.length} of {totalElements} courses
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
