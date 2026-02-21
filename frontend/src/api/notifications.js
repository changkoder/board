import client from './client';

export const notificationApi = {
  getAll() {
    return client.get('/notifications');
  },

  markAsRead(notificationId) {
    return client.patch(`/notifications/${notificationId}/read`);
  },

  markAllAsRead() {
    return client.patch('/notifications/read-all');
  },
};
