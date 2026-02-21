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

  getMyPosts() {
    return client.get('/users/me/posts');
  },

  getMyComments() {
    return client.get('/users/me/comments');
  },

  getMyLikes() {
    return client.get('/users/me/likes');
  },

  getMyBookmarks() {
    return client.get('/users/me/bookmarks');
  },
};
