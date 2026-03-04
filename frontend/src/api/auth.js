import client from './client';

export const authApi = {
  signup(email, password, nickname) {
    return client.post('/auth/signup', { email, password, nickname });
  },

  login(email, password) {
    return client.post('/auth/login', { email, password });
  },

  logout() {
    return client.post('/auth/logout');
  },

  refresh(refreshToken) {
    return client.post('/auth/refresh', { refreshToken });
  },

  getMe() {
    return client.get('/users/me');
  },

  updateMe(nickname, profileImg) {
    return client.patch('/users/me', { nickname, profileImg });
  },

  changePassword(currentPassword, newPassword) {
    return client.patch('/users/me/password', { currentPassword, newPassword });
  },

  deleteAccount() {
    return client.delete('/users/me');
  },

  getMyPosts(page = 0, size = 10) {
    return client.get('/users/me/posts', { params: { page, size } });
  },

  getMyComments(page = 0, size = 10) {
    return client.get('/users/me/comments', { params: { page, size } });
  },

  getMyLikes(page = 0, size = 10) {
    return client.get('/users/me/likes', { params: { page, size } });
  },

  getMyBookmarks(page = 0, size = 10) {
    return client.get('/users/me/bookmarks', { params: { page, size } });
  },

  getUserByNickname(nickname) {
    return client.get(`/users/nickname/${encodeURIComponent(nickname)}`);
  },

  getUserProfile(userId) {
    return client.get(`/users/${userId}`);
  },

  getUserPosts(userId, page = 0, size = 10) {
    return client.get(`/users/${userId}/posts`, { params: { page, size } });
  },
};
