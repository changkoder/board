import { createContext, useContext, useState, useEffect } from 'react';
import { authApi } from '../api/auth';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  // 앱 초기 로드 시 저장된 토큰으로 유저 정보 복원
  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      authApi
        .getMe()
        .then((res) => setUser(res.data.data))
        .catch((err) => {
          // 401/403만 토큰 삭제 (서버 다운 등 네트워크 에러는 토큰 유지)
          const status = err.response?.status;
          if (status === 401 || status === 403) {
            localStorage.removeItem('accessToken');
            localStorage.removeItem('refreshToken');
          }
        })
        .finally(() => setLoading(false));
    } else {
      setLoading(false);
    }
  }, []);

  // 세션 만료 이벤트 수신 → 로그아웃 처리
  useEffect(() => {
    const handleSessionExpired = () => setUser(null);
    window.addEventListener('auth:session-expired', handleSessionExpired);
    return () => window.removeEventListener('auth:session-expired', handleSessionExpired);
  }, []);

  const login = async (email, password) => {
    const res = await authApi.login(email, password);
    const { accessToken, refreshToken } = res.data.data;
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);

    const userRes = await authApi.getMe();
    setUser(userRes.data.data);
  };

  const logout = async () => {
    try {
      await authApi.logout();
    } catch {
      // 로그아웃 API 실패해도 로컬은 정리
    }
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    setUser(null);
  };

  const refreshUser = async () => {
    const res = await authApi.getMe();
    setUser(res.data.data);
  };

  const value = {
    user,
    loading,
    isAuthenticated: !!user,
    login,
    logout,
    refreshUser,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
}
