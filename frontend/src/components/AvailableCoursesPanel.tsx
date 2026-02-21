import { useState, useEffect } from 'react';
import { THEME } from '../constants/theme';
import { useEnrollmentStore } from '../stores/enrollmentStore';
import client from '../api/client';
import type { Course } from '../types/Course';
import type { CourseSection } from '../types/CourseSection';
import type { CurrentEnrollment } from '../types/Enrollment';

interface AvailableCoursesPanelProps {
  studentId: number | null;
  isLoading: boolean;
  enrollments: CurrentEnrollment[];
  onSelectSection?: (course: Course, section: CourseSection) => void;
  onClearSection?: () => void;
}

export function AvailableCoursesPanel({ studentId, isLoading, enrollments, onSelectSection, onClearSection }: AvailableCoursesPanelProps) {
  const { enroll, enrollmentError, clearError } = useEnrollmentStore();
  const [courses, setCourses] = useState<Course[]>([]);
  const [expandedCourse, setExpandedCourse] = useState<number | null>(null);
  const [sections, setSections] = useState<Record<number, CourseSection[]>>({});
  const [error, setError] = useState<string | null>(null);
  const [loadingSections, setLoadingSections] = useState<Record<number, boolean>>({});
  const [selectedSectionId, setSelectedSectionId] = useState<number | null>(null);
  const [enrollingId, setEnrollingId] = useState<number | null>(null);

  useEffect(() => {
    if (!studentId) return;

    const fetchAvailableCourses = async () => {
      try {
        setError(null);
        const res = await client.get<Course[]>(`/courses/available-for-student/${studentId}`);
        setCourses(res.data);
      } catch (err) {
        const message = err instanceof Error ? err.message : 'Failed to load available courses';
        setError(message);
      }
    };

    fetchAvailableCourses();
  }, [studentId]);

  const handleExpandCourse = async (courseId: number) => {
    if (expandedCourse === courseId) {
      setExpandedCourse(null);
      return;
    }

    // Load sections if not already loaded
    if (!sections[courseId]) {
      try {
        setLoadingSections((prev) => ({ ...prev, [courseId]: true }));
        const res = await client.get<CourseSection[]>(`/course-sections/search/course/${courseId}`);
        setSections((prev) => ({ ...prev, [courseId]: res.data }));
      } catch (err) {
        console.error('Failed to load sections:', err);
      } finally {
        setLoadingSections((prev) => ({ ...prev, [courseId]: false }));
      }
    }

    setExpandedCourse(courseId);
  };

  const handleEnroll = async (sectionId: number, course: Course, section: CourseSection) => {
    if (!studentId) return;

    try {
      setEnrollingId(sectionId);
      clearError();
      setSelectedSectionId(sectionId);
      onSelectSection?.(course, section);
      await enroll(studentId, sectionId);
      setEnrollingId(null);
    } catch {
      setEnrollingId(null);
    }
  };

  return (
    <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-4 border-2 ${THEME.colors.borders.light} max-h-140 overflow-y-auto`}>
      <h3 className={`text-lg font-bold ${THEME.colors.text.primary} mb-4`}>Available Courses</h3>

      {/* Error Alerts */}
      {error && (
        <div className="mb-4 p-3 bg-red-50 border-l-4 border-red-500 rounded">
          <p className="text-red-700 text-sm font-semibold">Error Loading Courses</p>
          <p className="text-red-600 text-xs mt-1">{error}</p>
        </div>
      )}

      {/* Enrollment Error Alert */}
      {enrollmentError && (
        <div className="mb-4 p-3 bg-red-50 border-l-4 border-red-500 rounded">
          <p className="text-red-700 text-sm font-semibold">Enrollment Error</p>
          <p className="text-red-600 text-xs mt-1">{enrollmentError}</p>
        </div>
      )}

      {isLoading ? (
        <div className="flex justify-center items-center h-20">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
        </div>
      ) : courses.length === 0 ? (
        <p className={`${THEME.colors.text.secondary} text-sm`}>No courses available for your grade level</p>
      ) : (
        <div className="space-y-2">
          {courses.map((course) => (
            <div key={course.id}>
              <button
                onClick={() => handleExpandCourse(course.id)}
                className="w-full text-left p-3 rounded-lg border border-slate-200 hover:bg-slate-50 transition-all"
              >
                <div className="flex items-center justify-between">
                  <div className="flex-1">
                    <p className="font-semibold text-sm">{course.code}</p>
                    <p className="text-xs text-slate-600">{course.name}</p>
                  </div>
                  <span className="text-lg text-slate-700">{expandedCourse === course.id ? '▼' : '▶'}</span>
                </div>
              </button>

              {/* Sections List */}
              {expandedCourse === course.id && (
                <div className="mt-2 ml-4 space-y-2 border-l-2 border-blue-300 pl-3">
                  {loadingSections[course.id] ? (
                    <div className="flex justify-center py-2">
                      <div className="animate-spin rounded-full h-4 w-4 border-b border-blue-500"></div>
                    </div>
                  ) : sections[course.id] ? (
                    sections[course.id].map((section) => {
                      const isEnrolled = enrollments.some((e) => e.section.id === section.id);
                      return (
                        <div
                          key={section.id}
                          onClick={() => {
                            if (isEnrolled) return; // Don't allow toggling enrolled sections
                            if (selectedSectionId === section.id) {
                              // Click again to clear overlay
                              setSelectedSectionId(null);
                              onClearSection?.();
                            } else {
                              // Click to show overlay
                              setSelectedSectionId(section.id);
                              onSelectSection?.(course, section);
                            }
                          }}
                          className={`p-2 rounded cursor-pointer hover:shadow transition-all text-xs border ${
                            isEnrolled
                              ? 'bg-green-50 border-green-300 opacity-60 cursor-not-allowed'
                              : selectedSectionId === section.id
                              ? 'bg-blue-100 border-blue-400 font-semibold'
                              : 'bg-blue-50 border-blue-200 hover:bg-blue-100'
                          }`}
                        >
                          <div className="flex items-center justify-between">
                            <div className="flex-1">
                              <p className="font-semibold">{section.teacher?.firstName} {section.teacher?.lastName}</p>
                              <p>{section.classroom?.name}</p>
                            </div>
                            {isEnrolled ? (
                              <span className="ml-2 px-2 py-0.5 bg-green-600 text-white text-xs rounded font-semibold whitespace-nowrap">
                                ✓ Enrolled
                              </span>
                            ) : (
                              <button
                                onClick={(e) => {
                                  e.stopPropagation();
                                  handleEnroll(section.id, course, section);
                                }}
                                disabled={enrollingId === section.id}
                                className="ml-2 px-3 py-1 bg-blue-600 text-white text-xs rounded font-semibold whitespace-nowrap hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors"
                              >
                                {enrollingId === section.id ? 'Enrolling...' : 'Enroll'}
                              </button>
                            )}
                          </div>
                        </div>
                      );
                    })
                  ) : (
                    <p className="text-xs text-slate-500">No sections available</p>
                  )}
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
