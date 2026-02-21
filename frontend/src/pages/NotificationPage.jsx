import { useState, useEffect } from 'react';
import { notificationApi } from '../api/notifications';

export default function NotificationPage() {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);

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

  const handleMarkAsRead = async (id) => {
    try {
      await notificationApi.markAsRead(id);
      fetchNotifications();
    } catch (err) {
      alert(err.response?.data?.message || '읽음 처리에 실패했습니다.');
    }
  };

  const handleMarkAllAsRead = async () => {
    try {
      await notificationApi.markAllAsRead();
      fetchNotifications();
    } catch (err) {
      alert(err.response?.data?.message || '전체 읽음 처리에 실패했습니다.');
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
              onClick={() => !noti.read && handleMarkAsRead(noti.id)}
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
