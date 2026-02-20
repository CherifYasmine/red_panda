import { useState, useEffect } from 'react';
import { useAuthStore } from '../stores/authStore';
import { useEnrollmentStore } from '../stores/enrollmentStore';
import { THEME } from '../constants/theme';
import { StatsCard } from '../components/common/StatsCard';

export function Dashboard() {
  const { student, refreshStudent, studentId } = useAuthStore();
  const { currentEnrollments, fetchCurrentEnrollments } = useEnrollmentStore();
  const [isRefreshing, setIsRefreshing] = useState(false);

  // Fetch enrollments on mount
  useEffect(() => {
    if (studentId) {
      fetchCurrentEnrollments(studentId);
    }
  }, [studentId, fetchCurrentEnrollments]);

  const handleRefresh = async () => {
    setIsRefreshing(true);
    await refreshStudent();
    if (studentId) {
      await fetchCurrentEnrollments(studentId);
    }
    setIsRefreshing(false);
  };

  const metrics = student?.academicMetrics;
  const firstName = student?.firstName || 'Student';
  const lastName = student?.lastName || '';
  const fullName = `${firstName} ${lastName}`.trim();
  const enrolledCoursesCount = currentEnrollments?.length || 0;

  return (
    <div>
      {/* Welcome Section */}
      <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-8 mb-8 border-2 ${THEME.colors.borders.light} flex items-center justify-between`}>
        <div>
          <h1 className={`text-3xl font-bold ${THEME.colors.text.primary} mb-2`}>
            Welcome back, {fullName}! 👋
          </h1>
          <div className="flex items-center gap-4">
            <p className={`${THEME.colors.text.secondary} text-lg`}>
              Ready to plan your course schedule?
            </p>
            {student?.status && (
              <span className={`inline-flex items-center px-3 py-1 rounded-full text-sm font-semibold ${
                student.status === 'ACTIVE' 
                  ? 'bg-green-100 text-green-700' 
                  : student.status === 'GRADUATED'
                  ? 'bg-blue-100 text-blue-700'
                  : 'bg-yellow-100 text-yellow-700'
              }`}>
                {student.status}
              </span>
            )}
          </div>
        </div>
        <button
          onClick={handleRefresh}
          disabled={isRefreshing}
          className={`px-4 py-2 rounded-xl border-2 ${THEME.colors.borders.light} ${THEME.colors.text.accent} font-semibold hover:bg-cyan-50 transition-all ${isRefreshing ? 'opacity-50 cursor-not-allowed' : ''}`}
        >
          <svg className={`w-5 h-5 inline mr-2 ${isRefreshing ? 'animate-spin' : ''}`} fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.5a8.5 8.5 0 1 1-2.5 5" />
          </svg>
          Refresh
        </button>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        {/* Enrolled Courses */}
        <StatsCard
          icon={
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          }
          title="Enrolled Courses"
          value={enrolledCoursesCount}
          subtitle="This semester"
          iconGradient="from-blue-100 to-cyan-100"
        />

        {/* GPA */}
        <StatsCard
          icon={
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          }
          title="GPA"
          value={metrics?.gpa || '0.0'}
          subtitle={metrics?.gpa ? 'Current' : 'Not yet calculated'}
          valueColor="text-teal-600"
          iconGradient="from-emerald-100 to-teal-100"
        />

        {/* Credits */}
        <StatsCard
          icon={
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          }
          title="Credits"
          value={`${metrics?.creditsEarned || '0'}/30`}
          subtitle="This semester"
          valueColor="text-orange-600"
          iconGradient="from-orange-100 to-amber-100"
        />
      </div>

      {/* Credits Progress Section */}
      <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-8 border-2 ${THEME.colors.borders.light}`}>
        <div className="flex items-center justify-between mb-6">
          <h2 className={`text-xl font-bold ${THEME.colors.text.primary}`}>
            Graduation Progress
          </h2>
          {metrics?.isGraduated && (
            <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-semibold bg-green-100 text-green-700">
              ✓ Graduated
            </span>
          )}
        </div>

        {/* Credits Progress */}
        <div className="mb-8">
          <div className="flex items-center justify-between mb-3">
            <span className={`font-semibold ${THEME.colors.text.secondary}`}>
              Credits Earned
            </span>
            <span className={`text-lg font-bold ${THEME.colors.text.primary}`}>
              {metrics?.creditsEarned || 0}/30
            </span>
          </div>
          
          {/* Progress Bar */}
          <div className={`h-3 bg-gray-200 rounded-full overflow-hidden`}>
            <div
              className={`h-full rounded-full transition-all duration-300 ${
                (metrics?.creditsEarned || 0) >= 30
                  ? 'bg-gradient-to-r from-green-400 to-emerald-500'
                  : (metrics?.creditsEarned || 0) >= 20
                  ? 'bg-gradient-to-r from-blue-400 to-cyan-500'
                  : 'bg-gradient-to-r from-orange-400 to-amber-500'
              }`}
              style={{
                width: `${Math.min(((metrics?.creditsEarned || 0) / 30) * 100, 100)}%`,
              }}
            />
          </div>
          
          <div className="flex items-center justify-between mt-2">
            <span className={`text-xs ${THEME.colors.text.secondary}`}>
              {Math.round(((metrics?.creditsEarned || 0) / 30) * 100)}% complete
            </span>
            <span className={`text-xs ${THEME.colors.text.secondary}`}>
              {30 - (metrics?.creditsEarned || 0)} credits remaining
            </span>
          </div>
        </div>

        {/* Current Semester Enrollment Status */}
        <div className={`p-4 rounded-xl ${
          enrolledCoursesCount >= 4
            ? 'bg-yellow-50 border-2 border-yellow-200'
            : 'bg-green-50 border-2 border-green-200'
        }`}>
          <div className="flex items-center justify-between">
            <span className={`font-semibold ${
              enrolledCoursesCount >= 4
                ? 'text-yellow-700'
                : 'text-green-700'
            }`}>
              This Semester
            </span>
            <span className={`text-lg font-bold ${
              enrolledCoursesCount >= 4
                ? 'text-yellow-700'
                : 'text-green-700'
            }`}>
              {enrolledCoursesCount}/5 courses
            </span>
          </div>
          {enrolledCoursesCount >= 4 && (
            <p className="text-sm text-yellow-700 mt-2">
              ⚠️ You're approaching the course limit for this semester
            </p>
          )}
        </div>
      </div>
    </div>
  );
}
