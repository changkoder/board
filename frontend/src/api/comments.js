import client from './client';

export const commentApi = {
  getByPostId(postId) {
    return client.get(`/posts/${postId}/comments`);
  },

  create(postId, content, parentId = null) {
    const body = { content };
    if (parentId) body.parentId = parentId;
    return client.post(`/posts/${postId}/comments`, body);
  },

  update(commentId, content) {
    return client.patch(`/comments/${commentId}`, { content });
  },

  delete(commentId) {
    return client.delete(`/comments/${commentId}`);
  },

  toggleLike(commentId) {
    return client.post(`/comments/${commentId}/like`);
  },
};
