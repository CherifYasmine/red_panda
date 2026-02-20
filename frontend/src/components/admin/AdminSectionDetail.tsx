import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { THEME } from '../../constants/theme';
import client from '../../api/client';
import { getErrorMessage } from '../../types/BackendError';
import type { CourseSection, CourseSectionMeeting } from '../../types/CourseSection';

export function AdminSectionDetail() {
  const { courseId, sectionId } = useParams<{ courseId: string; sectionId: string }>();
  const navigate = useNavigate();

  const [section, setSection] = useState<CourseSection | null>(null);
  const [meetings, setMeetings] = useState<CourseSectionMeeting[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Form state
  const [dayOfWeek, setDayOfWeek] = useState<string>('');
  const [startTime, setStartTime] = useState<string>('');
  const [endTime, setEndTime] = useState<string>('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submitError, setSubmitError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  // Load section details
  useEffect(() => {
    const loadSection = async () => {
      try {
        setLoading(true);
        const response = await client.get<CourseSection>(`/course-sections/${sectionId}`);
        setSection(response.data);
        setError(null);
      } catch (err) {
        setError(getErrorMessage(err, 'Failed to load section'));
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    loadSection();
  }, [sectionId]);

  // Load meetings for this section
  useEffect(() => {
    const loadMeetings = async () => {
      try {
        const response = await client.get<CourseSectionMeeting[]>(
          `/course-section-meetings/search/section/${sectionId}`
        );
        setMeetings(Array.isArray(response.data) ? response.data : []);
      } catch (err) {
        console.error('Failed to load meetings:', err);
        setMeetings([]);
      }
    };

    loadMeetings();
  }, [sectionId]);

  const handleAddMeeting = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitError(null);
    setSuccessMessage(null);

    if (!dayOfWeek || !startTime || !endTime) {
      setSubmitError('Please fill in all fields');
      return;
    }

    try {
      setIsSubmitting(true);
      const payload = {
        sectionId: parseInt(sectionId!),
        dayOfWeek,
        startTime: `${startTime}:00`,
        endTime: `${endTime}:00`,
      };

      await client.post('/course-section-meetings', payload);
      setSuccessMessage('Meeting created successfully!');

      // Reset form
      setDayOfWeek('');
      setStartTime('');
      setEndTime('');

      // Reload meetings
      const response = await client.get<CourseSectionMeeting[]>(
        `/course-section-meetings/search/section/${sectionId}`
      );
      setMeetings(Array.isArray(response.data) ? response.data : []);

      setTimeout(() => setSuccessMessage(null), 3000);
    } catch (err) {
      const message = getErrorMessage(err, 'Failed to create meeting');
      setSubmitError(message);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDeleteMeeting = async (meetingId: number) => {
    if (confirm('Are you sure you want to delete this meeting?')) {
      try {
        await client.delete(`/course-section-meetings/${meetingId}`);
        // Reload meetings
        const response = await client.get<CourseSectionMeeting[]>(
          `/course-section-meetings/search/section/${sectionId}`
        );
        setMeetings(Array.isArray(response.data) ? response.data : []);
      } catch (err) {
        console.error('Failed to delete meeting:', err);
        alert(getErrorMessage(err, 'Failed to delete meeting'));
      }
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
        <span className={`ml-3 ${THEME.colors.text.secondary}`}>Loading section...</span>
      </div>
    );
  }

  if (error || !section) {
    return (
      <div>
        <button
          onClick={() => navigate(`/admin/courses/${courseId}`)}
          className={`mb-4 px-4 py-2 rounded-lg font-semibold bg-gradient-to-r ${THEME.colors.gradients.button} text-white hover:shadow-lg`}
        >
          ← Back to Course
        </button>
        <div className="bg-red-50 border-l-4 border-red-500 rounded p-4">
          <p className="text-red-700 font-semibold">{error || 'Section not found'}</p>
        </div>
      </div>
    );
  }

  const days = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY'];
  const timeSlots = Array.from({ length: 10 }, (_, i) => {
    const hour = 8 + i;
    return `${String(hour).padStart(2, '0')}:00`;
  });

  return (
    <div>
      {/* Back Button */}
      <button
        onClick={() => navigate(`/admin/courses/${courseId}`)}
        className={`mb-6 px-4 py-2 rounded-lg font-semibold bg-gradient-to-r ${THEME.colors.gradients.button} text-white hover:shadow-lg transition-all`}
      >
        ← Back to Course
      </button>

      {/* Section Header */}
      <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-8 mb-8 border-2 ${THEME.colors.borders.light}`}>
        <h1 className={`text-4xl font-bold ${THEME.colors.text.primary} mb-4`}>
          {section.course.code} - Section {section.id}
        </h1>
        <div className="grid grid-cols-2 md:grid-cols-5 gap-6">
          <div>
            <p className={`text-sm ${THEME.colors.text.secondary} mb-1`}>Course</p>
            <p className={`font-semibold ${THEME.colors.text.primary}`}>{section.course.name}</p>
          </div>
          <div>
            <p className={`text-sm ${THEME.colors.text.secondary} mb-1`}>Teacher</p>
            <p className={`font-semibold ${THEME.colors.text.primary}`}>
              {section.teacher.firstName} {section.teacher.lastName}
            </p>
          </div>
          <div>
            <p className={`text-sm ${THEME.colors.text.secondary} mb-1`}>Classroom</p>
            <p className={`font-semibold ${THEME.colors.text.primary}`}>{section.classroom.name}</p>
          </div>
          <div>
            <p className={`text-sm ${THEME.colors.text.secondary} mb-1`}>Capacity</p>
            <p className={`font-semibold ${THEME.colors.text.primary}`}>{section.capacity}</p>
          </div>
          <div>
            <p className={`text-sm ${THEME.colors.text.secondary} mb-1`}>Enrolled</p>
            <p className={`font-semibold ${THEME.colors.text.primary}`}>
              {section.enrollmentCount}/{section.capacity}
            </p>
          </div>
        </div>
      </div>

      {/* Two Column Layout */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Create Meeting Form - Left Column */}
        <div className="lg:col-span-1">
          <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-6 border-2 ${THEME.colors.borders.light}`}>
            <h2 className={`text-xl font-bold ${THEME.colors.text.primary} mb-4`}>
              ➕ Add Meeting
            </h2>

            {submitError && (
              <div className="bg-red-50 border-l-4 border-red-500 rounded p-3 mb-4">
                <p className="text-red-700 text-sm font-semibold">{submitError}</p>
              </div>
            )}

            {successMessage && (
              <div className="bg-green-50 border-l-4 border-green-500 rounded p-3 mb-4">
                <p className="text-green-700 text-sm font-semibold">{successMessage}</p>
              </div>
            )}

            <form onSubmit={handleAddMeeting} className="space-y-4">
              <div>
                <label className={`block text-sm font-semibold ${THEME.colors.text.primary} mb-2`}>
                  Day *
                </label>
                <select
                  value={dayOfWeek}
                  onChange={(e) => setDayOfWeek(e.target.value)}
                  className={`w-full px-3 py-2 border-2 ${THEME.colors.borders.light} rounded-lg focus:outline-none focus:ring-2 focus:ring-cyan-500`}
                  required
                >
                  <option value="">Select day</option>
                  {days.map((day) => (
                    <option key={day} value={day}>
                      {day.charAt(0) + day.slice(1).toLowerCase()}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className={`block text-sm font-semibold ${THEME.colors.text.primary} mb-2`}>
                  Start Time *
                </label>
                <select
                  value={startTime}
                  onChange={(e) => setStartTime(e.target.value)}
                  className={`w-full px-3 py-2 border-2 ${THEME.colors.borders.light} rounded-lg focus:outline-none focus:ring-2 focus:ring-cyan-500`}
                  required
                >
                  <option value="">Select start time</option>
                  {timeSlots.map((time) => (
                    <option key={time} value={time}>
                      {time}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className={`block text-sm font-semibold ${THEME.colors.text.primary} mb-2`}>
                  End Time *
                </label>
                <select
                  value={endTime}
                  onChange={(e) => setEndTime(e.target.value)}
                  className={`w-full px-3 py-2 border-2 ${THEME.colors.borders.light} rounded-lg focus:outline-none focus:ring-2 focus:ring-cyan-500`}
                  required
                >
                  <option value="">Select end time</option>
                  {timeSlots.map((time) => (
                    <option key={time} value={time}>
                      {time}
                    </option>
                  ))}
                </select>
              </div>

              <button
                type="submit"
                disabled={isSubmitting || !dayOfWeek || !startTime || !endTime}
                className={`w-full px-4 py-2 font-semibold rounded-lg transition-all text-sm ${
                  isSubmitting || !dayOfWeek || !startTime || !endTime
                    ? 'bg-slate-100 text-slate-400 cursor-not-allowed'
                    : `bg-gradient-to-r ${THEME.colors.gradients.button} text-white hover:shadow-md`
                }`}
              >
                {isSubmitting ? 'Creating...' : 'Add Meeting'}
              </button>
            </form>
          </div>
        </div>

        {/* Meetings List - Right Column */}
        <div className="lg:col-span-2">
          <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-6 border-2 ${THEME.colors.borders.light}`}>
            <h2 className={`text-xl font-bold ${THEME.colors.text.primary} mb-4`}>
              📅 Meetings ({meetings.length})
            </h2>

            {meetings.length === 0 ? (
              <p className={THEME.colors.text.secondary}>No meetings scheduled for this section</p>
            ) : (
              <div className="space-y-3">
                {meetings.map((meeting) => (
                  <div
                    key={meeting.id}
                    className={`p-4 rounded-lg border-2 ${THEME.colors.borders.light} hover:border-cyan-400 transition-all`}
                  >
                    <div className="flex justify-between items-start">
                      <div className="flex-1">
                        <h3 className={`font-semibold ${THEME.colors.text.primary} mb-1`}>
                          {meeting.dayOfWeek.charAt(0) + meeting.dayOfWeek.slice(1).toLowerCase()}
                        </h3>
                        <p className={`text-sm ${THEME.colors.text.secondary}`}>
                          🕐 {meeting.startTime.substring(0, 5)} - {meeting.endTime.substring(0, 5)}
                        </p>
                      </div>
                      <button
                        onClick={() => handleDeleteMeeting(meeting.id)}
                        className="ml-4 px-3 py-1 text-sm rounded-lg bg-red-50 text-red-600 hover:bg-red-100 transition-all font-semibold"
                      >
                        Delete
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
