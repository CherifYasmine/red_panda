import type { Course } from '../types/Course';
import { THEME } from '../constants/theme';

interface CourseCardProps {
  course: Course;
}

export function CourseCard({ course }: CourseCardProps) {
  const gradeLevel = course.gradeLevelMin === course.gradeLevelMax 
    ? `Grade ${course.gradeLevelMin}` 
    : `Grades ${course.gradeLevelMin}-${course.gradeLevelMax}`;

  const semester = course.semesterOrder === 1 ? 'Fall' : 'Spring';
  const courseTypeColor = course.courseType === 'CORE' 
    ? 'bg-blue-100 text-blue-700' 
    : 'bg-purple-100 text-purple-700';

  return (
    <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-6 border-2 ${THEME.colors.borders.light} hover:shadow-lg transition-all`}>
      {/* Header with badge */}
      <div className="flex items-start justify-between mb-4">
        <div className="flex-1">
          <p className={`text-sm font-semibold ${THEME.colors.text.muted} mb-1`}>{course.code}</p>
          <h3 className={`text-lg font-bold ${THEME.colors.text.primary}`}>{course.name}</h3>
        </div>
        <span className={`inline-flex items-center px-3 py-1 rounded-lg text-xs font-semibold whitespace-nowrap ml-2 ${courseTypeColor}`}>
          {course.courseType}
        </span>
      </div>

      {/* Description */}
      {course.description && (
        <p className={`${THEME.colors.text.secondary} text-sm mb-4 line-clamp-2`}>
          {course.description}
        </p>
      )}

      {/* Metadata */}
      <div className="grid grid-cols-2 gap-3 mb-4 text-sm">
        <div>
          <p className={`${THEME.colors.text.muted} text-xs mb-1`}>Credits</p>
          <p className={`font-semibold text-cyan-600`}>{course.credits}</p>
        </div>
        <div>
          <p className={`${THEME.colors.text.muted} text-xs mb-1`}>Hours/Week</p>
          <p className={`font-semibold ${THEME.colors.text.primary}`}>{course.hoursPerWeek}h</p>
        </div>
        <div>
          <p className={`${THEME.colors.text.muted} text-xs mb-1`}>Grade Level</p>
          <p className={`font-semibold ${THEME.colors.text.primary}`}>{gradeLevel}</p>
        </div>
        <div>
          <p className={`${THEME.colors.text.muted} text-xs mb-1`}>Semester</p>
          <p className={`font-semibold ${THEME.colors.text.primary}`}>{semester}</p>
        </div>
      </div>

      {/* Specialization */}
      <div className="mb-4 p-3 bg-gradient-to-r from-slate-50 to-slate-100 rounded-lg">
        <p className={`${THEME.colors.text.muted} text-xs mb-1`}>Specialization</p>
        <p className={`font-semibold ${THEME.colors.text.primary}`}>{course.specialization.name}</p>
      </div>

      {/* Prerequisites */}
      {course.prerequisite && (
        <div className="mb-4 p-3 bg-amber-50 border border-amber-200 rounded-lg">
          <p className={`${THEME.colors.text.muted} text-xs mb-1`}>Prerequisite</p>
          <p className={`font-semibold text-amber-700`}>{course.prerequisite.name}</p>
          <p className={`text-xs text-amber-600`}>{course.prerequisite.code}</p>
        </div>
      )}
    </div>
  );
}
