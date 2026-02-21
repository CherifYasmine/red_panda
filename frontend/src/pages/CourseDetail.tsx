import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { THEME } from '../constants/theme';
import client from '../api/client';
import { useAuthStore } from '../stores/authStore';
import { useEnrollmentStore } from '../stores/enrollmentStore';
import type { Course } from '../types/Course';
import type { CourseSection, CourseSectionMeeting } from '../types/CourseSection';
import { getErrorMessage } from '../types/BackendError';
import { CourseHeader } from '../components/course/CourseHeader';
import { CourseSpecialization } from '../components/course/CourseSpecialization';
import { AvailableSections } from '../components/course/AvailableSections';
import { PrerequisiteChain } from '../components/course/PrerequisiteChain';

export function CourseDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { studentId } = useAuthStore();
  const { currentEnrollments, fetchCurrentEnrollments } = useEnrollmentStore();
  const [course, setCourse] = useState<Course | null>(null);
  const [prerequisiteChain, setPrerequisiteChain] = useState<Course[]>([]);
  const [sections, setSections] = useState<CourseSection[]>([]);
  const [sectionMeetings, setSectionMeetings] = useState<Record<number, CourseSectionMeeting[]>>({});
  const [enrollingSection, setEnrollingSection] = useState<number | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [enrollmentError, setEnrollmentError] = useState<string | null>(null);
  const [enrollmentSuccess, setEnrollmentSuccess] = useState<number | null>(null);
  const [expandedSections, setExpandedSections] = useState<Set<number>>(new Set());

  useEffect(() => {
    const fetchCourseDetail = async () => {
      try {
        setIsLoading(true);
        setError(null);

        // Fetch course details
        const courseRes = await client.get<Course>(`/courses/${id}`);
        setCourse(courseRes.data);

        // Fetch prerequisite chain
        const chainRes = await client.get<Course[]>(`/courses/${id}/prerequisite-chain`);
        setPrerequisiteChain(chainRes.data);

        // Fetch available sections
        const sectionsRes = await client.get<CourseSection[]>(`/course-sections/search/course/${id}`);
        setSections(sectionsRes.data);

        // Fetch meetings for all sections
        if (sectionsRes.data.length > 0) {
          const meetingsMap: Record<number, CourseSectionMeeting[]> = {};
          for (const section of sectionsRes.data) {
            try {
              const meetingsRes = await client.get<CourseSectionMeeting[]>(
                `/course-section-meetings/search/section/${section.id}`
              );
              meetingsMap[section.id] = meetingsRes.data;
            } catch (err) {
              console.error(`Failed to fetch meetings for section ${section.id}:`, err);
            }
          }
          // Merge with existing enrolled section meetings instead of replacing
          setSectionMeetings((prev) => {
            const updated = { ...prev, ...meetingsMap };
            return updated;
          });
        }
      } catch (err) {
        setError('Failed to load course details');
        console.error(err);
      } finally {
        setIsLoading(false);
      }
    };

    if (id && studentId) {
      fetchCourseDetail();
      // Fetch current enrollments to check for existing enrollments
      fetchCurrentEnrollments(studentId);
    }
  }, [id, studentId, fetchCurrentEnrollments]);

  // Fetch meetings for enrolled sections to detect conflicts
  useEffect(() => {
    const fetchEnrolledMeetings = async () => {
      if (!currentEnrollments || currentEnrollments.length === 0) return;
      
      const newMeetings: Record<number, CourseSectionMeeting[]> = {};
      for (const enrollment of currentEnrollments) {
        try {
          const meetingsRes = await client.get<CourseSectionMeeting[]>(
            `/course-section-meetings/search/section/${enrollment.section.id}`
          );
          newMeetings[enrollment.section.id] = meetingsRes.data;
        } catch (err) {
          console.error(`Failed to fetch meetings for enrolled section ${enrollment.section.id}:`, err);
        }
      }
      if (Object.keys(newMeetings).length > 0) {
        setSectionMeetings((prev) => {
          const updated = { ...prev, ...newMeetings };
          return updated;
        });
      }
    };

    fetchEnrolledMeetings();
  }, [currentEnrollments]);

  if (isLoading) {
    return (
      <div className="flex justify-center items-center py-20">
        <div className="animate-spin">
          <svg className="w-10 h-10 text-cyan-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.5a8.5 8.5 0 1 1-2.5 5" />
          </svg>
        </div>
        <span className={`ml-3 ${THEME.colors.text.secondary}`}>Loading course details...</span>
      </div>
    );
  }

  if (error || !course) {
    return (
      <div className="max-w-4xl mx-auto">
        <button
          onClick={() => navigate('/courses')}
          className={`px-4 py-2 mb-6 border-2 ${THEME.colors.borders.light} ${THEME.colors.text.accent} font-semibold rounded-lg hover:bg-cyan-50 transition-all`}
        >
          ← Back to Courses
        </button>
        <div className="mb-6 p-4 bg-red-50 border-l-4 border-red-500 rounded">
          <p className="text-red-700 font-semibold">{error || 'Course not found'}</p>
        </div>
      </div>
    );
  }

  const getDayName = (dayOfWeek: string): string => {
    if (!dayOfWeek) return '';
    return dayOfWeek.charAt(0).toUpperCase() + dayOfWeek.slice(1).toLowerCase();
  };

  const toggleSectionExpand = (sectionId: number) => {
    const newExpanded = new Set(expandedSections);
    if (newExpanded.has(sectionId)) {
      newExpanded.delete(sectionId);
    } else {
      newExpanded.add(sectionId);
    }
    setExpandedSections(newExpanded);
  };

  const getScheduleConflicts = (sectionId: number) => {
    if (!currentEnrollments) return [];
    
    const newMeetings = sectionMeetings[sectionId] || [];
    const conflicts: Array<{course: string; teacher: string; day: string; time: string}> = [];

    for (const enrollment of currentEnrollments) {
      const existingMeetings = sectionMeetings[enrollment.section.id] || [];

      for (const newMeeting of newMeetings) {
        for (const existingMeeting of existingMeetings) {
          if (newMeeting.dayOfWeek === existingMeeting.dayOfWeek) {
            // Parse times as integers for proper comparison (HH:MM -> HHMM)
            const newStart = parseInt(newMeeting.startTime.replace(':', ''));
            const newEnd = parseInt(newMeeting.endTime.replace(':', ''));
            const existStart = parseInt(existingMeeting.startTime.replace(':', ''));
            const existEnd = parseInt(existingMeeting.endTime.replace(':', ''));


            if (newStart < existEnd && existStart < newEnd) {
              conflicts.push({
                course: enrollment.section.course.name,
                teacher: `${enrollment.section.teacher.firstName} ${enrollment.section.teacher.lastName}`,
                day: getDayName(newMeeting.dayOfWeek),
                time: `${newMeeting.startTime.substring(0, 5)}-${newMeeting.endTime.substring(0, 5)}`,
              });
            }
          }
        }
      }
    }

    return conflicts;
  };

  const handleEnroll = async (sectionId: number) => {
    if (!studentId) {
      setEnrollmentError('Student not authenticated');
      return;
    }

    try {
      setEnrollingSection(sectionId);
      setEnrollmentError(null);
      setEnrollmentSuccess(null);
      await client.post('/enrollments', {
        studentId,
        sectionId: sectionId,
      });

      setEnrollmentSuccess(sectionId);
      await fetchCurrentEnrollments(studentId);
      setTimeout(() => setEnrollmentSuccess(null), 3000);
    } catch (err: unknown) {
      const errorMsg = getErrorMessage(err, 'Failed to enroll in section');
      setEnrollmentError(errorMsg);
    } finally {
      setEnrollingSection(null);
    }
  };

  return (
    <div className="max-w-4xl mx-auto">
      {/* Back Button */}
      <button
        onClick={() => navigate('/courses')}
        className={`px-4 py-2 mb-6 border-2 ${THEME.colors.borders.light} ${THEME.colors.text.accent} font-semibold rounded-lg hover:bg-cyan-50 transition-all`}
      >
        ← Back to Courses
      </button>

      {/* Course Header */}
      <CourseHeader course={course} />

      {/* Specialization */}
      <CourseSpecialization course={course} />

      {/* Available Sections */}
      <AvailableSections
        sections={sections}
        sectionMeetings={sectionMeetings}
        currentEnrollments={currentEnrollments}
        expandedSections={expandedSections}
        enrollingSection={enrollingSection}
        enrollmentSuccess={enrollmentSuccess}
        enrollmentError={enrollmentError}
        onToggleExpand={toggleSectionExpand}
        onEnroll={handleEnroll}
        onDismissError={() => setEnrollmentError(null)}
        getScheduleConflicts={getScheduleConflicts}
        getDayName={getDayName}
      />

      {/* Prerequisite Chain */}
      <PrerequisiteChain course={course} prerequisiteChain={prerequisiteChain} />
    </div>
  );
}
