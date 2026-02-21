import { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { THEME } from '../constants/theme';
import client from '../api/client';
import { useAuthStore } from '../stores/authStore';
import { getErrorMessage } from '../types/BackendError';
import type { CurrentEnrollment } from '../types/Enrollment';
import type { CourseSectionMeeting } from '../types/CourseSection';
import type { Course } from '../types/Course';
import type { CourseSection } from '../types/CourseSection';
import { ScheduleGrid } from '../components/ScheduleGrid';
import { EnrolledCoursesLegend } from '../components/EnrolledCoursesLegend';
import { AvailableCoursesPanel } from '../components/AvailableCoursesPanel';

export interface ScheduleSlot {
  enrollmentId: number;
  courseCode: string;
  courseName: string;
  teacherName: string;
  classroom: string;
  startTime: string;
  endTime: string;
  dayOfWeek: number;
  isOverlay?: boolean;
  hasConflict?: boolean;
}

const COLORS = [
  'bg-blue-100 border-blue-400 text-blue-900',
  'bg-purple-100 border-purple-400 text-purple-900',
  'bg-pink-100 border-pink-400 text-pink-900',
  'bg-green-100 border-green-400 text-green-900',
  'bg-amber-100 border-amber-400 text-amber-900',
  'bg-indigo-100 border-indigo-400 text-indigo-900',
  'bg-teal-100 border-teal-400 text-teal-900',
  'bg-cyan-100 border-cyan-400 text-cyan-900',
];

const DAY_MAP: Record<string, number> = {
  'MONDAY': 1,
  'TUESDAY': 2,
  'WEDNESDAY': 3,
  'THURSDAY': 4,
  'FRIDAY': 5,
};

const parseTime = (timeString: string): string => {
  if (timeString.includes('T')) {
    return timeString.split('T')[1].substring(0, 5);
  }
  return timeString.substring(0, 5);
};

export function Schedule() {
  const navigate = useNavigate();
  const { studentId } = useAuthStore();
  const [enrollments, setEnrollments] = useState<CurrentEnrollment[]>([]);
  const [scheduleSlots, setScheduleSlots] = useState<ScheduleSlot[]>([]);
  const [overlaySlots, setOverlaySlots] = useState<ScheduleSlot[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [dropError, setDropError] = useState<string | null>(null);

  // Fetch enrolled courses
  const fetchSchedule = useCallback(async () => {
    try {
      setIsLoading(true);
      setError(null);

      if (!studentId) {
        setError('Student not authenticated');
        return;
      }

      const enrollRes = await client.get<CurrentEnrollment[]>(`/enrollments/student/${studentId}`);
      setEnrollments(enrollRes.data);

      console.log('=== ENROLLED COURSES ===');
      const slots: ScheduleSlot[] = [];
      for (const enrollment of enrollRes.data) {
        const section = enrollment.section;
        if (!section?.course || !section?.teacher || !section?.classroom) continue;

        try {
          const meetingsRes = await client.get<CourseSectionMeeting[]>(
            `/course-section-meetings/search/section/${section.id}`
          );

          console.log(`Course: ${section.course!.name}, Raw data:`, meetingsRes.data);
          meetingsRes.data.forEach((meeting) => {
            const startParsed = parseTime(meeting.startTime);
            const endParsed = parseTime(meeting.endTime);
            console.log(`  Day=${meeting.dayOfWeek}, Raw: ${meeting.startTime} - ${meeting.endTime}, Parsed: ${startParsed} - ${endParsed}`);
            slots.push({
              enrollmentId: enrollment.id,
              courseCode: section.course!.code,
              courseName: section.course!.name,
              teacherName: `${section.teacher!.firstName} ${section.teacher!.lastName}`,
              classroom: section.classroom!.name,
              startTime: startParsed,
              endTime: endParsed,
              dayOfWeek: DAY_MAP[meeting.dayOfWeek as string] || 1,
            });
          });
        } catch (err) {
          console.error(`Failed to fetch meetings for section ${section.id}:`, err);
        }
      }

      setScheduleSlots(slots);
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setIsLoading(false);
    }
  }, [studentId]);

  useEffect(() => {
    fetchSchedule();
  }, [fetchSchedule]);

  const handleSelectSection = async (course: Course, section: CourseSection) => {
    // Don't show overlay if already enrolled in this section
    const isAlreadyEnrolled = enrollments.some((e) => e.section.id === section.id);
    if (isAlreadyEnrolled) return;

    // Load meetings for this section and show as overlay
    try {
      const meetingsRes = await client.get<CourseSectionMeeting[]>(
        `/course-section-meetings/search/section/${section.id}`
      );

      // Helper function to check if times overlap
      const hasTimeConflict = (slot1: ScheduleSlot, slot2: ScheduleSlot): boolean => {
        if (slot1.dayOfWeek !== slot2.dayOfWeek) return false;
        
        const timeToMinutes = (time: string): number => {
          const [hours, minutes] = time.split(':').map(Number);
          return hours * 60 + minutes;
        };
        
        const start1 = timeToMinutes(slot1.startTime);
        const end1 = timeToMinutes(slot1.endTime);
        const start2 = timeToMinutes(slot2.startTime);
        const end2 = timeToMinutes(slot2.endTime);
        
        return start1 < end2 && start2 < end1;
      };

      // Create overlay slots and check for conflicts
      const overlaySlotsList = meetingsRes.data.map((meeting) => {
        const parsedStart = parseTime(meeting.startTime);
        const parsedEnd = parseTime(meeting.endTime);
        const overlaySlot: ScheduleSlot = {
          enrollmentId: -1,
          courseCode: course.code,
          courseName: course.name,
          teacherName: `${section.teacher!.firstName} ${section.teacher!.lastName}`,
          classroom: section.classroom!.name,
          startTime: parsedStart,
          endTime: parsedEnd,
          dayOfWeek: DAY_MAP[meeting.dayOfWeek as string] || 1,
          isOverlay: true,
        };

        // Check if this overlay slot conflicts with any enrolled slot
        const hasConflict = scheduleSlots.some((enrolledSlot) =>
          hasTimeConflict(overlaySlot, enrolledSlot)
        );

        return { ...overlaySlot, hasConflict };
      });

      setOverlaySlots(overlaySlotsList);
    } catch (err) {
      console.error('Failed to load section meetings:', err);
    }
  };

  const handleClearOverlay = () => {
    setOverlaySlots([]);
  };

  if (isLoading) {
    return (
      <div className="flex justify-center items-center py-20">
        <div className="animate-spin">
          <svg className="w-10 h-10 text-cyan-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.5a8.5 8.5 0 1 1-2.5 5" />
          </svg>
        </div>
        <span className={`ml-3 ${THEME.colors.text.secondary}`}>Loading schedule...</span>
      </div>
    );
  }

  const allSlots = overlaySlots.length > 0 ? [...scheduleSlots, ...overlaySlots] : scheduleSlots;

  return (
    <div className="w-full">
      {/* Header */}
      <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-8 mb-8 border-2 ${THEME.colors.borders.light}`}>
        <h1 className={`text-3xl font-bold ${THEME.colors.text.primary} mb-2`}>My Schedule</h1>
        <p className={`${THEME.colors.text.secondary}`}>
          {enrollments.length} course{enrollments.length !== 1 ? 's' : ''} enrolled
        </p>
      </div>

      {/* Errors */}
      {error && (
        <div className="mb-6 p-4 bg-red-50 border-l-4 border-red-500 rounded">
          <p className="text-red-700 font-semibold">{error}</p>
        </div>
      )}

      {dropError && (
        <div className="mb-6 p-4 bg-red-50 border-l-4 border-red-500 rounded flex items-start justify-between">
          <p className="text-red-700 font-semibold flex-1">{dropError}</p>
          <button
            onClick={() => setDropError(null)}
            className="ml-4 text-red-500 hover:text-red-700 font-bold"
          >
            ✕
          </button>
        </div>
      )}

      {/* Empty State */}
      {enrollments.length === 0 ? (
        <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-12 border-2 ${THEME.colors.borders.light} text-center`}>
          <h2 className={`text-xl font-bold ${THEME.colors.text.primary} mb-2`}>No Courses Enrolled</h2>
          <p className={`${THEME.colors.text.secondary} mb-6`}>You haven't enrolled in any courses yet</p>
          <button
            onClick={() => navigate('/courses')}
            className={`px-6 py-2 bg-gradient-to-r ${THEME.colors.gradients.button} text-white font-semibold rounded-lg hover:shadow-md transition-all`}
          >
            Browse Courses
          </button>
        </div>
      ) : (
        <>
          {/* Main Layout: Sidebar + Schedule */}
          <div className="grid grid-cols-1 lg:grid-cols-4 gap-6 mb-8">
            {/* Sidebar: Available Courses */}
            <div className="lg:col-span-1">
              <AvailableCoursesPanel
                studentId={studentId ? Number(studentId) : null}
                isLoading={false}
                enrollments={enrollments}
                onSelectSection={handleSelectSection}
                onClearSection={handleClearOverlay}
              />
            </div>

            {/* Schedule Grid */}
            <div className="lg:col-span-3">
              <ScheduleGrid
                scheduleSlots={allSlots}
                colors={COLORS}
              />
            </div>
          </div>

          {/* Enrolled Courses Legend */}
          <EnrolledCoursesLegend
            enrollments={enrollments}
            colors={COLORS}
          />
        </>
      )}
    </div>
  );
}
