import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { useToast } from '../contexts/ToastContext';
import { authApi } from '../api/auth';

const TABS = [
  { key: 'posts', label: '내가 쓴 글' },
  { key: 'comments', label: '내 댓글' },
  { key: 'likes', label: '좋아요한 글' },
  { key: 'bookmarks', label: '북마크' },
];

export default function MyPage() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const { showToast } = useToast();
  const [activeTab, setActiveTab] = useState('posts');
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);

  // 프로필 수정
  const [editing, setEditing] = useState(false);
  const [nickname, setNickname] = useState(user?.nickname || '');

  // 비밀번호 변경
  const [showPasswordForm, setShowPasswordForm] = useState(false);
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');

  useEffect(() => {
    setLoading(true);

    const fetcher = {
      posts: authApi.getMyPosts,
      comments: authApi.getMyComments,
      likes: authApi.getMyLikes,
      bookmarks: authApi.getMyBookmarks,
    };

    fetcher[activeTab]()
      .then((res) => setData(res.data.data))
      .catch(() => setData([]))
      .finally(() => setLoading(false));
  }, [activeTab]);

  const handleUpdateNickname = async () => {
    try {
      await authApi.updateMe(nickname, null);
      showToast('닉네임이 변경되었습니다.', 'success');
      setEditing(false);
    } catch (err) {
      showToast(err.response?.data?.message || '닉네임 변경에 실패했습니다.', 'error');
    }
  };

  const handleChangePassword = async (e) => {
    e.preventDefault();
    try {
      await authApi.changePassword(currentPassword, newPassword);
      showToast('비밀번호가 변경되었습니다.', 'success');
      setShowPasswordForm(false);
      setCurrentPassword('');
      setNewPassword('');
    } catch (err) {
      showToast(err.response?.data?.message || '비밀번호 변경에 실패했습니다.', 'error');
    }
  };

  const handleDeleteAccount = async () => {
    if (!window.confirm('정말 탈퇴하시겠습니까? 이 작업은 되돌릴 수 없습니다.')) return;
    try {
      await authApi.deleteAccount();
      await logout();
      navigate('/');
    } catch (err) {
      showToast(err.response?.data?.message || '회원 탈퇴에 실패했습니다.', 'error');
    }
  };

  return (
    <div className="page">
      <h1>마이페이지</h1>

      {/* 프로필 */}
      <div className="profile-card">
        <div className="profile-info">
          <p>
            <strong>이메일:</strong> {user.email}
          </p>
          <p>
            <strong>닉네임:</strong>{' '}
            {editing ? (
              <span className="inline-edit">
                <input
                  type="text"
                  value={nickname}
                  onChange={(e) => setNickname(e.target.value)}
                  minLength={2}
                  maxLength={20}
                />
                <button onClick={handleUpdateNickname} className="btn btn-primary btn-sm">
                  저장
                </button>
                <button onClick={() => setEditing(false)} className="btn btn-sm">
                  취소
                </button>
              </span>
            ) : (
              <span>
                {user.nickname}{' '}
                <button onClick={() => setEditing(true)} className="btn btn-sm">
                  수정
                </button>
              </span>
            )}
          </p>
        </div>

        <div className="profile-actions">
          <button
            onClick={() => setShowPasswordForm(!showPasswordForm)}
            className="btn btn-sm"
          >
            비밀번호 변경
          </button>
          {user.role !== 'ADMIN' && (
            <button onClick={handleDeleteAccount} className="btn btn-sm btn-danger">
              회원 탈퇴
            </button>
          )}
        </div>

        {showPasswordForm && (
          <form onSubmit={handleChangePassword} className="password-form">
            <div className="form-group">
              <label>현재 비밀번호</label>
              <input
                type="password"
                value={currentPassword}
                onChange={(e) => setCurrentPassword(e.target.value)}
                required
              />
            </div>
            <div className="form-group">
              <label>새 비밀번호</label>
              <input
                type="password"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                minLength={8}
                maxLength={20}
                required
              />
              <small>8~20자</small>
            </div>
            <button type="submit" className="btn btn-primary btn-sm">
              변경
            </button>
          </form>
        )}
      </div>

      {/* 탭 */}
      <div className="tabs">
        {TABS.map((tab) => (
          <button
            key={tab.key}
            className={`tab ${activeTab === tab.key ? 'tab-active' : ''}`}
            onClick={() => setActiveTab(tab.key)}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {/* 탭 내용 */}
      <div className="tab-content">
        {loading ? (
          <div className="loading">로딩 중...</div>
        ) : data.length === 0 ? (
          <p className="empty">항목이 없습니다.</p>
        ) : activeTab === 'comments' ? (
          <ul className="comment-list">
            {data.map((comment) => (
              <li key={comment.id} className="comment-item">
                <p>{comment.content}</p>
                <span className="comment-meta">
                  {new Date(comment.createdAt).toLocaleDateString()}
                </span>
              </li>
            ))}
          </ul>
        ) : (
          <table className="table">
            <thead>
              <tr>
                <th>번호</th>
                <th>제목</th>
                <th>조회</th>
                <th>좋아요</th>
                <th>작성일</th>
              </tr>
            </thead>
            <tbody>
              {data.map((post) => (
                <tr key={post.id}>
                  <td>{post.id}</td>
                  <td>
                    <Link to={`/posts/${post.id}`}>{post.title}</Link>
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
    </div>
  );
}
