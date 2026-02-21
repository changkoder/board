import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';
import { ToastProvider } from './contexts/ToastContext';
import Layout from './components/layout/Layout';
import PrivateRoute from './components/layout/PrivateRoute';
import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
import SignupPage from './pages/SignupPage';
import PostDetailPage from './pages/PostDetailPage';
import PostCreatePage from './pages/PostCreatePage';
import PostEditPage from './pages/PostEditPage';
import MyPage from './pages/MyPage';
import NotificationPage from './pages/NotificationPage';
import AdminPage from './pages/AdminPage';
import NotFoundPage from './pages/NotFoundPage';

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <ToastProvider>
        <Routes>
          <Route element={<Layout />}>
            {/* 공개 페이지 */}
            <Route path="/" element={<HomePage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/signup" element={<SignupPage />} />
            <Route path="/posts/:id" element={<PostDetailPage />} />

            {/* 인증 필요 페이지 */}
            <Route
              path="/posts/new"
              element={
                <PrivateRoute>
                  <PostCreatePage />
                </PrivateRoute>
              }
            />
            <Route
              path="/posts/:id/edit"
              element={
                <PrivateRoute>
                  <PostEditPage />
                </PrivateRoute>
              }
            />
            <Route
              path="/mypage"
              element={
                <PrivateRoute>
                  <MyPage />
                </PrivateRoute>
              }
            />
            <Route
              path="/notifications"
              element={
                <PrivateRoute>
                  <NotificationPage />
                </PrivateRoute>
              }
            />
            <Route
              path="/admin"
              element={
                <PrivateRoute>
                  <AdminPage />
                </PrivateRoute>
              }
            />

            {/* 404 */}
            <Route path="*" element={<NotFoundPage />} />
          </Route>
        </Routes>
        </ToastProvider>
      </AuthProvider>
    </BrowserRouter>
  );
}
