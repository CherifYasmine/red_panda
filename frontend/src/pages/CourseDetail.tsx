import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { THEME } from '../constants/theme';
import client from '../api/client';
import { useAuthStore } from '../stores/authStore';
import type { Course } from '../types/Course';
import type { CourseSection, CourseSectionMeeting } from '../types/CourseSection';
import { getErrorMessage } from '../types/BackendError';

export function CourseDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { studentId } = useAuthStore();
  const [course, setCourse] = useState<Course | null>(null);
  const [prerequisiteChain, setPrerequisiteChain] = useState<Course[]>([]);
  const [sections, setSections] = useState<CourseSection[]>([]);
  const [sectionMeetings, setSectionMeetings] = useState<Record<number, CourseSectionMeeting[]>>({});
  const [enrollingSection, setEnrollingSection] = useState<number | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [enrollmentError, setEnrollmentError] = useState<string | null>(null);
  const [enrollmentSuccess, setEnrollmentSuccess] = useState<number | null>(null);

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
          setSectionMeetings(meetingsMap);
        }
      } catch (err) {
        setError('Failed to load course details');
        console.error(err);
      } finally {
        setIsLoading(false);
      }
    };

    if (id) {
      fetchCourseDetail();
    }
  }, [id]);

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

  const gradeLevel = course.gradeLevelMin === course.gradeLevelMax 
    ? `Grade ${course.gradeLevelMin}` 
    : `Grades ${course.gradeLevelMin}-${course.gradeLevelMax}`;

  const semester = course.semesterOrder === 1 ? 'Fall' : 'Spring';
  const courseTypeColor = course.courseType === 'CORE' 
    ? 'bg-blue-100 text-blue-700' 
    : 'bg-purple-100 text-purple-700';

  const getDayName = (dayOfWeek: string): string => {
    if (!dayOfWeek) return '';
    // Convert 'MONDAY' to 'Monday', 'TUESDAY' to 'Tuesday', etc.
    return dayOfWeek.charAt(0).toUpperCase() + dayOfWeek.slice(1).toLowerCase();
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

      // POST to enroll: /enrollments with payload { studentId, sectionId }
      await client.post('/enrollments', {
        studentId,
        sectionId: sectionId,
      });

      setEnrollmentSuccess(sectionId);
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
      <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-8 mb-8 border-2 ${THEME.colors.borders.light}`}>
        <div className="flex items-start justify-between mb-4">
          <div className="flex-1">
            <p className={`text-sm font-semibold ${THEME.colors.text.muted} mb-2`}>{course.code}</p>
            <h1 className={`text-4xl font-bold ${THEME.colors.text.primary} mb-2`}>{course.name}</h1>
          </div>
          <span className={`inline-flex items-center px-4 py-2 rounded-lg text-sm font-semibold whitespace-nowrap ml-4 ${courseTypeColor}`}>
            {course.courseType}
          </span>
        </div>

        {/* Description */}
        {course.description && (
          <p className={`${THEME.colors.text.secondary} text-lg mb-6`}>
            {course.description}
          </p>
        )}

        {/* Key Info Grid */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
          <div>
            <p className={`${THEME.colors.text.muted} text-sm mb-2`}>Credits</p>
            <p className={`font-bold text-cyan-600 text-xl`}>{course.credits}</p>
          </div>
          <div>
            <p className={`${THEME.colors.text.muted} text-sm mb-2`}>Hours/Week</p>
            <p className={`font-bold ${THEME.colors.text.primary} text-xl`}>{course.hoursPerWeek}h</p>
          </div>
          <div>
            <p className={`${THEME.colors.text.muted} text-sm mb-2`}>Grade Level</p>
            <p className={`font-bold ${THEME.colors.text.primary} text-xl`}>{gradeLevel}</p>
          </div>
          <div>
            <p className={`${THEME.colors.text.muted} text-sm mb-2`}>Semester</p>
            <p className={`font-bold ${THEME.colors.text.primary} text-xl`}>{semester}</p>
          </div>
        </div>
      </div>

      {/* Specialization */}
      <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-6 mb-8 border-2 ${THEME.colors.borders.light}`}>
        <h2 className={`text-lg font-bold ${THEME.colors.text.primary} mb-4`}>Specialization</h2>
        <div className="p-4 bg-gradient-to-r from-slate-50 to-slate-100 rounded-lg">
          <p className={`text-lg font-semibold ${THEME.colors.text.primary}`}>{course.specialization.name}</p>
          {course.specialization.description && (
            <p className={`${THEME.colors.text.secondary} text-sm mt-2`}>{course.specialization.description}</p>
          )}
        </div>
      </div>

      {/* Available Sections */}
      {sections.length > 0 && (
        <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-6 mb-8 border-2 ${THEME.colors.borders.light}`}>
          <h2 className={`text-lg font-bold ${THEME.colors.text.primary} mb-4`}>Course Sections</h2>

          {enrollmentError && (
            <div className="mb-4 p-4 bg-red-50 border-l-4 border-red-500 rounded flex items-start justify-between">
              <div className="flex-1">
                <p className="text-red-700 font-semibold text-sm">Enrollment Failed</p>
                <p className="text-red-600 text-sm mt-1">{enrollmentError}</p>
              </div>
              <button
                onClick={() => setEnrollmentError(null)}
                className="ml-4 text-red-500 hover:text-red-700 font-bold text-lg"
              >
                ✕
              </button>
            </div>
          )}

          <div className="space-y-3">
            {sections.map((section) => {
              const meetings = sectionMeetings[section.id] || [];
              const meetingStr = meetings
                .map((m) => `${getDayName(m.dayOfWeek)} ${m.startTime.substring(0, 5)}-${m.endTime.substring(0, 5)}`)
                .join(', ') || 'No scheduled times';

              return (
                <div
                  key={section.id}
                  className={`flex items-center justify-between p-4 rounded-lg border-2 transition-all ${
                    enrollmentSuccess === section.id
                      ? 'border-green-400 bg-green-50'
                      : `${THEME.colors.borders.light} hover:bg-slate-50`
                  }`}
                >
                  {/* Section Info */}
                  <div className="flex-1">
                    <div className="flex items-start gap-4">
                      <div className="flex-1">
                        <p className={`font-semibold ${THEME.colors.text.primary}`}>
                          {section.teacher.firstName} {section.teacher.lastName}
                        </p>
                        <p className={`text-sm ${THEME.colors.text.secondary} mt-1`}>
                          {section.classroom.name}
                        </p>
                        <p className={`text-xs ${THEME.colors.text.muted} mt-2`}>
                          {meetingStr}
                        </p>
                      </div>
                      <div className="text-right whitespace-nowrap">
                        <p className={`text-sm font-semibold ${THEME.colors.text.accent}`}>
                          {section.capacity} slots
                        </p>
                      </div>
                    </div>
                  </div>

                  {/* Enroll Button */}
                  <button
                    onClick={() => handleEnroll(section.id)}
                    disabled={enrollingSection === section.id}
                    className={`ml-4 px-4 py-2 rounded-lg font-semibold transition-all whitespace-nowrap ${
                      enrollmentSuccess === section.id
                        ? 'bg-green-500 text-white'
                        : `bg-gradient-to-r ${THEME.colors.gradients.button} text-white hover:shadow-md ${
                            enrollingSection === section.id ? 'opacity-50 cursor-not-allowed' : ''
                          }`
                    }`}
                  >
                    {enrollingSection === section.id ? 'Enrolling...' : 'Enroll'}
                  </button>
                </div>
              );
            })}
          </div>
        </div>
      )}

      {/* Prerequisite Chain */}
      {prerequisiteChain.length > 0 && (
        <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-6 mb-8 border-2 ${THEME.colors.borders.light}`}>
          <h2 className={`text-lg font-bold ${THEME.colors.text.primary} mb-4`}>Prerequisite Chain</h2>
          <p className={`${THEME.colors.text.secondary} text-sm mb-6`}>
            Complete course requirements in this order:
          </p>
          <div className="space-y-3">
            {prerequisiteChain.map((prereq, index) => (
              <div key={prereq.id} className="flex items-start gap-4">
                <div className={`flex-shrink-0 w-8 h-8 rounded-full bg-cyan-100 text-cyan-700 font-bold flex items-center justify-center`}>
                  {index + 1}
                </div>
                <div className="flex-1 p-4 bg-slate-50 rounded-lg border border-slate-200">
                  <p className={`font-semibold ${THEME.colors.text.primary}`}>{prereq.name}</p>
                  <p className={`text-sm ${THEME.colors.text.muted} mt-1`}>{prereq.code}</p>
                  <p className={`text-sm ${THEME.colors.text.secondary} mt-2`}>{prereq.credits} credits • {prereq.hoursPerWeek}h/week</p>
                </div>
              </div>
            ))}
            <div className="flex items-start gap-4 mt-6 pt-6 border-t-2 border-slate-200">
              <div className={`flex-shrink-0 w-8 h-8 rounded-full bg-green-100 text-green-700 font-bold flex items-center justify-center`}>
                ✓
              </div>
              <div className="flex-1 p-4 bg-slate-50 rounded-lg border border-slate-200">
                <p className={`font-semibold ${THEME.colors.text.primary}`}>{course.name}</p>
                <p className={`text-sm ${THEME.colors.text.muted} mt-1`}>{course.code}</p>
                <p className={`text-sm ${THEME.colors.text.secondary} mt-2`}>{course.credits} credits • {course.hoursPerWeek}h/week</p>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* No Prerequisites Message */}
      {!course.prerequisite && prerequisiteChain.length === 0 && (
        <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-6 mb-8 border-2 ${THEME.colors.borders.light}`}>
          <p className={`${THEME.colors.text.secondary} text-center`}>
            This course has no prerequisites. You can enroll directly!
          </p>
        </div>
      )}
    </div>
  );
}
