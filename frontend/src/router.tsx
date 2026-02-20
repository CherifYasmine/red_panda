import { createBrowserRouter, Navigate, type RouteObject } from 'react-router-dom';
import App from './App';
import LoginPage from './pages/LoginPage';
import { Dashboard } from './pages/Dashboard';
import { Layout } from './components/Layout';
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
        path: '',
        element: <Navigate to="/dashboard" replace />,
      },
    ],
  },
];

export const router = createBrowserRouter(routes);

