import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { notificationApi } from '../api/notifications';
import { useToast } from '../contexts/ToastContext';
import { useNotification } from '../contexts/NotificationContext';

export default function NotificationPage() {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  const { showToast } = useToast();
  const { refreshCount } = useNotification();

  const fetchNotifications = () => {
    setLoading(true);
    notificationApi
      .getAll()
      .then((res) => setNotifications(res.data.data))
      .catch(() => setNotifications([]))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchNotifications();
  }, []);

  const handleClick = async (noti) => {
    try {
      if (!noti.read) {
        await notificationApi.markAsRead(noti.id);
        refreshCount();
      }
      if (noti.postId) {
        navigate(`/posts/${noti.postId}`);
      }
    } catch (err) {
      showToast(err.response?.data?.message || '알림 처리에 실패했습니다.', 'error');
    }
  };

  const handleMarkAllAsRead = async () => {
    try {
      await notificationApi.markAllAsRead();
      fetchNotifications();
      refreshCount();
    } catch (err) {
      showToast(err.response?.data?.message || '전체 읽음 처리에 실패했습니다.', 'error');
    }
  };

  if (loading) return <div className="loading">로딩 중...</div>;

  const unreadCount = notifications.filter((n) => !n.read).length;

  return (
    <div className="page">
      <div className="page-header-row">
        <h1>알림</h1>
        {unreadCount > 0 && (
          <button onClick={handleMarkAllAsRead} className="btn btn-sm">
            전체 읽음
          </button>
        )}
      </div>

      {notifications.length === 0 ? (
        <p className="empty">알림이 없습니다.</p>
      ) : (
        <ul className="notification-list">
          {notifications.map((noti) => (
            <li
              key={noti.id}
              className={`notification-item ${noti.read ? '' : 'notification-unread'}`}
              onClick={() => handleClick(noti)}
            >
              <p className="notification-message">{noti.message}</p>
              <span className="notification-date">
                {new Date(noti.createdAt).toLocaleString()}
              </span>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
