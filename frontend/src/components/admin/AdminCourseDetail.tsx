import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { THEME } from '../../constants/theme';
import { CreateSection } from './CreateSection';
import client from '../../api/client';
import { getErrorMessage } from '../../types/BackendError';
import type { Course } from '../../types/Course';
import type { CourseSection } from '../../types/CourseSection';
import type { PaginatedResponse } from '../../types/common';

export function AdminCourseDetail() {
  const { courseId } = useParams<{ courseId: string }>();
  const navigate = useNavigate();

  const [course, setCourse] = useState<Course | null>(null);
  const [sections, setSections] = useState<CourseSection[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [refreshTrigger, setRefreshTrigger] = useState(0);

  // Load course details
  useEffect(() => {
    const loadCourse = async () => {
      try {
        setLoading(true);
        const response = await client.get<Course>(`/courses/${courseId}`);
        setCourse(response.data);
        setError(null);
      } catch (err) {
        setError(getErrorMessage(err, 'Failed to load course'));
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    loadCourse();
  }, [courseId]);

  // Load sections for this course
  useEffect(() => {
    if (!courseId) return;

    const loadSections = async () => {
      try {
        const courseIdNum = parseInt(courseId, 10);
        const response = await client.get<CourseSection[] | PaginatedResponse<CourseSection>>(
          `/course-sections/search/course/${courseIdNum}`
        );
        
        // Handle both array and paginated response formats
        if (Array.isArray(response.data)) {
          setSections(response.data);
          setTotalPages(1); // Single page when getting array
        } else {
          setSections(response.data.content || []);
          setTotalPages(response.data.page?.totalPages || 1);
        }
      } catch (err) {
        console.error('Failed to load sections:', err);
        setSections([]);
        setTotalPages(1);
      }
    };

    loadSections();
  }, [courseId, refreshTrigger]);

  const handleSectionClick = (sectionId: number) => {
    navigate(`/admin/courses/${courseId}/sections/${sectionId}`);
  };

  const handleSectionCreated = () => {
    setRefreshTrigger((prev) => prev + 1);
    setCurrentPage(0);
  };

  const handleDeleteSection = async (sectionId: number) => {
    if (confirm('Are you sure you want to delete this section?')) {
      try {
        await client.delete(`/course-sections/${sectionId}`);
        setRefreshTrigger((prev) => prev + 1);
      } catch (err) {
        alert(getErrorMessage(err, 'Failed to delete section'));
      }
    }
  };

  const handleNextPage = () => {
    if (currentPage < totalPages - 1) {
      setCurrentPage(currentPage + 1);
    }
  };

  const handlePrevPage = () => {
    if (currentPage > 0) {
      setCurrentPage(currentPage - 1);
    }
  };

  const handleGoToPage = (page: number) => {
    if (page >= 0 && page < totalPages) {
      setCurrentPage(page);
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center py-20">
        <div className="animate-spin">
          <svg className="w-10 h-10 text-cyan-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.5a8.5 8.5 0 1 1-2.5 5" />
          </svg>
        </div>
        <span className={`ml-3 ${THEME.colors.text.secondary}`}>Loading course...</span>
      </div>
    );
  }

  if (error || !course) {
    return (
      <div>
        <button
          onClick={() => navigate('/admin/courses')}
          className={`mb-4 px-4 py-2 rounded-lg font-semibold bg-gradient-to-r ${THEME.colors.gradients.button} text-white hover:shadow-lg`}
        >
          ← Back to Courses
        </button>
        <div className="bg-red-50 border-l-4 border-red-500 rounded p-4">
          <p className="text-red-700 font-semibold">{error || 'Course not found'}</p>
        </div>
      </div>
    );
  }

  return (
    <div>
      {/* Back Button */}
      <button
        onClick={() => navigate('/admin/courses')}
        className={`mb-6 px-4 py-2 rounded-lg font-semibold bg-gradient-to-r ${THEME.colors.gradients.button} text-white hover:shadow-lg transition-all`}
      >
        ← Back to Courses
      </button>

      {/* Course Header */}
      <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-8 mb-8 border-2 ${THEME.colors.borders.light}`}>
        <h1 className={`text-4xl font-bold ${THEME.colors.text.primary} mb-4`}>
          {course.code} - {course.name}
        </h1>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
          <div>
            <p className={`text-sm ${THEME.colors.text.secondary} mb-1`}>Specialization</p>
            <p className={`font-semibold ${THEME.colors.text.primary}`}>{course.specialization.name}</p>
          </div>
          <div>
            <p className={`text-sm ${THEME.colors.text.secondary} mb-1`}>Credits</p>
            <p className={`font-semibold ${THEME.colors.text.primary}`}>{course.credits} hrs</p>
          </div>
          <div>
            <p className={`text-sm ${THEME.colors.text.secondary} mb-1`}>Grade Level</p>
            <p className={`font-semibold ${THEME.colors.text.primary}`}>
              {course.gradeLevelMin}-{course.gradeLevelMax}
            </p>
          </div>
          <div>
            <p className={`text-sm ${THEME.colors.text.secondary} mb-1`}>Hours/Week</p>
            <p className={`font-semibold ${THEME.colors.text.primary}`}>{course.hoursPerWeek} hrs</p>
          </div>
        </div>
        {course.description && (
          <div className="mt-6 pt-6 border-t border-gray-200">
            <p className={`text-sm ${THEME.colors.text.secondary} mb-2`}>Description</p>
            <p className={THEME.colors.text.primary}>{course.description}</p>
          </div>
        )}
      </div>

      {/* Two Column Layout */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Create Section Form - Left Column */}
        <div className="lg:col-span-1">
          <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-6 border-2 ${THEME.colors.borders.light}`}>
            <h2 className={`text-xl font-bold ${THEME.colors.text.primary} mb-4`}>
              ➕ Create Section
            </h2>
            <CreateSection courseId={parseInt(courseId!)} onSectionCreated={handleSectionCreated} />
          </div>
        </div>

        {/* Sections List - Right Column */}
        <div className="lg:col-span-2">
          <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-6 border-2 ${THEME.colors.borders.light}`}>
            <h2 className={`text-xl font-bold ${THEME.colors.text.primary} mb-4`}>
              📋 Sections ({sections.length})
            </h2>

            {sections.length === 0 ? (
              <p className={THEME.colors.text.secondary}>No sections created for this course yet</p>
            ) : (
              <div className="space-y-3 mb-6">
                {sections.map((section) => (
                  <button
                    key={section.id}
                    onClick={() => handleSectionClick(section.id)}
                    className={`w-full p-4 text-left rounded-lg border-2 ${THEME.colors.backgrounds.card} ${THEME.colors.borders.light} hover:border-cyan-400 transition-all hover:shadow-md`}
                  >
                    <div className="flex justify-between items-start">
                      <div className="flex-1">
                        <h3 className={`font-semibold ${THEME.colors.text.primary} mb-1`}>
                          Section {section.id}
                        </h3>
                        <p className={`text-sm ${THEME.colors.text.secondary}`}>
                          👨‍🏫 {section.teacher.firstName} {section.teacher.lastName}
                        </p>
                        <p className={`text-sm ${THEME.colors.text.secondary}`}>
                          🏢 {section.classroom.name} • Capacity: {section.capacity}
                        </p>
                        <p className={`text-sm ${THEME.colors.text.secondary}`}>
                          👥 Enrolled: {section.enrollmentCount}/{section.capacity}
                        </p>
                      </div>
                      <button
                        onClick={(e) => {
                          e.stopPropagation();
                          handleDeleteSection(section.id);
                        }}
                        className="ml-4 px-3 py-1 text-sm rounded-lg bg-red-50 text-red-600 hover:bg-red-100 transition-all font-semibold"
                      >
                        Delete
                      </button>
                    </div>
                  </button>
                ))}
              </div>
            )}

            {/* Pagination */}
            {totalPages > 1 && (
              <div className={`pt-6 border-t ${THEME.colors.borders.light}`}>
                <div className="flex items-center justify-between">
                  <button
                    onClick={handlePrevPage}
                    disabled={currentPage === 0}
                    className={`px-4 py-2 rounded-lg font-semibold transition-all text-sm ${
                      currentPage === 0
                        ? 'bg-slate-100 text-slate-400 cursor-not-allowed'
                        : `bg-gradient-to-r ${THEME.colors.gradients.button} text-white hover:shadow-md`
                    }`}
                  >
                    ← Prev
                  </button>

                  <div className="flex items-center gap-2">
                    <span className={`${THEME.colors.text.secondary} font-semibold text-sm`}>
                      Page {currentPage + 1} of {totalPages}
                    </span>
                    <div className="flex gap-1">
                      {Array.from({ length: totalPages }).map((_, i) => (
                        <button
                          key={i}
                          onClick={() => handleGoToPage(i)}
                          className={`w-7 h-7 text-xs rounded font-semibold transition-all ${
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

                  <button
                    onClick={handleNextPage}
                    disabled={currentPage === totalPages - 1}
                    className={`px-4 py-2 rounded-lg font-semibold transition-all text-sm ${
                      currentPage === totalPages - 1
                        ? 'bg-slate-100 text-slate-400 cursor-not-allowed'
                        : `bg-gradient-to-r ${THEME.colors.gradients.button} text-white hover:shadow-md`
                    }`}
                  >
                    Next →
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
