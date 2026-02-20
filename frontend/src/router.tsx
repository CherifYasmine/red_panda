import { createBrowserRouter, Navigate, type RouteObject } from 'react-router-dom';
import App from './App';
import LoginPage from './pages/LoginPage';
import { Dashboard } from './pages/Dashboard';
import { CourseCatalog } from './pages/CourseCatalog';
import { CourseDetail } from './pages/CourseDetail';
import { Schedule } from './pages/Schedule';
import { StudentHistory } from './pages/StudentHistory';
import { AdminPanel } from './pages/AdminPanel';
import { AdminCourses } from './components/admin/AdminCourses';
import { AdminCourseDetail } from './components/admin/AdminCourseDetail';
import { AdminSectionDetail } from './components/admin/AdminSectionDetail';
import { Layout } from './components/layout/Layout';
import { ProtectedRoute } from './components/ProtectedRoute';

const routes: RouteObject[] = [
  {
    path: '/',
    element: <App />,
    children: [
      {
        path: 'login',
        element: <LoginPage />,
      },
      {
        path: 'dashboard',
        element: (
          <ProtectedRoute>
            <Layout>
              <Dashboard />
            </Layout>
          </ProtectedRoute>
        ),
      },
      {
        path: 'courses',
        element: (
          <ProtectedRoute>
            <Layout>
              <CourseCatalog />
            </Layout>
          </ProtectedRoute>
        ),
      },
      {
        path: 'course/:id',
        element: (
          <ProtectedRoute>
            <Layout>
              <CourseDetail />
            </Layout>
          </ProtectedRoute>
        ),
      },
      {
        path: 'schedule',
        element: (
          <ProtectedRoute>
            <Layout>
              <Schedule />
            </Layout>
          </ProtectedRoute>
        ),
      },
      {
        path: 'history',
        element: (
          <ProtectedRoute>
            <Layout>
              <StudentHistory />
            </Layout>
          </ProtectedRoute>
        ),
      },
      {
        path: 'admin',
        children: [
          {
            index: true,
            element: <Navigate to="/admin/courses" replace />,
          },
          {
            path: 'courses',
            element: (
              <ProtectedRoute>
                <Layout>
                  <AdminCourses />
                </Layout>
              </ProtectedRoute>
            ),
          },
          {
            path: 'courses/:courseId',
            element: (
              <ProtectedRoute>
                <Layout>
                  <AdminCourseDetail />
                </Layout>
              </ProtectedRoute>
            ),
          },
          {
            path: 'courses/:courseId/sections/:sectionId',
            element: (
              <ProtectedRoute>
                <Layout>
                  <AdminSectionDetail />
                </Layout>
              </ProtectedRoute>
            ),
          },
          {
            path: 'quick',
            element: (
              <ProtectedRoute>
                <Layout>
                  <AdminPanel />
                </Layout>
              </ProtectedRoute>
            ),
          },
        ],
      },
      {
        path: '',
        element: <Navigate to="/dashboard" replace />,
      },
    ],
  },
];

export const router = createBrowserRouter(routes);

