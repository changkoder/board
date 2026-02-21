import client from './client';

export const adminApi = {
  getHiddenPosts() {
    return client.get('/admin/posts/hidden');
  },

  getHiddenComments() {
    return client.get('/admin/comments/hidden');
  },

  hidePost(postId) {
    return client.patch(`/admin/posts/${postId}/hide`);
  },

  hideComment(commentId) {
    return client.patch(`/admin/comments/${commentId}/hide`);
  },

  restorePost(postId) {
    return client.patch(`/admin/posts/${postId}/restore`);
  },

  restoreComment(commentId) {
    return client.patch(`/admin/comments/${commentId}/restore`);
  },

  deletePost(postId) {
    return client.delete(`/admin/posts/${postId}`);
  },

  deleteComment(commentId) {
    return client.delete(`/admin/comments/${commentId}`);
  },

  getBlockedUsers() {
    return client.get('/admin/users/blocked');
  },

  blockUser(userId) {
    return client.post(`/admin/users/${userId}/block`);
  },

  unblockUser(userId) {
    return client.delete(`/admin/users/${userId}/block`);
  },
};
