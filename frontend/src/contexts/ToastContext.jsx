import { createContext, useContext, useState, useCallback, useEffect } from 'react';
import { useLocation } from 'react-router-dom';

const ToastContext = createContext(null);

export function ToastProvider({ children }) {
  const [message, setMessage] = useState(null);
  const location = useLocation();

  // 페이지 이동 시 메시지 초기화
  useEffect(() => {
    setMessage(null);
  }, [location.pathname]);

  const showToast = useCallback((text, type = 'info') => {
    setMessage({ text, type });
  }, []);

  const clearToast = useCallback(() => {
    setMessage(null);
  }, []);

  return (
    <ToastContext.Provider value={{ message, showToast, clearToast }}>
      {children}
    </ToastContext.Provider>
  );
}

export function useToast() {
  const context = useContext(ToastContext);
  if (!context) {
    throw new Error('useToast must be used within ToastProvider');
  }
  return context;
}
