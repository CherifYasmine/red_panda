import { THEME } from '../../constants/theme';
import type { Course } from '../../types/Course';

interface PrerequisiteChainProps {
  course: Course;
  prerequisiteChain: Course[];
}

export function PrerequisiteChain({ course, prerequisiteChain }: PrerequisiteChainProps) {
  // Show the component if there are prerequisites or the course has a prerequisite
  if (course.prerequisite && prerequisiteChain.length === 0) {
    return null;
  }

  if (!course.prerequisite && prerequisiteChain.length === 0) {
    return (
      <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-6 mb-8 border-2 ${THEME.colors.borders.light}`}>
        <p className={`${THEME.colors.text.secondary} text-center`}>
          This course has no prerequisites. You can enroll directly!
        </p>
      </div>
    );
  }

  return (
    <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-6 mb-8 border-2 ${THEME.colors.borders.light}`}>
      <h2 className={`text-lg font-bold ${THEME.colors.text.primary} mb-4`}>Prerequisite Chain</h2>
      <p className={`${THEME.colors.text.secondary} text-sm mb-6`}>Complete course requirements in this order:</p>
      <div className="space-y-3">
        {prerequisiteChain.map((prereq, index) => (
          <div key={prereq.id} className="flex items-start gap-4">
            <div
              className={`flex-shrink-0 w-8 h-8 rounded-full bg-cyan-100 text-cyan-700 font-bold flex items-center justify-center`}
            >
              {index + 1}
            </div>
            <div className="flex-1 p-4 bg-slate-50 rounded-lg border border-slate-200">
              <p className={`font-semibold ${THEME.colors.text.primary}`}>{prereq.name}</p>
              <p className={`text-sm ${THEME.colors.text.muted} mt-1`}>{prereq.code}</p>
              <p className={`text-sm ${THEME.colors.text.secondary} mt-2`}>
                {prereq.credits} credits • {prereq.hoursPerWeek}h/week
              </p>
            </div>
          </div>
        ))}
        <div className="flex items-start gap-4 mt-6 pt-6 border-t-2 border-slate-200">
          <div
            className={`flex-shrink-0 w-8 h-8 rounded-full bg-green-100 text-green-700 font-bold flex items-center justify-center`}
          >
            ✓
          </div>
          <div className="flex-1 p-4 bg-slate-50 rounded-lg border border-slate-200">
            <p className={`font-semibold ${THEME.colors.text.primary}`}>{course.name}</p>
            <p className={`text-sm ${THEME.colors.text.muted} mt-1`}>{course.code}</p>
            <p className={`text-sm ${THEME.colors.text.secondary} mt-2`}>
              {course.credits} credits • {course.hoursPerWeek}h/week
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
