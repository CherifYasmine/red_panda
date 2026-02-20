import { useState, useEffect } from 'react';
import { THEME } from '../../constants/theme';
import client from '../../api/client';
import { getErrorMessage } from '../../types/BackendError';
import type { CourseSection, CourseSectionMeeting } from '../../types/CourseSection';

const DAYS = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY'];

interface ScheduleMeetingsProps {
  refreshTrigger?: number;
}

interface MeetingForm {
  sectionId: number | null;
  dayOfWeek: string;
  startTime: string;
  endTime: string;
}

export function ScheduleMeetings({ refreshTrigger }: ScheduleMeetingsProps) {
  const [sections, setSections] = useState<CourseSection[]>([]);
  const [meetings, setMeetings] = useState<CourseSectionMeeting[]>([]);

  const [form, setForm] = useState<MeetingForm>({
    sectionId: null,
    dayOfWeek: 'MONDAY',
    startTime: '09:00',
    endTime: '10:00',
  });

  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [deletingId, setDeletingId] = useState<number | null>(null);

  useEffect(() => {
    loadSections();
  }, [refreshTrigger]);

  const loadSections = async () => {
    try {
      setIsLoading(true);
      setError(null);

      const res = await client.get<{
        content: CourseSection[];
      }>('/course-sections?page=0&size=100');

      setSections(res.data.content || []);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to load course sections'));
    } finally {
      setIsLoading(false);
    }
  };

  const handleSectionChange = async (sectionId: string) => {
    const selectedSectionId = parseInt(sectionId);
    setForm((prev) => ({ ...prev, sectionId: selectedSectionId }));

    if (!selectedSectionId) {
      setMeetings([]);
      return;
    }

    try {
      const res = await client.get<CourseSectionMeeting[]>(
        `/course-section-meetings/search/section/${selectedSectionId}`
      );
      setMeetings(res.data || []);
    } catch (err) {
      console.error('Failed to load meetings:', err);
      setMeetings([]);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccessMessage(null);

    if (!form.sectionId) {
      setError('Please select a course section');
      return;
    }

    if (form.startTime >= form.endTime) {
      setError('Start time must be before end time');
      return;
    }

    try {
      setIsSubmitting(true);

      const payload = {
        sectionId: form.sectionId,
        dayOfWeek: form.dayOfWeek,
        startTime: `${form.startTime}:00`,
        endTime: `${form.endTime}:00`,
      };

      const res = await client.post<CourseSectionMeeting>(
        '/course-section-meetings',
        payload
      );

      setSuccessMessage(`Meeting added for ${form.dayOfWeek} ${form.startTime}-${form.endTime}!`);
      setMeetings([...meetings, res.data]);

      setForm({
        sectionId: form.sectionId,
        dayOfWeek: 'MONDAY',
        startTime: '09:00',
        endTime: '10:00',
      });

      setTimeout(() => setSuccessMessage(null), 3000);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to add meeting'));
      console.error('Error adding meeting:', err);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDeleteMeeting = async (meetingId: number) => {
    if (!window.confirm('Are you sure you want to delete this meeting?')) {
      return;
    }

    try {
      setDeletingId(meetingId);
      setError(null);

      await client.delete(`/course-section-meetings/${meetingId}`);
      setSuccessMessage('Meeting deleted successfully!');

      setMeetings(meetings.filter((m) => m.id !== meetingId));
      setTimeout(() => setSuccessMessage(null), 3000);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to delete meeting'));
    } finally {
      setDeletingId(null);
    }
  };

  const selectedSection = form.sectionId
    ? sections.find((s) => s.id === form.sectionId)
    : null;

  if (isLoading) {
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

      <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-6 border-2 ${THEME.colors.borders.light}`}>
        <h3 className={`text-xl font-bold ${THEME.colors.text.primary} mb-6`}>
          Add Meeting to Course Section
        </h3>

        <form onSubmit={handleSubmit} className="space-y-6">
          <div>
            <label className={`block text-sm font-semibold ${THEME.colors.text.primary} mb-2`}>
              Course Section *
            </label>
            <select
              value={form.sectionId || ''}
              onChange={(e) => handleSectionChange(e.target.value)}
              className={`w-full px-4 py-2 border-2 ${THEME.colors.borders.light} rounded-lg focus:outline-none focus:ring-2 focus:ring-cyan-500`}
              required
            >
              <option value="">-- Select a section --</option>
              {sections.map((section) => (
                <option key={section.id} value={section.id}>
                  {section.course.code} - {section.course.name} (Teacher: {section.teacher.firstName} {section.teacher.lastName})
                </option>
              ))}
            </select>
          </div>

          {selectedSection && (
            <div className={`p-4 rounded-lg border-2 ${THEME.colors.borders.light} bg-blue-50`}>
              <p className={`text-sm ${THEME.colors.text.primary} mb-2`}>
                <span className="font-semibold">Classroom:</span> {selectedSection.classroom.name}
              </p>
              <p className={`text-sm ${THEME.colors.text.primary}`}>
                <span className="font-semibold">Capacity:</span> {selectedSection.capacity}
              </p>
              <p className={`text-sm ${THEME.colors.text.primary}`}>
                <span className="font-semibold">Hours:</span> {selectedSection.course.hoursPerWeek} hrs/week
              </p>
            </div>
          )}

          <div className="grid grid-cols-3 gap-4">
            <div>
              <label className={`block text-sm font-semibold ${THEME.colors.text.primary} mb-2`}>
                Day of Week *
              </label>
              <select
                value={form.dayOfWeek}
                onChange={(e) => setForm((prev) => ({ ...prev, dayOfWeek: e.target.value }))}
                className={`w-full px-4 py-2 border-2 ${THEME.colors.borders.light} rounded-lg focus:outline-none focus:ring-2 focus:ring-cyan-500`}
                required
              >
                {DAYS.map((day) => (
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
              <input
                type="time"
                value={form.startTime}
                onChange={(e) => setForm((prev) => ({ ...prev, startTime: e.target.value }))}
                className={`w-full px-4 py-2 border-2 ${THEME.colors.borders.light} rounded-lg focus:outline-none focus:ring-2 focus:ring-cyan-500`}
                required
              />
            </div>

            <div>
              <label className={`block text-sm font-semibold ${THEME.colors.text.primary} mb-2`}>
                End Time *
              </label>
              <input
                type="time"
                value={form.endTime}
                onChange={(e) => setForm((prev) => ({ ...prev, endTime: e.target.value }))}
                className={`w-full px-4 py-2 border-2 ${THEME.colors.borders.light} rounded-lg focus:outline-none focus:ring-2 focus:ring-cyan-500`}
                required
              />
            </div>
          </div>

          <button
            type="submit"
            disabled={isSubmitting || !form.sectionId}
            className={`w-full px-6 py-3 font-semibold rounded-lg transition-all ${
              isSubmitting || !form.sectionId
                ? 'bg-slate-200 text-slate-400 cursor-not-allowed'
                : `bg-gradient-to-r ${THEME.colors.gradients.button} text-white hover:shadow-md`
            }`}
          >
            {isSubmitting ? 'Adding...' : 'Add Meeting'}
          </button>
        </form>
      </div>

      {/* Meetings List */}
      {selectedSection && (
        <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-6 border-2 ${THEME.colors.borders.light}`}>
          <h3 className={`text-xl font-bold ${THEME.colors.text.primary} mb-6`}>
            Meetings for {selectedSection.course.code}
          </h3>

          {meetings.length === 0 ? (
            <p className={`${THEME.colors.text.muted} text-center py-8`}>
              No meetings scheduled yet. Add one above!
            </p>
          ) : (
            <div className="space-y-3">
              {meetings.map((meeting) => (
                <div
                  key={meeting.id}
                  className={`flex items-center justify-between p-4 rounded-lg border-2 ${THEME.colors.borders.light} ${THEME.colors.backgrounds.main}`}
                >
                  <div>
                    <p className={`text-sm font-semibold ${THEME.colors.text.primary}`}>
                      {meeting.dayOfWeek.charAt(0) + meeting.dayOfWeek.slice(1).toLowerCase()}
                    </p>
                    <p className={`text-sm ${THEME.colors.text.secondary}`}>
                      {meeting.startTime.substring(0, 5)} - {meeting.endTime.substring(0, 5)}
                    </p>
                  </div>
                  <button
                    onClick={() => handleDeleteMeeting(meeting.id)}
                    disabled={deletingId === meeting.id}
                    className={`px-3 py-1 rounded text-sm font-semibold transition-all ${
                      deletingId === meeting.id
                        ? 'bg-red-200 text-red-600 cursor-not-allowed'
                        : 'bg-red-100 text-red-600 hover:bg-red-200'
                    }`}
                  >
                    {deletingId === meeting.id ? 'Deleting...' : 'Delete'}
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
}
