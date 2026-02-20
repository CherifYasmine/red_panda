import { useState, useEffect } from 'react';
import { THEME } from '../../constants/theme';
import client from '../../api/client';
import { getErrorMessage } from '../../types/BackendError';
import type { CourseSection } from '../../types/CourseSection';

interface ManageSectionsProps {
  refreshTrigger?: number;
}

export function ManageSections({ refreshTrigger }: ManageSectionsProps) {
  const [sections, setSections] = useState<CourseSection[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [deletingId, setDeletingId] = useState<number | null>(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const PAGE_SIZE = 10;

  useEffect(() => {
    const loadSections = async () => {
      try {
        setIsLoading(true);
        setError(null);

        const res = await client.get<{
          content: CourseSection[];
          page: {
            number: number;
            size: number;
            totalPages: number;
          };
        }>(`/course-sections?page=${currentPage}&size=${PAGE_SIZE}`);

        setSections(res.data.content || []);
        setTotalPages(res.data.page?.totalPages || 0);
      } catch (err) {
        setError(getErrorMessage(err, 'Failed to load course sections'));
        console.error('Error loading sections:', err);
      } finally {
        setIsLoading(false);
      }
    };

    loadSections();
  }, [refreshTrigger, currentPage]);

  const handleDelete = async (sectionId: number) => {
    if (!window.confirm('Are you sure you want to delete this course section? This cannot be undone.')) {
      return;
    }

    try {
      setDeletingId(sectionId);
      setError(null);

      await client.delete(`/course-sections/${sectionId}`);
      setSuccessMessage('Course section deleted successfully!');

      setSections(sections.filter((s) => s.id !== sectionId));
      setTimeout(() => setSuccessMessage(null), 3000);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to delete course section'));
    } finally {
      setDeletingId(null);
    }
  };

  if (isLoading && sections.length === 0) {
    return (
      <div className="flex justify-center items-center py-12">
        <div className="animate-spin">
          <svg className="w-8 h-8 text-cyan-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.5a8.5 8.5 0 1 1-2.5 5" />
          </svg>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {error && (
        <div className="p-4 bg-red-50 border-l-4 border-red-500 rounded flex items-start justify-between">
          <p className="text-red-700 font-semibold flex-1">{error}</p>
          <button onClick={() => setError(null)} className="ml-4 text-red-500 hover:text-red-700 font-bold">
            ✕
          </button>
        </div>
      )}

      {successMessage && (
        <div className="p-4 bg-green-50 border-l-4 border-green-500 rounded flex items-start justify-between">
          <p className="text-green-700 font-semibold flex-1">{successMessage}</p>
          <button onClick={() => setSuccessMessage(null)} className="ml-4 text-green-500 hover:text-green-700 font-bold">
            ✕
          </button>
        </div>
      )}

      {sections.length === 0 ? (
        <div className={`text-center py-12 ${THEME.colors.backgrounds.card} rounded-lg border-2 ${THEME.colors.borders.light}`}>
          <p className={`${THEME.colors.text.muted} text-lg`}>No course sections found. Create one to get started.</p>
        </div>
      ) : (
        <>
          <div className="overflow-x-auto rounded-lg border-2 border-gray-200">
            <table className="w-full">
              <thead className={`${THEME.colors.backgrounds.card} border-b-2 border-gray-200`}>
                <tr>
                  <th className={`px-4 py-3 text-left text-sm font-semibold ${THEME.colors.text.primary}`}>Course</th>
                  <th className={`px-4 py-3 text-left text-sm font-semibold ${THEME.colors.text.primary}`}>Teacher</th>
                  <th className={`px-4 py-3 text-left text-sm font-semibold ${THEME.colors.text.primary}`}>Classroom</th>
                  <th className={`px-4 py-3 text-left text-sm font-semibold ${THEME.colors.text.primary}`}>Capacity</th>
                  <th className={`px-4 py-3 text-left text-sm font-semibold ${THEME.colors.text.primary}`}>Enrolled</th>
                  <th className={`px-4 py-3 text-center text-sm font-semibold ${THEME.colors.text.primary}`}>Action</th>
                </tr>
              </thead>
              <tbody>
                {sections.map((section, idx) => (
                  <tr key={section.id} className={idx % 2 === 0 ? 'bg-white' : 'bg-gray-50'}>
                    <td className={`px-4 py-3 text-sm ${THEME.colors.text.primary} font-medium`}>
                      {section.course?.code} - {section.course?.name}
                    </td>
                    <td className={`px-4 py-3 text-sm ${THEME.colors.text.secondary}`}>
                      {section.teacher?.firstName} {section.teacher?.lastName}
                    </td>
                    <td className={`px-4 py-3 text-sm ${THEME.colors.text.secondary}`}>
                      {section.classroom?.name}
                    </td>
                    <td className={`px-4 py-3 text-sm ${THEME.colors.text.secondary}`}>
                      {section.capacity}
                    </td>
                    <td className={`px-4 py-3 text-sm ${THEME.colors.text.secondary}`}>
                      {section.enrollmentCount} / {section.capacity}
                    </td>
                    <td className="px-4 py-3 text-center">
                      <button
                        onClick={() => handleDelete(section.id)}
                        disabled={deletingId === section.id}
                        className={`px-3 py-1 rounded text-sm font-semibold transition-all ${
                          deletingId === section.id
                            ? 'bg-red-200 text-red-600 cursor-not-allowed'
                            : 'bg-red-100 text-red-600 hover:bg-red-200'
                        }`}
                      >
                        {deletingId === section.id ? 'Deleting...' : 'Delete'}
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {/* Pagination */}
          {totalPages > 1 && (
            <div className="flex items-center justify-between">
              <button
                onClick={() => setCurrentPage(Math.max(0, currentPage - 1))}
                disabled={currentPage === 0}
                className={`px-4 py-2 rounded-lg font-semibold transition-all ${
                  currentPage === 0
                    ? 'bg-gray-200 text-gray-400 cursor-not-allowed'
                    : 'bg-cyan-500 text-white hover:bg-cyan-600'
                }`}
              >
                ← Previous
              </button>

              <div className="flex gap-2">
                {Array.from({ length: totalPages }, (_, i) => (
                  <button
                    key={i}
                    onClick={() => setCurrentPage(i)}
                    className={`px-3 py-2 rounded-lg font-semibold transition-all ${
                      currentPage === i
                        ? `bg-gradient-to-r ${THEME.colors.gradients.button} text-white`
                        : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
                    }`}
                  >
                    {i + 1}
                  </button>
                ))}
              </div>

              <button
                onClick={() => setCurrentPage(Math.min(totalPages - 1, currentPage + 1))}
                disabled={currentPage === totalPages - 1}
                className={`px-4 py-2 rounded-lg font-semibold transition-all ${
                  currentPage === totalPages - 1
                    ? 'bg-gray-200 text-gray-400 cursor-not-allowed'
                    : 'bg-cyan-500 text-white hover:bg-cyan-600'
                }`}
              >
                Next →
              </button>
            </div>
          )}
        </>
      )}
    </div>
  );
}
