import { useState } from 'react';
import { useAuthStore } from '../stores/authStore';
import { THEME } from '../constants/theme';

export function Dashboard() {
  const { student, refreshStudent } = useAuthStore();
  const [isRefreshing, setIsRefreshing] = useState(false);

  const handleRefresh = async () => {
    setIsRefreshing(true);
    await refreshStudent();
    setIsRefreshing(false);
  };

  const metrics = student?.academicMetrics;
  const firstName = student?.firstName || 'Student';
  const lastName = student?.lastName || '';
  const fullName = `${firstName} ${lastName}`.trim();

  return (
    <div>
      {/* Welcome Section */}
      <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-8 mb-8 border-2 ${THEME.colors.borders.light} flex items-center justify-between`}>
        <div>
          <h1 className={`text-3xl font-bold ${THEME.colors.text.primary} mb-2`}>
            Welcome back, {fullName}! 👋
          </h1>
          <p className={`${THEME.colors.text.secondary} text-lg`}>
            Ready to plan your course schedule?
          </p>
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
        {/* Card 1 */}
        <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-6 border-2 ${THEME.colors.borders.light}`}>
          <div className={`inline-flex items-center justify-center w-12 h-12 bg-gradient-to-br from-blue-100 to-cyan-100 rounded-xl mb-4`}>
            <svg className="w-6 h-6 text-cyan-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <h3 className={`text-lg font-semibold ${THEME.colors.text.primary} mb-2`}>Enrolled Courses</h3>
          <p className={`text-3xl font-bold ${THEME.colors.text.accent}`}>0</p>
          <p className={`${THEME.colors.text.muted} text-sm mt-1`}>This semester</p>
        </div>

        {/* Card 2 */}
        <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-6 border-2 ${THEME.colors.borders.light}`}>
          <div className={`inline-flex items-center justify-center w-12 h-12 bg-gradient-to-br from-emerald-100 to-teal-100 rounded-xl mb-4`}>
            <svg className="w-6 h-6 text-teal-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <h3 className={`text-lg font-semibold ${THEME.colors.text.primary} mb-2`}>GPA</h3>
          <p className={`text-3xl font-bold text-teal-600`}>{metrics?.gpa || '0.0'}</p>
          <p className={`${THEME.colors.text.muted} text-sm mt-1`}>{metrics?.gpa ? 'Current' : 'Not yet calculated'}</p>
        </div>

        {/* Card 3 */}
        <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-6 border-2 ${THEME.colors.borders.light}`}>
          <div className={`inline-flex items-center justify-center w-12 h-12 bg-gradient-to-br from-orange-100 to-amber-100 rounded-xl mb-4`}>
            <svg className="w-6 h-6 text-orange-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <h3 className={`text-lg font-semibold ${THEME.colors.text.primary} mb-2`}>Credits</h3>
          <p className={`text-3xl font-bold text-orange-600`}>{metrics?.creditsEarned || '0'}/30</p>
          <p className={`${THEME.colors.text.muted} text-sm mt-1`}>This semester</p>
        </div>
      </div>

      {/* Quick Actions */}
      <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-8 border-2 ${THEME.colors.borders.light}`}>
        <h2 className={`text-2xl font-bold ${THEME.colors.text.primary} mb-6`}>Quick Actions</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <button className={`px-6 py-4 bg-gradient-to-r ${THEME.colors.gradients.button} text-white font-semibold rounded-xl hover:shadow-lg transition-all transform hover:scale-105 active:scale-95 flex items-center gap-3`}>
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
