import client from './client';

export const postApi = {
  // 게시글 목록 (페이지네이션)
  getList(page = 0, size = 10, categoryId = null) {
    const params = { page, size, sort: 'createdAt,desc' };
    if (categoryId) params.categoryId = categoryId;
    return client.get('/posts', { params });
  },

  // 게시글 목록 (무한스크롤)
  getListInfinite(lastPostId = null, size = 10) {
    const params = { size };
    if (lastPostId) params.lastPostId = lastPostId;
    return client.get('/posts/infinite', { params });
  },

  // 게시글 검색
  search(searchType, keyword, page = 0, size = 10, categoryId = null) {
    const params = { searchType, keyword, page, size };
    if (categoryId) params.categoryId = categoryId;
    return client.get('/posts/search', { params });
  },

  // 인기 게시글
  getPopular() {
    return client.get('/posts/popular');
  },

  // 게시글 상세
  getById(postId) {
    return client.get(`/posts/${postId}`);
  },

  // 게시글 생성
  create(title, content, categoryId, imageUrls = []) {
    return client.post('/posts', { title, content, categoryId, imageUrls });
  },

  // 게시글 수정
  update(postId, title, content, categoryId, imageUrls = []) {
    return client.patch(`/posts/${postId}`, { title, content, categoryId, imageUrls });
  },

  // 게시글 삭제
  delete(postId) {
    return client.delete(`/posts/${postId}`);
  },

  // 좋아요 토글
  toggleLike(postId) {
    return client.post(`/posts/${postId}/like`);
  },

  // 북마크 토글
  toggleBookmark(postId) {
    return client.post(`/posts/${postId}/bookmark`);
  },
};
