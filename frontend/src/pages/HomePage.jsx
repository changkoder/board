import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { postApi } from '../api/posts';
import { useAuth } from '../contexts/AuthContext';
import Pagination from '../components/Pagination';

const SEARCH_TYPES = [
  { value: 'TITLE', label: '제목' },
  { value: 'CONTENT', label: '내용' },
  { value: 'TITLE_CONTENT', label: '제목+내용' },
  { value: 'AUTHOR', label: '작성자' },
];

const CATEGORY_TABS = [
  { id: null, name: '전체' },
  { id: 2, name: '자유' },
  { id: 3, name: '질문' },
  { id: 4, name: '정보공유' },
];

export default function HomePage() {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  const [posts, setPosts] = useState([]);
  const [notices, setNotices] = useState([]);
  const [popularPosts, setPopularPosts] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const [categoryId, setCategoryId] = useState(null);

  // 검색
  const [searchType, setSearchType] = useState('TITLE');
  const [keyword, setKeyword] = useState('');
  const [isSearching, setIsSearching] = useState(false);
  const [activeKeyword, setActiveKeyword] = useState('');
  const [activeSearchType, setActiveSearchType] = useState('');
  const [error, setError] = useState(false);

  // 인기글 로드 (최초 1회)
  useEffect(() => {
    postApi
      .getPopular()
      .then((res) => setPopularPosts(res.data.data))
      .catch(() => setPopularPosts([]));
  }, []);

  const fetchPosts = () => {
    setLoading(true);

    let request;
    if (isSearching) {
      request = postApi.search(activeSearchType, activeKeyword, page);
    } else {
      request = postApi.getList(page, 10, categoryId);
    }

    request
      .then((res) => {
        setError(false);
        if (isSearching) {
          setPosts(res.data.data.content);
          setTotalPages(res.data.data.totalPages);
          setNotices([]);
        } else {
          setPosts(res.data.data.posts.content);
          setTotalPages(res.data.data.posts.totalPages);
          setNotices(res.data.data.notices || []);
        }
      })
      .catch(() => {
        setPosts([]);
        setNotices([]);
        setTotalPages(0);
        setError(true);
      })
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchPosts();
  }, [page, isSearching, activeKeyword, activeSearchType, categoryId]);

  const handleCategoryChange = (catId) => {
    setCategoryId(catId);
    setPage(0);
    if (isSearching) {
      setKeyword('');
      setActiveKeyword('');
      setActiveSearchType('');
      setIsSearching(false);
    }
  };

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

      {/* 인기글 */}
      {popularPosts.length > 0 && !isSearching && (
        <div className="popular-section">
          <h2 className="popular-title">인기글</h2>
          <div className="popular-list">
            {popularPosts.slice(0, 5).map((post, index) => (
              <Link
                key={post.id}
                to={`/posts/${post.id}`}
                className="popular-item"
              >
                <span className="popular-rank">{index + 1}</span>
                <span className="popular-post-title">{post.title}</span>
                <span className="popular-likes">{post.likeCount}</span>
              </Link>
            ))}
          </div>
        </div>
      )}

      {/* 카테고리 탭 */}
      {!isSearching && (
        <div className="category-tabs">
          {CATEGORY_TABS.map((cat) => (
            <button
              key={cat.id ?? 'all'}
              className={`category-tab ${categoryId === cat.id ? 'category-tab-active' : ''}`}
              onClick={() => handleCategoryChange(cat.id)}
            >
              {cat.name}
            </button>
          ))}
        </div>
      )}

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
      ) : error ? (
        <div className="error-state">
          <p>서버에 문제가 발생했습니다.</p>
          <p>잠시 후 다시 시도해주세요.</p>
          <button onClick={fetchPosts} className="btn btn-primary" style={{ marginTop: '16px' }}>
            다시 시도
          </button>
        </div>
      ) : (
        <div className="post-list">
          {notices.length === 0 && posts.length === 0 ? (
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
                {notices.map((post) => (
                  <tr key={`notice-${post.id}`} className="notice-row">
                    <td>
                      <span className="notice-badge">공지</span>
                    </td>
                    <td>{post.categoryName}</td>
                    <td>
                      <Link to={`/posts/${post.id}`}>
                        <strong>{post.title}</strong>
                        {post.commentCount > 0 && (
                          <span className="comment-count"> [{post.commentCount}]</span>
                        )}
                      </Link>
                    </td>
                    <td>
                      <span
                        className={`author-cell${post.authorNickname !== '(탈퇴한 사용자)' ? ' nickname-link' : ''}`}
                        onClick={post.authorNickname !== '(탈퇴한 사용자)' ? () => navigate(`/users/${post.authorId}`) : undefined}
                      >
                        <span className="inline-avatar">
                          {post.authorProfileImg ? (
                            <img src={post.authorProfileImg} alt="" />
                          ) : (
                            <span className="inline-avatar-placeholder">{post.authorNickname === '(탈퇴한 사용자)' ? '?' : post.authorNickname?.charAt(0)}</span>
                          )}
                        </span>
                        {post.authorNickname}
                      </span>
                    </td>
                    <td>{post.viewCount}</td>
                    <td>{post.likeCount}</td>
                    <td>{new Date(post.createdAt).toLocaleDateString()}</td>
                  </tr>
                ))}
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
                    <td>
                      <span
                        className={`author-cell${post.authorNickname !== '(탈퇴한 사용자)' ? ' nickname-link' : ''}`}
                        onClick={post.authorNickname !== '(탈퇴한 사용자)' ? () => navigate(`/users/${post.authorId}`) : undefined}
                      >
                        <span className="inline-avatar">
                          {post.authorProfileImg ? (
                            <img src={post.authorProfileImg} alt="" />
                          ) : (
                            <span className="inline-avatar-placeholder">{post.authorNickname === '(탈퇴한 사용자)' ? '?' : post.authorNickname?.charAt(0)}</span>
                          )}
                        </span>
                        {post.authorNickname}
                      </span>
                    </td>
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

      <div className="list-footer">
        <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
        {isAuthenticated && (
          <Link to="/posts/new" className="btn btn-primary">글쓰기</Link>
        )}
      </div>
    </div>
  );
}
