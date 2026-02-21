import { THEME } from '../../constants/theme';
import type { Course } from '../../types/Course';

interface CourseSpecializationProps {
  course: Course;
}

export function CourseSpecialization({ course }: CourseSpecializationProps) {
  return (
    <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-6 mb-8 border-2 ${THEME.colors.borders.light}`}>
      <h2 className={`text-lg font-bold ${THEME.colors.text.primary} mb-4`}>Specialization</h2>
      <div className="p-4 bg-gradient-to-r from-slate-50 to-slate-100 rounded-lg">
        <p className={`text-lg font-semibold ${THEME.colors.text.primary}`}>{course.specialization.name}</p>
        {course.specialization.description && (
          <p className={`${THEME.colors.text.secondary} text-sm mt-2`}>{course.specialization.description}</p>
        )}
      </div>
    </div>
  );
}
