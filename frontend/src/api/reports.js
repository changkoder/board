import client from './client';

export const reportApi = {
  reportPost(postId, reason) {
    return client.post(`/posts/${postId}/report`, { reason });
  },

  reportComment(commentId, reason) {
    return client.post(`/comments/${commentId}/report`, { reason });
  },
};
