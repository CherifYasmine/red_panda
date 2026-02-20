import axios from 'axios';
import type { AxiosInstance, InternalAxiosRequestConfig } from 'axios';

/**
 * Configured Axios instance for API communication
 * Base URL: http://localhost:8080/api/v1
 */
const client: AxiosInstance = axios.create({
  baseURL: 'http://localhost:8080/api/v1',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

/**
 * Request interceptor
 */
client.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const studentId = localStorage.getItem('studentId');
    if (studentId) {
      config.headers['X-Student-ID'] = studentId;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

/**
 * Response interceptor: Handle global error responses
 * 401/403: Redirect to login
 * 404: Resource not found
 * 400: Validation errors
 * 500: Server errors
 */
client.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response) {
      switch (error.response.status) {
        case 401:
          // Unauthorized - redirect to login
          localStorage.removeItem('studentId');
          localStorage.removeItem('email');
          window.location.href = '/login';
          break;
        case 403:
          console.error('Forbidden: You do not have permission');
          break;
        case 404:
          console.error('Not found:', error.response.data);
          break;
        case 400:
          console.error('Validation error:', error.response.data);
          break;
        default:
          console.error('Error:', error.response.data);
      }
    } else if (error.request) {
      console.error('No response received:', error.request);
    } else {
      console.error('Error:', error.message);
    }
    return Promise.reject(error);
  }
);

export default client;
