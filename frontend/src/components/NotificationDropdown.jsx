import { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { notificationApi } from '../api/notifications';
import { useToast } from '../contexts/ToastContext';
import { useNotification } from '../contexts/NotificationContext';

export default function NotificationDropdown({ open, onClose }) {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(false);
  const navigate = useNavigate();
  const { showToast } = useToast();
  const { refreshCount, unreadCount } = useNotification();
  const panelRef = useRef(null);

  const fetchItems = () => {
    setLoading(true);
    notificationApi
      .getAll(0, 10)
      .then((res) => {
        setItems(res.data.data.content);
        setError(false);
      })
      .catch(() => {
        setItems([]);
        setError(true);
      })
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    if (!open) return;
    fetchItems();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [open]);

  useEffect(() => {
    if (!open) return;
    const handleClickOutside = (e) => {
      // 토글 버튼 클릭은 Header에서 처리되므로 외부 클릭에서 제외
      if (e.target.closest('.nav-notification')) return;
      if (panelRef.current && !panelRef.current.contains(e.target)) {
        onClose();
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, [open, onClose]);

  if (!open) return null;

  const handleContentClick = async (noti) => {
    try {
      if (!noti.read) {
        await notificationApi.markAsRead(noti.id);
        refreshCount();
      }
      onClose();
      if (noti.postId) {
        navigate(`/posts/${noti.postId}`);
      }
    } catch (err) {
      showToast(err.response?.data?.message || '알림 처리에 실패했습니다.', 'error');
    }
  };

  const handleAvatarClick = async (e, noti) => {
    e.stopPropagation();
    try {
      if (!noti.read) {
        await notificationApi.markAsRead(noti.id);
        refreshCount();
      }
      onClose();
      navigate(`/users/${noti.actorId}`);
    } catch (err) {
      showToast(err.response?.data?.message || '알림 처리에 실패했습니다.', 'error');
    }
  };

  const handleMarkAllAsRead = async () => {
    try {
      await notificationApi.markAllAsRead();
      refreshCount();
      fetchItems();
    } catch (err) {
      showToast(err.response?.data?.message || '전체 읽음 처리에 실패했습니다.', 'error');
    }
  };

  const handleViewAll = () => {
    onClose();
    navigate('/notifications');
  };

  return (
    <div className="notification-dropdown" ref={panelRef}>
      <div className="notification-dropdown-header">
        <strong>알림</strong>
        {unreadCount > 0 && (
          <button className="btn-link notification-dropdown-mark-all" onClick={handleMarkAllAsRead}>
            전체 읽음
          </button>
        )}
      </div>
      <div className="notification-dropdown-body">
        {loading ? (
          <div className="notification-dropdown-empty">로딩 중...</div>
        ) : error ? (
          <div className="notification-dropdown-empty">불러오지 못했습니다.</div>
        ) : items.length === 0 ? (
          <div className="notification-dropdown-empty">알림이 없습니다.</div>
        ) : (
          <ul className="notification-dropdown-list">
            {items.map((noti) => (
              <li
                key={noti.id}
                className={`notification-dropdown-item ${noti.read ? '' : 'notification-unread'}`}
              >
                <span
                  className="inline-avatar"
                  onClick={(e) => handleAvatarClick(e, noti)}
                  style={{ cursor: 'pointer', flexShrink: 0 }}
                >
                  {noti.actorProfileImg ? (
                    <img src={noti.actorProfileImg} alt="" />
                  ) : (
                    <span className="inline-avatar-placeholder">
                      {noti.actorNickname?.charAt(0) || '?'}
                    </span>
                  )}
                </span>
                <div
                  className="notification-dropdown-content"
                  onClick={() => handleContentClick(noti)}
                  style={{ cursor: 'pointer', flex: 1, minWidth: 0 }}
                >
                  <p className="notification-dropdown-message">{noti.message}</p>
                  <span className="notification-dropdown-date">
                    {new Date(noti.createdAt).toLocaleString()}
                  </span>
                </div>
              </li>
            ))}
          </ul>
        )}
      </div>
      <div className="notification-dropdown-footer">
        <button className="btn-link" onClick={handleViewAll}>
          모두 보기
        </button>
      </div>
    </div>
  );
}
