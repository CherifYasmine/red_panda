import { useEffect, useState, useCallback } from 'react';
import client from '../../api/client';
import { THEME } from '../../constants/theme';
import type { Student } from '../../types/Student';
import type { PaginatedResponse } from '../../types/common';

const PAGE_SIZE = 10;

export function AdminStudents() {
  const [students, setStudents] = useState<Student[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  const fetchStudents = useCallback(async (page: number) => {
    setLoading(true);
    setError(null);

    try {
      const response = await client.get<PaginatedResponse<Student>>('/students', {
        params: {
          page,
          size: PAGE_SIZE,
          sort: 'id,desc',
        },
      });

      setStudents(response.data.content);
      setTotalPages(response.data.page.totalPages);
      setTotalElements(response.data.page.totalElements);
      setCurrentPage(page);
    } catch (err) {
      setError('Failed to load students');
      console.error('Error fetching students:', err);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchStudents(0);
  }, [fetchStudents]);

  const handleNextPage = () => {
    if (currentPage < totalPages - 1) {
      fetchStudents(currentPage + 1);
    }
  };

  const handlePrevPage = () => {
    if (currentPage > 0) {
      fetchStudents(currentPage - 1);
    }
  };

  const handleGoToPage = (page: number) => {
    if (page >= 0 && page < totalPages) {
      fetchStudents(page);
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'ACTIVE':
        return 'bg-green-100 text-green-800';
      case 'INACTIVE':
        return 'bg-gray-100 text-gray-800';
      case 'GRADUATED':
        return 'bg-blue-100 text-blue-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const getGradeDisplay = (grade: number) => {
    const grades: { [key: number]: string } = {
      9: '9th Grade',
      10: '10th Grade',
      11: '11th Grade',
      12: '12th Grade',
    };
    return grades[grade] || `Grade ${grade}`;
  };

  if (error) {
    return (
      <div className={`p-8 ${THEME.colors.backgrounds.main} rounded-xl border-2 border-red-300 bg-red-50`}>
        <p className="text-red-700 font-semibold">{error}</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className={`text-3xl font-bold ${THEME.colors.text.primary}`}>Students</h1>
          <p className={`${THEME.colors.text.muted} mt-1`}>
            Total: {totalElements} students across {totalPages} pages
          </p>
        </div>
      </div>

      {/* Students Grid */}
      {loading ? (
        <div className="flex items-center justify-center py-16">
          <div className="animate-spin">
            <svg className="w-8 h-8 text-blue-500" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
              <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
          </div>
        </div>
      ) : students.length === 0 ? (
        <div className={`text-center py-16 ${THEME.colors.backgrounds.card} rounded-xl border-2 ${THEME.colors.borders.light}`}>
          <p className={`${THEME.colors.text.muted} text-lg`}>No students found</p>
        </div>
      ) : (
        <>
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {students.map((student) => (
              <div
                key={student.id}
                className={`${THEME.colors.backgrounds.card} rounded-xl p-6 border-2 ${THEME.colors.borders.light} ${THEME.shadows.card} hover:shadow-lg transition-shadow`}
              >
                {/* Header with ID */}
                <div className="flex items-start justify-between mb-4">
                  <div>
                    <h3 className={`text-xl font-bold ${THEME.colors.text.primary}`}>
                      {student.firstName} {student.lastName}
                    </h3>
                    <p className={`text-sm ${THEME.colors.text.muted} mt-1`}>ID: {student.id}</p>
                  </div>
                  <span className={`px-3 py-1 rounded-full text-xs font-semibold ${getStatusColor(student.status)}`}>
                    {student.status}
                  </span>
                </div>

                {/* Email and Grade */}
                <div className="space-y-2 mb-4 pb-4 border-b-2 border-gray-100">
                  <div>
                    <p className={`text-xs ${THEME.colors.text.muted} font-semibold uppercase`}>Email</p>
                    <p className={`text-sm ${THEME.colors.text.secondary} break-all`}>{student.email}</p>
                  </div>
                  <div>
                    <p className={`text-xs ${THEME.colors.text.muted} font-semibold uppercase`}>Grade Level</p>
                    <p className={`text-sm ${THEME.colors.text.secondary} font-semibold`}>{getGradeDisplay(student.gradeLevel)}</p>
                  </div>
                </div>

                {/* Academic Metrics */}
                {student.academicMetrics && (
                  <div className="space-y-3">
                    <div className="flex items-center justify-between">
                      <span className={`text-sm ${THEME.colors.text.muted}`}>GPA</span>
                      <span className="text-lg font-bold text-blue-600">
                        {student.academicMetrics.gpa.toFixed(2)}
                      </span>
                    </div>
                    <div className="flex items-center justify-between">
                      <span className={`text-sm ${THEME.colors.text.muted}`}>Credits Earned</span>
                      <span className="font-semibold text-gray-700">
                        {student.academicMetrics.creditsEarned}
                      </span>
                    </div>
                    <div className="flex items-center justify-between">
                      <span className={`text-sm ${THEME.colors.text.muted}`}>Remaining Credits</span>
                      <span className="font-semibold text-gray-700">
                        {student.academicMetrics.remainingCreditsToGraduate}
                      </span>
                    </div>
                    {student.academicMetrics.isGraduated && (
                      <div className="mt-3 pt-3 border-t-2 border-gray-100">
                        <p className="text-center text-sm font-semibold text-green-600">
                          ✓ Graduated
                        </p>
                      </div>
                    )}
                  </div>
                )}
              </div>
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
          </div>
        </>
      )}
    </div>
  );
}
