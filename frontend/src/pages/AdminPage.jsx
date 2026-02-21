import { useState, useEffect } from 'react';
import { adminApi } from '../api/admin';

const TABS = [
  { key: 'hiddenPosts', label: '숨김 게시글' },
  { key: 'hiddenComments', label: '숨김 댓글' },
  { key: 'blockedUsers', label: '차단 회원' },
];

export default function AdminPage() {
  const [activeTab, setActiveTab] = useState('hiddenPosts');
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);

  const fetchData = () => {
    setLoading(true);
    const fetcher = {
      hiddenPosts: adminApi.getHiddenPosts,
      hiddenComments: adminApi.getHiddenComments,
      blockedUsers: adminApi.getBlockedUsers,
    };

    fetcher[activeTab]()
      .then((res) => setData(res.data.data))
      .catch(() => setData([]))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchData();
  }, [activeTab]);

  const handleRestorePost = async (postId) => {
    try {
      await adminApi.restorePost(postId);
      fetchData();
    } catch (err) {
      alert(err.response?.data?.message || '복원에 실패했습니다.');
    }
  };

  const handleDeletePost = async (postId) => {
    if (!window.confirm('정말 삭제하시겠습니까?')) return;
    try {
      await adminApi.deletePost(postId);
      fetchData();
    } catch (err) {
      alert(err.response?.data?.message || '삭제에 실패했습니다.');
    }
  };

  const handleRestoreComment = async (commentId) => {
    try {
      await adminApi.restoreComment(commentId);
      fetchData();
    } catch (err) {
      alert(err.response?.data?.message || '복원에 실패했습니다.');
    }
  };

  const handleDeleteComment = async (commentId) => {
    if (!window.confirm('정말 삭제하시겠습니까?')) return;
    try {
      await adminApi.deleteComment(commentId);
      fetchData();
    } catch (err) {
      alert(err.response?.data?.message || '삭제에 실패했습니다.');
    }
  };

  const handleUnblockUser = async (userId) => {
    try {
      await adminApi.unblockUser(userId);
      fetchData();
    } catch (err) {
      alert(err.response?.data?.message || '차단 해제에 실패했습니다.');
    }
  };

  return (
    <div className="page">
      <h1>관리자</h1>

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

      {loading ? (
        <div className="loading">로딩 중...</div>
      ) : data.length === 0 ? (
        <p className="empty">항목이 없습니다.</p>
      ) : activeTab === 'hiddenPosts' ? (
        <table className="table">
          <thead>
            <tr>
              <th>번호</th>
              <th>제목</th>
              <th>작성자</th>
              <th>작성일</th>
              <th>관리</th>
            </tr>
          </thead>
          <tbody>
            {data.map((post) => (
              <tr key={post.id}>
                <td>{post.id}</td>
                <td>{post.title}</td>
                <td>{post.authorNickname}</td>
                <td>{new Date(post.createdAt).toLocaleDateString()}</td>
                <td>
                  <button onClick={() => handleRestorePost(post.id)} className="btn btn-sm">
                    복원
                  </button>{' '}
                  <button onClick={() => handleDeletePost(post.id)} className="btn btn-sm btn-danger">
                    삭제
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : activeTab === 'hiddenComments' ? (
        <table className="table">
          <thead>
            <tr>
              <th>번호</th>
              <th>내용</th>
              <th>작성자</th>
              <th>작성일</th>
              <th>관리</th>
            </tr>
          </thead>
          <tbody>
            {data.map((comment) => (
              <tr key={comment.id}>
                <td>{comment.id}</td>
                <td>{comment.content}</td>
                <td>{comment.authorNickname}</td>
                <td>{new Date(comment.createdAt).toLocaleDateString()}</td>
                <td>
                  <button onClick={() => handleRestoreComment(comment.id)} className="btn btn-sm">
                    복원
                  </button>{' '}
                  <button onClick={() => handleDeleteComment(comment.id)} className="btn btn-sm btn-danger">
                    삭제
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : (
        <table className="table">
          <thead>
            <tr>
              <th>ID</th>
              <th>이메일</th>
              <th>닉네임</th>
              <th>관리</th>
            </tr>
          </thead>
          <tbody>
            {data.map((user) => (
              <tr key={user.id}>
                <td>{user.id}</td>
                <td>{user.email}</td>
                <td>{user.nickname}</td>
                <td>
                  <button onClick={() => handleUnblockUser(user.id)} className="btn btn-sm">
                    차단 해제
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
