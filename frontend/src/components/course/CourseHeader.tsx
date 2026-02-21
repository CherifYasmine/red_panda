import { THEME } from '../../constants/theme';
import type { Course } from '../../types/Course';

interface CourseHeaderProps {
  course: Course;
}

export function CourseHeader({ course }: CourseHeaderProps) {
  const gradeLevel =
    course.gradeLevelMin === course.gradeLevelMax
      ? `Grade ${course.gradeLevelMin}`
      : `Grades ${course.gradeLevelMin}-${course.gradeLevelMax}`;

  const semester = course.semesterOrder === 1 ? 'Fall' : 'Spring';
  const courseTypeColor =
    course.courseType === 'CORE' ? 'bg-blue-100 text-blue-700' : 'bg-purple-100 text-purple-700';

  return (
    <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-8 mb-8 border-2 ${THEME.colors.borders.light}`}>
      <div className="flex items-start justify-between mb-4">
        <div className="flex-1">
          <p className={`text-sm font-semibold ${THEME.colors.text.muted} mb-2`}>{course.code}</p>
          <h1 className={`text-4xl font-bold ${THEME.colors.text.primary} mb-2`}>{course.name}</h1>
        </div>
        <span
          className={`inline-flex items-center px-4 py-2 rounded-lg text-sm font-semibold whitespace-nowrap ml-4 ${courseTypeColor}`}
        >
          {course.courseType}
        </span>
      </div>

      {course.description && (
        <p className={`${THEME.colors.text.secondary} text-lg mb-6`}>{course.description}</p>
      )}

      <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
        <div>
          <p className={`${THEME.colors.text.muted} text-sm mb-2`}>Credits</p>
          <p className="font-bold text-cyan-600 text-xl">{course.credits}</p>
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
  );
}
