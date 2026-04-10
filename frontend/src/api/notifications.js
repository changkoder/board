import client from './client';

export const notificationApi = {
  getAll(page = 0, size = 10) {
    return client.get('/notifications', { params: { page, size } });
  },

  getUnreadCount() {
    return client.get('/notifications/unread-count');
  },

  markAsRead(notificationId) {
    return client.patch(`/notifications/${notificationId}/read`);
  },

  markAllAsRead() {
    return client.patch('/notifications/read-all');
  },
};
