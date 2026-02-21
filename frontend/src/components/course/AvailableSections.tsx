import { THEME } from '../../constants/theme';
import type { CourseSection, CourseSectionMeeting } from '../../types/CourseSection';
import type { CurrentEnrollment } from '../../types/Enrollment';
import { SectionCard } from './SectionCard';

interface AvailableSectionsProps {
  sections: CourseSection[];
  sectionMeetings: Record<number, CourseSectionMeeting[]>;
  currentEnrollments: CurrentEnrollment[] | null;
  expandedSections: Set<number>;
  enrollingSection: number | null;
  enrollmentSuccess: number | null;
  enrollmentError: string | null;
  onToggleExpand: (sectionId: number) => void;
  onEnroll: (sectionId: number) => void;
  onDismissError: () => void;
  getScheduleConflicts: (sectionId: number) => Array<{ course: string; teacher: string; day: string; time: string }>;
  getDayName: (day: string) => string;
}

export function AvailableSections({
  sections,
  sectionMeetings,
  currentEnrollments,
  expandedSections,
  enrollingSection,
  enrollmentSuccess,
  enrollmentError,
  onToggleExpand,
  onEnroll,
  onDismissError,
  getScheduleConflicts,
  getDayName,
}: AvailableSectionsProps) {
  const isAlreadyEnrolled = (sectionId: number): boolean => {
    return currentEnrollments?.some((enrollment) => enrollment.section.id === sectionId) ?? false;
  };

  if (sections.length === 0) {
    return null;
  }

  return (
    <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-6 mb-8 border-2 ${THEME.colors.borders.light}`}>
      <h2 className={`text-lg font-bold ${THEME.colors.text.primary} mb-4`}>Classes</h2>

      {enrollmentError && (
        <div className="mb-4 p-4 bg-red-50 border-l-4 border-red-500 rounded flex items-start justify-between">
          <div className="flex-1">
            <p className="text-red-700 font-semibold text-sm">Enrollment Failed</p>
            <p className="text-red-600 text-sm mt-1">{enrollmentError}</p>
          </div>
          <button
            onClick={onDismissError}
            className="ml-4 text-red-500 hover:text-red-700 font-bold text-lg"
          >
            ✕
          </button>
        </div>
      )}

      <div className="space-y-3">
        {sections.map((section) => {
          const meetings = sectionMeetings[section.id] || [];
          const isExpanded = expandedSections.has(section.id);
          const isEnrolled = isAlreadyEnrolled(section.id);
          const conflicts = !isEnrolled ? getScheduleConflicts(section.id) : [];
          const isEnrolling = enrollingSection === section.id;
          const isSuccess = enrollmentSuccess === section.id;

          return (
            <SectionCard
              key={section.id}
              section={section}
              meetings={meetings}
              isExpanded={isExpanded}
              isEnrolled={isEnrolled}
              isEnrolling={isEnrolling}
              enrollmentSuccess={isSuccess}
              conflicts={conflicts}
              onToggleExpand={onToggleExpand}
              onEnroll={onEnroll}
              getDayName={getDayName}
            />
          );
        })}
      </div>
    </div>
  );
}
