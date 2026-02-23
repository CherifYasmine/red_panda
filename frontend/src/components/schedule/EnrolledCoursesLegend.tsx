import type { CurrentEnrollment } from '../../types/Enrollment';
import { THEME } from '../../constants/theme';

interface EnrolledCoursesLegendProps {
  enrollments: CurrentEnrollment[];
  colors: string[];
  onDropCourse?: (enrollmentId: number) => void;
}

export function EnrolledCoursesLegend({ enrollments, colors, onDropCourse }: EnrolledCoursesLegendProps) {
  const getSlotColor = (index: number): string => colors[index % colors.length];

  return (
    <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-6 border-2 ${THEME.colors.borders.light}`}>
      <h3 className={`text-lg font-bold ${THEME.colors.text.primary} mb-4`}>Enrolled Courses</h3>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {enrollments
          .filter((enrollment) => enrollment.section && enrollment.section.course && enrollment.section.teacher && enrollment.section.classroom)
          .map((enrollment, idx) => {
            const section = enrollment.section!;
            return (
              <div key={enrollment.id} className={`${getSlotColor(idx)} p-4 rounded-lg border-2 flex justify-between items-start`}>
                <div className="flex-1">
                  <p className="font-bold">{section.course.code}</p>
                  <p className="text-sm">{section.course.name}</p>
                  <p className="text-xs mt-2">
                    <span className="font-semibold">Teacher:</span> {section.teacher.firstName} {section.teacher.lastName}
                  </p>
                  <p className="text-xs">
                    <span className="font-semibold">Room:</span> {section.classroom.name}
                  </p>
                </div>
                {onDropCourse && (
                  <button
                    onClick={() => onDropCourse(enrollment.id)}
                    className="ml-2 text-red-500 hover:text-red-700 font-bold text-lg"
                    title="Drop course"
                  >
                    ✕
                  </button>
                )}
              </div>
            );
          })}
      </div>
    </div>
  );
}
