import { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { useAuth } from './AuthContext';
import { notificationApi } from '../api/notifications';

const NotificationContext = createContext();

export function NotificationProvider({ children }) {
  const [unreadCount, setUnreadCount] = useState(0);
  const { isAuthenticated } = useAuth();

  const refreshCount = useCallback(() => {
    if (!isAuthenticated) {
      setUnreadCount(0);
      return;
    }
    notificationApi
      .getUnreadCount()
      .then((res) => {
        setUnreadCount(res.data.data);
      })
      .catch(() => setUnreadCount(0));
  }, [isAuthenticated]);

  useEffect(() => {
    refreshCount();
    const interval = setInterval(refreshCount, 30000);
    return () => clearInterval(interval);
  }, [refreshCount]);

  return (
    <NotificationContext.Provider value={{ unreadCount, refreshCount }}>
      {children}
    </NotificationContext.Provider>
  );
}

export function useNotification() {
  return useContext(NotificationContext);
}
