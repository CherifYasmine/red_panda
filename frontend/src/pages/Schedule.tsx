import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { THEME } from '../constants/theme';
import client from '../api/client';
import { useAuthStore } from '../stores/authStore';
import { getErrorMessage } from '../types/BackendError';
import type { CurrentEnrollment } from '../types/Enrollment';
import type { CourseSectionMeeting } from '../types/CourseSection';

interface ScheduleSlot {
  enrollmentId: number;
  courseCode: string;
  courseName: string;
  teacherName: string;
  classroom: string;
  startTime: string;
  endTime: string;
  dayOfWeek: number;
}

const TIME_SLOTS = [
  '08:00', '08:30', '09:00', '09:30', '10:00', '10:30',
  '11:00', '11:30', '12:00', '12:30', '13:00', '13:30',
  '14:00', '14:30', '15:00', '15:30', '16:00', '16:30', '17:00'
];

const DAYS = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday'];
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

// Map day names to day numbers (MONDAY=1, TUESDAY=2, etc.)
const DAY_MAP: Record<string, number> = {
  'MONDAY': 1,
  'TUESDAY': 2,
  'WEDNESDAY': 3,
  'THURSDAY': 4,
  'FRIDAY': 5,
};

export function Schedule() {
  const navigate = useNavigate();
  const { studentId } = useAuthStore();
  const [enrollments, setEnrollments] = useState<CurrentEnrollment[]>([]);
  const [scheduleSlots, setScheduleSlots] = useState<ScheduleSlot[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [dropError, setDropError] = useState<string | null>(null);

  useEffect(() => {
    const fetchSchedule = async () => {
      try {
        setIsLoading(true);
        setError(null);

        if (!studentId) {
          setError('Student not authenticated');
          return;
        }

        // Fetch student enrollments
        const enrollRes = await client.get<CurrentEnrollment[]>(`/enrollments/student/${studentId}`);
        setEnrollments(enrollRes.data);

        // Build schedule from enrollments
        const slots: ScheduleSlot[] = [];
        for (const enrollment of enrollRes.data) {
          const section = enrollment.section;
          
          // Skip if section data is missing
          if (!section || !section.course || !section.teacher || !section.classroom) {
            console.warn('Incomplete section data for enrollment:', enrollment.id);
            continue;
          }
          
          // Fetch meetings for this section
          try {
            const meetingsRes = await client.get<CourseSectionMeeting[]>(
              `/course-section-meetings/search/section/${section.id}`
            );

            // Add each meeting as a schedule slot
            meetingsRes.data.forEach((meeting) => {
              // Convert day name (MONDAY) to day number (1)
              const dayNum = DAY_MAP[meeting.dayOfWeek as string] || 1;
              // Convert '09:00:00' to '09:00'
              const startTimeFormatted = meeting.startTime.substring(0, 5);
              const endTimeFormatted = meeting.endTime.substring(0, 5);

              slots.push({
                enrollmentId: enrollment.id,
                courseCode: section.course.code,
                courseName: section.course.name,
                teacherName: `${section.teacher.firstName} ${section.teacher.lastName}`,
                classroom: section.classroom.name,
                startTime: startTimeFormatted,
                endTime: endTimeFormatted,
                dayOfWeek: dayNum,
              });
            });
          } catch (err) {
            console.error(`Failed to fetch meetings for section ${section.id}:`, err);
          }
        }

        setScheduleSlots(slots);
      } catch (err) {
        setError(getErrorMessage(err, 'Failed to load schedule'));
      } finally {
        setIsLoading(false);
      }
    };

    fetchSchedule();
  }, [studentId]);

  const getSlotColor = (index: number): string => {
    return COLORS[index % COLORS.length];
  };

  const calculateRowSpan = (startTime: string, endTime: string): number => {
    const [startH, startM] = startTime.split(':').map(Number);
    const [endH, endM] = endTime.split(':').map(Number);
    const startMins = startH * 60 + startM;
    const endMins = endH * 60 + endM;
    const diffMins = endMins - startMins;
    return Math.ceil(diffMins / 30) + 1;
  };

  const isTimeInRange = (time: string, startTime: string, endTime: string): boolean => {
    return time >= startTime && time <= endTime;
  };

  const getSlotAtTime = (dayOfWeek: number, time: string): ScheduleSlot | undefined => {
    return scheduleSlots.find(
      (slot) =>
        slot.dayOfWeek === dayOfWeek &&
        isTimeInRange(time, slot.startTime, slot.endTime)
    );
  };

  if (isLoading) {
    return (
      <div className="flex justify-center items-center py-20">
        <div className="animate-spin">
          <svg className="w-10 h-10 text-cyan-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.5a8.5 8.5 0 1 1-2.5 5" />
          </svg>
        </div>
        <span className={`ml-3 ${THEME.colors.text.secondary}`}>Loading your schedule...</span>
      </div>
    );
  }

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
            className="ml-4 text-red-500 hover:text-red-700 font-bold text-lg"
          >
            ✕
          </button>
        </div>
      )}

      {/* Empty State */}
      {enrollments.length === 0 ? (
        <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-12 border-2 ${THEME.colors.borders.light} text-center`}>
          <div className="mb-4">
            <svg className="w-16 h-16 text-slate-300 mx-auto" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M8 7V3m8 4V3m-9 8h18M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
          </div>
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
          {/* Schedule Grid */}
          <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-6 mb-8 border-2 ${THEME.colors.borders.light} overflow-x-auto`}>
            <table className="w-full border-collapse">
              {/* Header */}
              <thead>
                <tr>
                  <th className={`p-3 text-sm font-semibold ${THEME.colors.text.primary} border border-slate-200 bg-slate-50`}>
                    Time
                  </th>
                  {DAYS.map((day) => (
                    <th
                      key={day}
                      className={`p-3 text-sm font-semibold ${THEME.colors.text.primary} border border-slate-200 bg-slate-50 text-center`}
                    >
                      {day}
                    </th>
                  ))}
                </tr>
              </thead>

              {/* Time Slots */}
              <tbody>
                {TIME_SLOTS.map((time) => (
                  <tr key={time}>
                    <td className={`p-3 text-xs font-semibold ${THEME.colors.text.muted} border border-slate-200 bg-slate-50 whitespace-nowrap`}>
                      {time}
                    </td>
                    {DAYS.map((_, dayIdx) => {
                      const dayOfWeek = dayIdx + 1; // 1-5 for Monday-Friday
                      const slot = getSlotAtTime(dayOfWeek, time);
                      const slotKey = `${dayOfWeek}-${time}`;

                      // Only render slot once (at its start time)
                      const shouldRender = slot && slot.startTime === time;

                      return (
                        <td
                          key={slotKey}
                          className={`p-2 border border-slate-200 ${!slot ? 'bg-white' : ''} h-12 align-top relative`}
                        >
                          {shouldRender && (
                            <div
                              className={`${getSlotColor(
                                scheduleSlots.indexOf(slot)
                              )} p-2 rounded border-2 text-xs cursor-pointer group hover:shadow-md transition-all absolute top-0 left-0 right-0`}
                              style={{
                                height: `${calculateRowSpan(slot.startTime, slot.endTime) * 48}px`,
                                zIndex: 10,
                              }}
                              title={`${slot.courseName} - ${slot.teacherName}`}
                            >
                              <p className="font-bold line-clamp-1">{slot.courseCode}</p>
                              <p className="line-clamp-1">{slot.classroom}</p>
                              <p className="text-xs line-clamp-1 opacity-75">
                                {slot.startTime}-{slot.endTime}
                              </p>
                            </div>
                          )}
                        </td>
                      );
                    })}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {/* Course Legend */}
          <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-6 border-2 ${THEME.colors.borders.light}`}>
            <h3 className={`text-lg font-bold ${THEME.colors.text.primary} mb-4`}>Enrolled Courses</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {enrollments
                .filter((enrollment) => enrollment.section && enrollment.section.course && enrollment.section.teacher && enrollment.section.classroom)
                .map((enrollment, idx) => {
                  const section = enrollment.section!;
                  return (
                    <div key={enrollment.id} className={`${getSlotColor(idx)} p-4 rounded-lg border-2`}>
                      <p className="font-bold">{section.course.code}</p>
                      <p className="text-sm">{section.course.name}</p>
                      <p className="text-xs mt-2">
                        <span className="font-semibold">Teacher:</span> {section.teacher.firstName} {section.teacher.lastName}
                      </p>
                      <p className="text-xs">
                        <span className="font-semibold">Room:</span> {section.classroom.name}
                      </p>
                    </div>
                  );
                })}
            </div>
          </div>
        </>
      )}
    </div>
  );
}
