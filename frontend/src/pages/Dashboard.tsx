import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '../stores/authStore';
import { useEnrollmentStore } from '../stores/enrollmentStore';
import { THEME } from '../constants/theme';
import { StatsCard } from '../components/common/StatsCard';

export function Dashboard() {
  const navigate = useNavigate();
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

      {/* Quick Actions */}
      <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-8 border-2 ${THEME.colors.borders.light}`}>
        <h2 className={`text-2xl font-bold ${THEME.colors.text.primary} mb-6`}>Quick Actions</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <button onClick={() => navigate('/courses')} className={`px-6 py-4 bg-gradient-to-r ${THEME.colors.gradients.button} text-white font-semibold rounded-xl hover:shadow-lg transition-all transform hover:scale-105 active:scale-95 flex items-center gap-3`}>
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
            </svg>
            Browse Courses
          </button>
          <button className={`px-6 py-4 border-2 border-cyan-400 text-cyan-600 font-semibold rounded-xl hover:bg-cyan-50 transition-all`}>
            <svg className="w-5 h-5 inline mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
            View My Schedule
          </button>
        </div>
      </div>
    </div>
  );
}
