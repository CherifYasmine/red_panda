/**
 * Backend error response structure
 * Used for type-safe error handling across the frontend
 */
export interface BackendError {
  response?: {
    status: number;
    data?: {
      error?: string;
      message?: string;
      details?: Record<string, unknown>;
      timestamp?: string;
      path?: string;
    };
  };
  message?: string;
}

/**
 * Extract user-friendly error message from backend error
 * @param err - The error object (typically from axios)
 * @returns Formatted error message for display
 */
export function getErrorMessage(err: unknown, fallback: string = 'An error occurred'): string {
  const backendErr = err as BackendError;
  
  if (backendErr.response?.data?.message) {
    return backendErr.response.data.message;
  }
  
  if (backendErr.response?.data?.error) {
    const errorDetail = backendErr.response.data.message || 'Unknown error';
    return `${backendErr.response.data.error}: ${errorDetail}`;
  }
  
  if (backendErr.message) {
    return backendErr.message;
  }
  
  return fallback;
}
