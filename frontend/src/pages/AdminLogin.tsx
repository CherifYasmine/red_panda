import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '../stores/authStore';
import { THEME, BUTTON_HOVER, INPUT_FOCUS } from '../constants/theme';

export function AdminLogin() {
  const navigate = useNavigate();
  const { loginAdmin, error, clearError } = useAuthStore();

  const [password, setPassword] = useState('');
  const [localError, setLocalError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const handleAdminLogin = async (e: React.SyntheticEvent<HTMLFormElement>) => {
    e.preventDefault();
    setLocalError(null);
    clearError();

    if (!password.trim()) {
      setLocalError('Password is required');
      return;
    }

    try {
      setIsLoading(true);
      loginAdmin(password.trim());
      // Wait a moment for state to update in localStorage
      setTimeout(() => {
        navigate('/admin');
        setIsLoading(false);
      }, 100);
    } catch {
      setLocalError(error || 'Invalid password');
      setIsLoading(false);
    }
  };

  return (
    <div className={`min-h-screen ${THEME.colors.backgrounds.main} flex items-center justify-center p-4 relative overflow-hidden`}>
      {/* Background decorative blobs */}
      <div className="absolute top-20 left-10 w-72 h-72 bg-blue-200 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-blob"></div>
      <div className="absolute top-40 right-10 w-72 h-72 bg-cyan-200 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-blob animation-delay-2000"></div>
      <div className="absolute -bottom-8 left-20 w-72 h-72 bg-teal-200 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-blob animation-delay-4000"></div>

      <div className="w-full max-w-md relative z-10">
        {/* Card */}
        <div className={`${THEME.colors.backgrounds.card} ${THEME.spacing.cardRadius} ${THEME.shadows.card} p-8 border-2 ${THEME.colors.borders.card}`}>
          {/* Decorative top accent */}
          <div className={`h-1 bg-gradient-to-r ${THEME.colors.gradients.accent} rounded-full mb-8`}></div>

          {/* Header with icon */}
          <div className="text-center mb-8">
            <div className={`inline-flex items-center justify-center w-20 h-20 bg-gradient-to-br from-amber-400 to-orange-500 ${THEME.spacing.iconRadius} mb-4 transform hover:scale-110 transition-transform ${THEME.shadows.button}`}>
              <span className="text-2xl font-bold text-white">⚙️</span>
            </div>
            <h1 className={`text-4xl font-bold ${THEME.colors.text.primary} mb-2`}>Admin Panel</h1>
            <p className={`${THEME.colors.text.accent} text-lg font-semibold`}>Secure Access</p>
            <p className={`${THEME.colors.text.muted} text-sm mt-2`}>Enter admin password to continue</p>
          </div>

          {/* Form */}
          <form onSubmit={handleAdminLogin} className="space-y-5">
            {/* Error Message */}
            {(localError || error) && (
              <div className={`p-4 ${THEME.colors.status.error.bg} border-l-4 ${THEME.colors.status.error.border} rounded-lg flex items-start gap-3`}>
                <svg className={`w-5 h-5 ${THEME.colors.status.error.icon} flex-shrink-0 mt-0.5`} fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
                </svg>
                <p className={`${THEME.colors.status.error.text} text-sm font-medium`}>{localError || error}</p>
              </div>
            )}

            {/* Password Input */}
            <div>
              <label htmlFor="password" className={`block text-sm font-semibold ${THEME.colors.text.primary} mb-3`}>
                Admin Password
              </label>
              <input
                id="password"
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Enter admin password"
                disabled={isLoading}
                className={`w-full px-4 py-3 border-2 ${THEME.colors.borders.light} ${THEME.spacing.inputRadius} focus:outline-none ${INPUT_FOCUS} disabled:bg-gray-50 disabled:text-gray-500 transition-all text-gray-900 placeholder-gray-400 hover:border-gray-300`}
              />
            </div>

            {/* Login Button */}
            <button
              type="submit"
              disabled={isLoading}
              className={`w-full bg-gradient-to-r from-amber-500 to-orange-500 ${BUTTON_HOVER} text-white font-semibold py-3 px-4 ${THEME.spacing.buttonRadius} transition-all transform hover:scale-105 active:scale-95 disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:scale-100 ${THEME.shadows.button} hover:${THEME.shadows.buttonHover} flex items-center justify-center gap-2`}
            >
              {isLoading ? (
                <>
                  <svg className="animate-spin w-5 h-5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  <span>Verifying...</span>
                </>
              ) : (
                <>
                  <span>Access Admin Panel</span>
                  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 7l5 5m0 0l-5 5m5-5H6" />
                  </svg>
                </>
              )}
            </button>
          </form>

          {/* Divider */}
          <div className="my-6 flex items-center gap-4">
            <div className={`flex-1 h-px ${THEME.colors.borders.light}`}></div>
            <span className={`text-xs ${THEME.colors.text.muted} font-medium`}>OR</span>
            <div className={`flex-1 h-px ${THEME.colors.borders.light}`}></div>
          </div>

          {/* Back to login */}
          <button
            type="button"
            onClick={() => navigate('/login')}
            className={`w-full px-4 py-2 border-2 ${THEME.colors.borders.light} text-slate-700 font-semibold rounded-lg hover:bg-slate-50 transition-all`}
          >
            Back to Login
          </button>
        </div>

        {/* Footer info */}
        <p className={`text-center text-sm ${THEME.colors.text.secondary} mt-8`}>
          © 2026 Maplewood High School
        </p>
      </div>
    </div>
  );
}
