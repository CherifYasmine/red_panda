import { THEME } from '../../constants/theme';
import type { CourseSection, CourseSectionMeeting } from '../../types/CourseSection';
import { ConflictDetails } from './ConflictDetails';

interface SectionCardProps {
  section: CourseSection;
  meetings: CourseSectionMeeting[];
  isExpanded: boolean;
  isEnrolled: boolean;
  isEnrolling: boolean;
  enrollmentSuccess: boolean;
  conflicts: Array<{ course: string; teacher: string; day: string; time: string }>;
  onToggleExpand: (sectionId: number) => void;
  onEnroll: (sectionId: number) => void;
  getDayName: (day: string) => string;
}

export function SectionCard({
  section,
  meetings,
  isExpanded,
  isEnrolled,
  isEnrolling,
  enrollmentSuccess,
  conflicts,
  onToggleExpand,
  onEnroll,
  getDayName,
}: SectionCardProps) {
  const meetingStr =
    meetings
      .map((m) => `${getDayName(m.dayOfWeek)} ${m.startTime.substring(0, 5)}-${m.endTime.substring(0, 5)}`)
      .join(', ') || 'No scheduled times';

  return (
    <div
      className={`border-2 rounded-lg overflow-hidden transition-all ${
        isExpanded && !isEnrolled ? 'border-cyan-400' : THEME.colors.borders.light
      }`}
    >
      {/* Section Header */}
      <div className={`p-4 ${isExpanded && !isEnrolled ? 'bg-slate-50' : THEME.colors.backgrounds.card}`}>
        <div className="flex items-center justify-between gap-4">
          {/* Expand Arrow and Section Info */}
          <div className="flex items-start gap-3 flex-1 min-w-0">
            {/* Expand Arrow (only shown when not enrolled) */}
            {!isEnrolled && (
              <button
                onClick={() => onToggleExpand(section.id)}
                className="flex-shrink-0 mt-1 p-1 hover:bg-slate-100 rounded transition-all"
                aria-label={isExpanded ? 'Collapse' : 'Expand'}
              >
                <svg
                  className={`w-5 h-5 text-slate-600 transition-transform duration-200 ${isExpanded ? 'rotate-180' : ''}`}
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M19 14l-7 7m0 0l-7-7m7 7V3"
                  />
                </svg>
              </button>
            )}

            {/* Section Info */}
            <div className="flex-1 min-w-0">
              <div className="flex items-start gap-4 justify-between">
                <div className="flex-1 min-w-0">
                  <p className={`font-semibold ${THEME.colors.text.primary}`}>
                    {section.teacher.firstName} {section.teacher.lastName}
                  </p>
                  <p className={`text-sm ${THEME.colors.text.secondary} mt-1`}>{section.classroom.name}</p>
                  <p className={`text-xs ${THEME.colors.text.muted} mt-2`}>{meetingStr}</p>
                </div>
                <div className="text-right whitespace-nowrap flex-shrink-0">
                  <p className={`text-sm font-semibold ${THEME.colors.text.accent}`}>{section.capacity} slots</p>
                </div>
              </div>
            </div>
          </div>

          {/* Enroll Button */}
          <button
            onClick={() => onEnroll(section.id)}
            disabled={isEnrolling || isEnrolled}
            className={`flex-shrink-0 px-4 py-2 rounded-lg font-semibold transition-all whitespace-nowrap ${
              isEnrolled
                ? 'bg-slate-300 text-slate-600 cursor-not-allowed'
                : enrollmentSuccess
                  ? 'bg-green-500 text-white'
                  : `bg-gradient-to-r ${THEME.colors.gradients.button} text-white hover:shadow-md ${
                      isEnrolling ? 'opacity-50 cursor-not-allowed' : ''
                    }`
            }`}
          >
            {isEnrolled ? 'Enrolled' : isEnrolling ? 'Enrolling...' : 'Enroll'}
          </button>
        </div>
      </div>

      {/* Expandable Conflicts Section */}
      {isExpanded && !isEnrolled && (
        <div className="border-t border-slate-200 bg-white p-4">
          <ConflictDetails conflicts={conflicts} />
        </div>
      )}
    </div>
  );
}
