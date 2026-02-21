import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { postApi } from '../api/posts';

const SEARCH_TYPES = [
  { value: 'TITLE', label: '제목' },
  { value: 'CONTENT', label: '내용' },
  { value: 'TITLE_CONTENT', label: '제목+내용' },
  { value: 'AUTHOR', label: '작성자' },
];

export default function HomePage() {
  const [posts, setPosts] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);

  // 검색
  const [searchType, setSearchType] = useState('TITLE');
  const [keyword, setKeyword] = useState('');
  const [isSearching, setIsSearching] = useState(false);
  const [activeKeyword, setActiveKeyword] = useState('');
  const [activeSearchType, setActiveSearchType] = useState('');

  const fetchPosts = () => {
    setLoading(true);
    const request = isSearching
      ? postApi.search(activeSearchType, activeKeyword, page)
      : postApi.getList(page);

    request
      .then((res) => {
        setPosts(res.data.data.content);
        setTotalPages(res.data.data.totalPages);
      })
      .catch(() => {
        setPosts([]);
        setTotalPages(0);
      })
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchPosts();
  }, [page, isSearching, activeKeyword, activeSearchType]);

  const handleSearch = (e) => {
    e.preventDefault();
    if (!keyword.trim()) return;
    setPage(0);
    setActiveKeyword(keyword);
    setActiveSearchType(searchType);
    setIsSearching(true);
  };

  const handleClearSearch = () => {
    setKeyword('');
    setActiveKeyword('');
    setActiveSearchType('');
    setIsSearching(false);
    setPage(0);
  };

  return (
    <div className="page">
      <h1>게시판</h1>

      {/* 검색 */}
      <form onSubmit={handleSearch} className="search-bar">
        <select value={searchType} onChange={(e) => setSearchType(e.target.value)}>
          {SEARCH_TYPES.map((t) => (
            <option key={t.value} value={t.value}>
              {t.label}
            </option>
          ))}
        </select>
        <input
          type="text"
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          placeholder="검색어를 입력하세요"
        />
        <button type="submit" className="btn btn-primary">
          검색
        </button>
        {isSearching && (
          <button type="button" onClick={handleClearSearch} className="btn">
            초기화
          </button>
        )}
      </form>

      {isSearching && (
        <p className="search-result-info">
          &quot;{activeKeyword}&quot; 검색 결과
        </p>
      )}

      {/* 게시글 목록 */}
      {loading ? (
        <div className="loading">로딩 중...</div>
      ) : (
        <div className="post-list">
          {posts.length === 0 ? (
            <p className="empty">게시글이 없습니다.</p>
          ) : (
            <table className="table">
              <thead>
                <tr>
                  <th>번호</th>
                  <th>카테고리</th>
                  <th>제목</th>
                  <th>작성자</th>
                  <th>조회</th>
                  <th>좋아요</th>
                  <th>작성일</th>
                </tr>
              </thead>
              <tbody>
                {posts.map((post) => (
                  <tr key={post.id}>
                    <td>{post.id}</td>
                    <td>{post.categoryName}</td>
                    <td>
                      <Link to={`/posts/${post.id}`}>
                        {post.title}
                        {post.commentCount > 0 && (
                          <span className="comment-count"> [{post.commentCount}]</span>
                        )}
                      </Link>
                    </td>
                    <td>{post.authorNickname}</td>
                    <td>{post.viewCount}</td>
                    <td>{post.likeCount}</td>
                    <td>{new Date(post.createdAt).toLocaleDateString()}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      )}

      {totalPages > 1 && (
        <div className="pagination">
          <button onClick={() => setPage(page - 1)} disabled={page === 0}>
            이전
          </button>
          <span>
            {page + 1} / {totalPages}
          </span>
          <button onClick={() => setPage(page + 1)} disabled={page >= totalPages - 1}>
            다음
          </button>
        </div>
      )}
    </div>
  );
}
