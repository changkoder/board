import { useState, useEffect } from 'react';
import { useParams, Link, Navigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { authApi } from '../api/auth';

export default function UserProfilePage() {
  const { userId } = useParams();
  const { user } = useAuth();
  const [profile, setProfile] = useState(null);
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [profileRes, postsRes] = await Promise.all([
          authApi.getUserProfile(userId),
          authApi.getUserPosts(userId),
        ]);
        setProfile(profileRes.data.data);
        setPosts(postsRes.data.data);
        setError(null);
      } catch (err) {
        const status = err.response?.status;
        if (status === 404) {
          setError('not_found');
        } else if (status === 400) {
          setError('bad_request');
        } else {
          setError('server');
        }
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [userId]);

  // 내 프로필이면 마이페이지로 이동
  if (user && String(user.id) === String(userId)) {
    return <Navigate to="/mypage" replace />;
  }

  if (loading) return <div className="loading">로딩 중...</div>;
  if (error === 'not_found' || error === 'bad_request') return (
    <div className="page">
      <div className="error-state">
        <p>존재하지 않는 사용자입니다.</p>
        <Link to="/" className="btn btn-primary" style={{ marginTop: '16px' }}>홈으로</Link>
      </div>
    </div>
  );
  if (error === 'server') return (
    <div className="page">
      <div className="error-state">
        <p>서버에 문제가 발생했습니다.</p>
        <p>잠시 후 다시 시도해주세요.</p>
        <button onClick={() => window.location.reload()} className="btn btn-primary" style={{ marginTop: '16px' }}>다시 시도</button>
      </div>
    </div>
  );
  if (!profile) return null;

  return (
    <div className="page">
      <div className="user-profile-header">
        <div className="user-profile-avatar">
          {profile.profileImg ? (
            <img src={profile.profileImg} alt={profile.nickname} />
          ) : (
            <div className="user-profile-placeholder">
              {profile.nickname.charAt(0)}
            </div>
          )}
        </div>
        <h1 className="user-profile-nickname">{profile.nickname}</h1>
      </div>

      <h2 className="user-profile-posts-title">작성한 글</h2>

      {posts.length === 0 ? (
        <p className="empty">작성한 글이 없습니다.</p>
      ) : (
        <table className="table">
          <thead>
            <tr>
              <th>번호</th>
              <th>제목</th>
              <th>카테고리</th>
              <th>조회</th>
              <th>좋아요</th>
              <th>작성일</th>
            </tr>
          </thead>
          <tbody>
            {posts.map((post) => (
              <tr key={post.id}>
                <td>{post.id}</td>
                <td>
                  <Link to={`/posts/${post.id}`}>
                    {post.title}
                    {post.commentCount > 0 && (
                      <span className="comment-count"> [{post.commentCount}]</span>
                    )}
                  </Link>
                </td>
                <td>{post.categoryName}</td>
                <td>{post.viewCount}</td>
                <td>{post.likeCount}</td>
                <td>{new Date(post.createdAt).toLocaleDateString()}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
