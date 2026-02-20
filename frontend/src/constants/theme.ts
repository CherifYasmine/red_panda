/**
 * Maplewood Theme Configuration
 * Central place for all colors and design tokens
 */

export const THEME = {
  // Primary color palette
  colors: {
    // Background gradients
    backgrounds: {
      main: 'bg-gradient-to-br from-blue-50 via-cyan-50 to-teal-50',
      card: 'bg-white',
    },

    // Gradient accents
    gradients: {
      primary: 'from-blue-400 to-cyan-400',
      button: 'from-blue-500 to-cyan-500',
      buttonHover: 'from-blue-600 to-cyan-600',
      accent: 'from-blue-600 via-cyan-500 to-teal-500',
    },

    // Text colors
    text: {
      primary: 'text-gray-900',
      secondary: 'text-gray-600',
      muted: 'text-gray-500',
      accent: 'text-cyan-600',
      accentHover: 'text-cyan-700',
      light: 'text-gray-400',
    },

    // Border colors
    borders: {
      light: 'border-gray-200',
      card: 'border-blue-100',
      focus: 'border-cyan-400',
    },

    // Status colors
    status: {
      error: {
        bg: 'bg-red-50',
        border: 'border-red-400',
        text: 'text-red-700',
        icon: 'text-red-600',
      },
      success: {
        bg: 'bg-green-50',
        border: 'border-green-400',
        text: 'text-green-700',
        icon: 'text-green-600',
      },
    },

    // Focus states
    focus: {
      ring: 'focus:ring-cyan-100',
      border: 'focus:border-cyan-400',
    },
  },

  // Spacing and sizing
  spacing: {
    cardRadius: 'rounded-3xl',
    inputRadius: 'rounded-xl',
    buttonRadius: 'rounded-xl',
    iconRadius: 'rounded-2xl',
  },

  // Shadows
  shadows: {
    card: 'shadow-2xl',
    button: 'shadow-lg',
    buttonHover: 'shadow-xl',
  },
};

// Convenience exports for common patterns
export const PRIMARY_GRADIENT = `bg-gradient-to-r ${THEME.colors.gradients.button}`;
export const BUTTON_HOVER = `hover:from-blue-600 hover:to-cyan-600`;
export const INPUT_FOCUS = `focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100`;
export const CARD_BORDER = `border-blue-100`;
export const ACCENT_TEXT = `text-cyan-600 hover:text-cyan-700`;

// Animation delays for blobs
export const BLOB_ANIMATION = {
  delay1: 'animation-delay-0',
  delay2: 'animation-delay-2000',
  delay3: 'animation-delay-4000',
};
