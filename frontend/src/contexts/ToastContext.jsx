import { createContext, useContext, useState, useCallback, useEffect, useRef } from 'react';
import { useLocation } from 'react-router-dom';

const ToastContext = createContext(null);

export function ToastProvider({ children }) {
  const [message, setMessage] = useState(null);
  const location = useLocation();
  const timerRef = useRef(null);

  // 페이지 이동 시 메시지 초기화
  useEffect(() => {
    setMessage(null);
  }, [location.pathname]);

  const showToast = useCallback((text, type = 'info') => {
    if (timerRef.current) clearTimeout(timerRef.current);
    setMessage({ text, type });
    timerRef.current = setTimeout(() => setMessage(null), 3000);
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
