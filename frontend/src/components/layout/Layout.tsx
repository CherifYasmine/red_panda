import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '../../stores/authStore';
import { THEME } from '../../constants/theme';
import { Logo } from '../common/Logo';

export function Layout({ children }: { children: React.ReactNode }) {
  const navigate = useNavigate();
  const { logout, student } = useAuthStore();
  const [profileMenuOpen, setProfileMenuOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const firstName = student?.firstName || 'Student';
  const lastName = student?.lastName || '';
  const initials = `${firstName.charAt(0)}${lastName.charAt(0)}`.toUpperCase();

  return (
    <div className={`min-h-screen ${THEME.colors.backgrounds.main}`}>
      {/* Header */}
      <header className={`${THEME.colors.backgrounds.card} border-b-2 ${THEME.colors.borders.light} sticky top-0 z-50 ${THEME.shadows.card}`}>
        <div className="max-w-7xl mx-auto px-4 py-4 flex items-center justify-between">
          {/* Logo and title */}
          <div className="flex items-center gap-3">
            <div className={`inline-flex items-center justify-center w-10 h-10 bg-gradient-to-br ${THEME.colors.gradients.primary} ${THEME.spacing.iconRadius}`}>
              <Logo size="sm" />
            </div>
            <div>
              <h1 className={`text-xl font-bold ${THEME.colors.text.primary}`}>Maplewood</h1>
              <p className={`text-xs ${THEME.colors.text.muted}`}>Course Planning</p>
            </div>
          </div>

          {/* Navigation items - Center */}
          <nav className="hidden md:flex items-center gap-8">
            <a href="/dashboard" className={`${THEME.colors.text.secondary} hover:${THEME.colors.text.accent} font-medium transition-colors`}>
              Dashboard
            </a>
            <a href="/courses" className={`${THEME.colors.text.secondary} hover:${THEME.colors.text.accent} font-medium transition-colors`}>
              Browse Courses
            </a>
            <a href="#" className={`${THEME.colors.text.secondary} hover:${THEME.colors.text.accent} font-medium transition-colors`}>
              My Schedule
            </a>
          </nav>

          {/* Profile Menu */}
          <div className="relative">
            <button
              onClick={() => setProfileMenuOpen(!profileMenuOpen)}
              className={`flex items-center gap-2 px-4 py-2 rounded-xl bg-gradient-to-r ${THEME.colors.gradients.primary} text-white font-medium hover:shadow-lg transition-all`}
            >
              <div className="w-8 h-8 rounded-lg bg-white bg-opacity-20 flex items-center justify-center text-sm font-bold">
                {initials}
              </div>
              <span className="text-sm">{firstName}</span>
              <svg className={`w-4 h-4 transition-transform ${profileMenuOpen ? 'rotate-180' : ''}`} fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 14l-7 7m0 0l-7-7m7 7V3" />
              </svg>
            </button>

            {/* Dropdown Menu */}
            {profileMenuOpen && (
              <div className={`absolute right-0 mt-2 w-75 ${THEME.colors.backgrounds.card} rounded-xl ${THEME.shadows.card} border-2 ${THEME.colors.borders.light} overflow-hidden`}>
                <div className="px-4 py-4 border-b-2 border-gray-100">
                  <p className={`text-sm font-semibold ${THEME.colors.text.primary}`}>{firstName} {lastName}</p>
                  <p className={`text-xs ${THEME.colors.text.muted} mt-1`}>{student?.email}</p>
                  <p className={`text-xs ${THEME.colors.text.muted} mt-1`}>ID: {student?.id}</p>
                </div>
                <button
                  onClick={handleLogout}
                  className={`w-full text-left px-4 py-3 text-red-600 hover:bg-red-50 font-medium transition-colors flex items-center gap-2`}
                >
                  <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                  </svg>
                  Logout
                </button>
              </div>
            )}
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 py-8">
        {children}
      </main>
    </div>
  );
}
